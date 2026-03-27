package com.wilove.vaulten.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.CancellationSignal
import android.os.OutcomeReceiver
import android.util.Log
import android.graphics.drawable.Icon
import androidx.annotation.RequiresApi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// --- Jetpack Credentials imports ---
import androidx.credentials.provider.BeginGetCredentialRequest
import androidx.credentials.provider.BeginGetCredentialResponse
import androidx.credentials.provider.BeginGetPasswordOption
import androidx.credentials.provider.CredentialEntry
import androidx.credentials.provider.CredentialProviderService
import androidx.credentials.provider.PasswordCredentialEntry
import androidx.credentials.provider.BeginCreateCredentialRequest
import androidx.credentials.provider.BeginCreateCredentialResponse
import androidx.credentials.provider.CreateEntry
import androidx.credentials.provider.ProviderClearCredentialStateRequest

import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialUnknownException

import com.wilove.vaulten.data.local.VaultDatabase
import com.wilove.vaulten.data.local.entity.VaultEntity
import com.wilove.vaulten.ui.credential.CredentialFillActivity

private const val TAG = "VaultenCredProvider"

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class VaultenCredentialProviderService : CredentialProviderService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // -------------------------------
    // ON BEGIN GET CREDENTIAL
    // -------------------------------
    override fun onBeginGetCredentialRequest(
        request: BeginGetCredentialRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<BeginGetCredentialResponse, GetCredentialException>
    ) {
        val callingApp = request.callingAppInfo?.packageName ?: "unknown"
        Log.d(TAG, "onBeginGetCredentialRequest: app=$callingApp, options=${request.beginGetCredentialOptions.size}")

        scope.launch {
            try {
                val dao = VaultDatabase.getInstance(applicationContext).vaultDao()
                val allCredentials = dao.searchCredentials("")

                val credentialEntries = mutableListOf<CredentialEntry>()

                for (option in request.beginGetCredentialOptions) {
                    if (option is BeginGetPasswordOption) {
                        for (cred in allCredentials) {
                            credentialEntries.add(buildCredentialEntry(cred, option))
                        }
                    }
                }

                val response = BeginGetCredentialResponse(credentialEntries)
                Log.d(TAG, "onBeginGetCredentialRequest: returning ${credentialEntries.size} entries")
                callback.onResult(response)

            } catch (e: Exception) {
                Log.e(TAG, "Error in onBeginGetCredential", e)
                callback.onError(GetCredentialUnknownException(e.message))
            }
        }
    }

    private fun buildCredentialEntry(
        cred: VaultEntity,
        option: BeginGetPasswordOption
    ): CredentialEntry {
        val fillIntent = Intent(applicationContext, CredentialFillActivity::class.java).apply {
            putExtra(CredentialFillActivity.EXTRA_CREDENTIAL_ID, cred.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            cred.id.hashCode(),
            fillIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return PasswordCredentialEntry.Builder(
            applicationContext,
            cred.username,
            pendingIntent,
            option
        )
            .setDisplayName(cred.name)
            .build()
    }

    // -------------------------------
    // ON BEGIN CREATE CREDENTIAL
    // -------------------------------
    override fun onBeginCreateCredentialRequest(
        request: BeginCreateCredentialRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<BeginCreateCredentialResponse, CreateCredentialException>
    ) {
        val callingApp = request.callingAppInfo?.packageName ?: "unknown"
        Log.d(TAG, "onBeginCreateCredentialRequest: app=$callingApp")

        // Create an entry that allows the user to "Save to Vaulten"
        val fillIntent = Intent(applicationContext, CredentialFillActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            fillIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val createEntry = CreateEntry.Builder("Vaulten", pendingIntent)
            .setDescription("Guardar en mi bóveda segura")
            .setIcon(Icon.createWithResource(applicationContext, com.wilove.vaulten.R.mipmap.ic_launcher))
            .build()

        val response = BeginCreateCredentialResponse.Builder()
            .addCreateEntry(createEntry)
            .build()
        
        Log.d(TAG, "onBeginCreateCredentialRequest: returning Save entry")
        callback.onResult(response)
    }

    // -------------------------------
    // ON CLEAR CREDENTIAL STATE
    // -------------------------------
    override fun onClearCredentialStateRequest(
        request: ProviderClearCredentialStateRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<Void?, ClearCredentialException>
    ) {
        callback.onResult(null)
    }
}