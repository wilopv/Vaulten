package com.wilove.vaulten.ui.credential

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.wilove.vaulten.data.local.VaultDatabase
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.provider.PendingIntentHandler
import kotlinx.coroutines.launch

/**
 * Transparent background Activity launched by a PendingIntent from
 * VaultenCredentialProviderService. Reads the selected credential from the
 * Room database and immediately returns it to the Android CredentialManager,
 * then finishes without showing any visible UI.
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class CredentialFillActivity : ComponentActivity() {

    companion object {
        const val EXTRA_CREDENTIAL_ID = "vaulten_credential_id"
        private const val TAG = "CredentialFillActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val credentialId = intent.getStringExtra(EXTRA_CREDENTIAL_ID)
        if (credentialId == null) {
            Log.e(TAG, "No credential ID in intent")
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        lifecycleScope.launch {
            val cred = VaultDatabase
                .getInstance(applicationContext)
                .vaultDao()
                .getCredentialById(credentialId)

            if (cred == null) {
                Log.e(TAG, "Credential $credentialId not found in DB")
                setResult(Activity.RESULT_CANCELED)
                finish()
                return@launch
            }

            Log.d(TAG, "Returning credential for: ${cred.username}")

            val resultData = Intent()
            PendingIntentHandler.setGetCredentialResponse(
                resultData,
                GetCredentialResponse(
                    PasswordCredential(cred.username, cred.password)
                )
            )
            setResult(Activity.RESULT_OK, resultData)
            finish()
        }
    }
}
