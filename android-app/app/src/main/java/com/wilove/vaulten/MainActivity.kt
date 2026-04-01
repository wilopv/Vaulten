package com.wilove.vaulten

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.wilove.vaulten.data.local.TokenManager
import com.wilove.vaulten.ui.navigation.VaultenDestinations
import com.wilove.vaulten.ui.navigation.VaultenNavGraph
import com.wilove.vaulten.ui.theme.VaultenTheme

/**
 * Main activity and entry point for the Vaulten password manager app.
 * Sets up the navigation graph and theme.
 *
 * When launched from the autofill fallback, the intent may carry:
 * - [EXTRA_START_DESTINATION]: the route to open directly (login or credentials_list)
 * - [EXTRA_SEARCH_QUERY]: a pre-filled search term for the credentials list
 */
class MainActivity : ComponentActivity() {

    companion object {
        /** Route to start at when launched from the autofill fallback. */
        const val EXTRA_START_DESTINATION = "autofill_start_destination"
        /** Pre-filled search query to apply when opening the credentials list. */
        const val EXTRA_SEARCH_QUERY = "autofill_search_query"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupContent(intent)
    }

    /**
     * Called when the activity is already running and a new intent is delivered
     * (e.g. user taps "Buscar en Vaulten" while Vaulten is in the back stack).
     * We update the stored intent and recreate so the new start destination applies.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recreate()
    }

    /**
     * Save the current timestamp when the app goes to background so that
     * [SessionManager] can decide whether to show the lock screen on resume.
     * Only saved when the user is logged in (token present).
     */
    override fun onStop() {
        super.onStop()
        val tokenManager = TokenManager(this)
        if (tokenManager.getToken() != null) {
            tokenManager.saveLastActiveTimestamp(System.currentTimeMillis())
        }
    }

    private fun setupContent(intent: Intent) {
        val startDestination = intent.getStringExtra(EXTRA_START_DESTINATION)
            ?: VaultenDestinations.LOGIN
        val searchQuery = intent.getStringExtra(EXTRA_SEARCH_QUERY)

        setContent {
            VaultenTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VaultenNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        startDestination = startDestination,
                        initialSearchQuery = searchQuery
                    )
                }
            }
        }
    }
}
