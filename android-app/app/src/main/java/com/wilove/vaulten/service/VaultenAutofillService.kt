package com.wilove.vaulten.service

import android.app.assist.AssistStructure
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.content.Intent
import android.app.PendingIntent
import android.util.Log
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.wilove.vaulten.MainActivity
import com.wilove.vaulten.R
import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.data.local.VaultDatabase
import com.wilove.vaulten.data.local.entity.VaultEntity
import com.wilove.vaulten.ui.navigation.VaultenDestinations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val TAG = "VaultenAutofill"


@RequiresApi(Build.VERSION_CODES.O)
class VaultenAutofillService : AutofillService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onDisconnected() {
        super.onDisconnected()
        Log.d(TAG, "onDisconnected")
    }

    // ─── onFillRequest ───────────────────────────────────────────────────────

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val structure = request.fillContexts.lastOrNull()?.structure
        if (structure == null) {
            callback.onSuccess(null)
            return
        }

        val packageName = structure.activityComponent?.packageName ?: "unknown"

        scope.launch {
            try {
                // 1. Walk the view tree
                val parsed = parseStructure(structure)

                // No autofill-able fields found → nothing to do
                if (parsed.usernameId == null && parsed.passwordId == null) {
                    callback.onSuccess(null)
                    return@launch
                }

                val dao = VaultDatabase.getInstance(applicationContext).vaultDao()

                // 2. Search credentials:
                //    - Browser: search by webDomain, then fallback to packageName
                //    - Native app: extract brand keywords from package name and search each
                val results: List<VaultEntity>
                val fallbackSearchQuery: String

                if (parsed.webDomain != null) {
                    // Browser: search by web domain
                    fallbackSearchQuery = parsed.webDomain
                    results = dao.searchCredentials(parsed.webDomain)
                } else {
                    // Native app: exact androidPackageName match only
                    fallbackSearchQuery = packageName
                    results = dao.getCredentialsByPackageName(packageName)
                }

                // 3. No credentials found → show "Buscar en Vaulten" fallback
                if (results.isEmpty()) {
                    // Route to credentials list if the user already has a session,
                    // otherwise send them to login.
                    val hasSession = try {
                        TokenManager(applicationContext).getToken() != null
                    } catch (e: Exception) {
                        false
                    }

                    val destination = if (hasSession) {
                        VaultenDestinations.CREDENTIALS_LIST
                    } else {
                        VaultenDestinations.LOGIN
                    }

                    val intent = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra(MainActivity.EXTRA_START_DESTINATION, destination)
                        putExtra(MainActivity.EXTRA_SEARCH_QUERY, fallbackSearchQuery)
                    }
                    val pendingIntent = PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    val presentation = RemoteViews(
                        this@VaultenAutofillService.packageName,
                        R.layout.autofill_suggestion
                    ).apply {
                        setTextViewText(R.id.text1, "🔑 Buscar en Vaulten")
                    }

                    val ids = listOfNotNull(parsed.usernameId, parsed.passwordId).toTypedArray()
                    val rb = FillResponse.Builder()
                    if (ids.isNotEmpty()) {
                        rb.setAuthentication(ids, pendingIntent.intentSender, presentation)
                    }

                    if (!cancellationSignal.isCanceled) {
                        callback.onSuccess(rb.build())
                    }
                    return@launch
                }

                // 4. Build fill response with the matching credentials
                val responseBuilder = FillResponse.Builder()

                for (cred in results) {
                    val label = "${cred.name} (${cred.username})"
                    val presentation = RemoteViews(
                        this@VaultenAutofillService.packageName,
                        R.layout.autofill_suggestion
                    ).apply {
                        setTextViewText(R.id.text1, "🔑 $label")
                    }

                    // Dataset.Builder(RemoteViews) is available from API 26.
                    // Dataset.Builder() no-arg constructor requires API 28 — avoid it.
                    @Suppress("DEPRECATION")
                    val dataset = Dataset.Builder(presentation)

                    parsed.usernameId?.let { id ->
                        dataset.setValue(id, AutofillValue.forText(cred.username))
                    }
                    parsed.passwordId?.let { id ->
                        dataset.setValue(id, AutofillValue.forText(cred.password))
                    }

                    responseBuilder.addDataset(dataset.build())
                }

                if (!cancellationSignal.isCanceled) {
                    callback.onSuccess(responseBuilder.build())
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error in onFillRequest", e)
                callback.onSuccess(null)
            }
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        callback.onSuccess()
    }

    // ─── Structure parsing ─────────────────────────────────────────────────

    private data class ParsedStructure(
        val usernameId: AutofillId?,
        val passwordId: AutofillId?,
        val webDomain: String?
    )

    private fun parseStructure(structure: AssistStructure): ParsedStructure {
        var usernameId: AutofillId? = null
        var passwordId: AutofillId? = null
        var webDomain: String? = null

        val queue = ArrayDeque<AssistStructure.ViewNode>()
        for (i in 0 until structure.windowNodeCount) {
            queue.add(structure.getWindowNodeAt(i).rootViewNode)
        }

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            Log.v(TAG, "Parsing node: id=${node.idEntry} hint=${node.hint} class=${node.className} type=${node.inputType}")

            // Capture the web domain from any node that has it
            if (webDomain == null) {
                node.webDomain?.takeIf { it.isNotBlank() }?.let {
                    webDomain = it
                        .removePrefix("https://")
                        .removePrefix("http://")
                        .split("/").first()
                    Log.d(TAG, "webDomain = $webDomain")
                }
            }

            // Detect field type from multiple signals
            val hints = node.autofillHints?.map { it.lowercase() } ?: emptyList()
            val viewId = node.idEntry?.lowercase() ?: ""
            val hint   = node.hint?.toString()?.lowercase() ?: ""
            val inputType = node.inputType

            // Extract HTML Info for web forms (Chrome, WebViews)
            var isPasswordType = false
            var isEmailType = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val attrs = node.htmlInfo?.attributes ?: emptyList()
                attrs.forEach { attr ->
                    val key = attr.first?.lowercase() ?: ""
                    val value = attr.second?.lowercase() ?: ""
                    if (key == "type" && value == "password") isPasswordType = true
                    if (key == "type" && value == "email") isEmailType = true
                    if ((key == "name" || key == "id") && (value.contains("user") || value.contains("email") || value.contains("login"))) {
                        isEmailType = true
                    }
                    if ((key == "name" || key == "id") && (value.contains("pass") || value.contains("pwd"))) {
                        isPasswordType = true
                    }
                }
            }

            val looksLikeUsername = hints.any { "username" in it || "email" in it || "user" in it || "login" in it }
                || "user" in viewId || "email" in viewId || "login" in viewId
                || "user" in hint  || "email" in hint
                || (inputType and 0xFF) == 0x21
                || isEmailType

            val looksLikePassword = hints.any { "password" in it || "pass" in it }
                || "pass" in viewId || "pwd" in viewId
                || "pass" in hint
                || (inputType and 0xFF) == 0x81
                || (inputType and 0xFF) == 0x91
                || isPasswordType

            if ((looksLikeUsername || viewId.isNotEmpty()) && usernameId == null && !looksLikePassword) {
                if (looksLikeUsername) {
                    usernameId = node.autofillId
                    Log.d(TAG, "Found Username field: id=${node.idEntry} hints=${node.autofillHints?.joinToString()}")
                }
            }

            if (looksLikePassword && passwordId == null) {
                passwordId = node.autofillId
                Log.d(TAG, "Found Password field: id=${node.idEntry} hints=${node.autofillHints?.joinToString()}")
            }

            for (i in 0 until node.childCount) {
                queue.add(node.getChildAt(i))
            }
        }

        return ParsedStructure(usernameId, passwordId, webDomain)
    }
}
