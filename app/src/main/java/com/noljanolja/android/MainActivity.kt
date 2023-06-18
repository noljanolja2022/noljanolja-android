package com.noljanolja.android

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.d2brothers.firebase_auth.AuthSdk
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.noljanolja.android.common.base.launchInMainIO
import com.noljanolja.android.common.mobiledata.data.ContactsLoader
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.ui.theme.NoljanoljaTheme
import com.noljanolja.core.CoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val navigationManager: NavigationManager by inject()

    private val coreManager: CoreManager by inject()
    private val contactsLoader: ContactsLoader by inject()
    private val authSdk: AuthSdk by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        syncContacts()
        setContent {
            RequestPermissions() {
                if (it[android.Manifest.permission.READ_CONTACTS] == true) {
                    syncContacts()
                }
            }

            NoljanoljaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    MainScreen(navigationManager)
                }
            }
        }
    }

    private fun syncContacts() {
        launchIfLogin {
            withContext(Dispatchers.IO) {
                val loadedContacts = contactsLoader.loadContacts().toList()
                coreManager.syncUserContacts(loadedContacts)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lifecycleScope.launch {
            navigationManager.navigate(
                NavigationDirections.Chat(
                    conversationId = intent.getConversationId()
                )
            )
        }
    }

    fun launchIfLogin(block: suspend () -> Unit) = launchInMainIO {
        authSdk.getIdToken(false)?.takeIf { it.isNotBlank() } ?: return@launchInMainIO
        block.invoke()
    }

    companion object {
        const val EXTRA_CONVERSATION_ID = "conversationId"

        fun getIntent(
            context: Context,
            conversationId: String,
        ): Intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_CONVERSATION_ID, conversationId)
        }

        fun getPendingIntent(
            context: Context,
            conversationId: String,
        ): PendingIntent = with(getIntent(context, conversationId)) {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            PendingIntent.getActivity(context, 0, this, flags)
        }

        fun Intent.getConversationId(): Long {
            return extras?.getString(EXTRA_CONVERSATION_ID)?.toLong() ?: 0
        }

        fun Intent.removeConversationId() {
            removeExtra(EXTRA_CONVERSATION_ID)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestPermissions(onPermissionsResult: (Map<String, Boolean>) -> Unit = {}) {
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOfNotNull(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android.Manifest.permission.POST_NOTIFICATIONS
            } else {
                null
            },
            android.Manifest.permission.READ_CONTACTS,
        ),
        onPermissionsResult = onPermissionsResult
    )
    LaunchedEffect(key1 = true) {
        if (!multiplePermissionsState.allPermissionsGranted) {
            multiplePermissionsState.launchMultiplePermissionRequest()
        }
    }
}
