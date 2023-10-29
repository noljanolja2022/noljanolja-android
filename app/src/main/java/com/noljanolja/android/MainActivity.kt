package com.noljanolja.android

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import co.touchlab.kermit.Logger
import com.d2brothers.firebase_auth.AuthSdk
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.noljanolja.android.common.base.launchInMain
import com.noljanolja.android.common.base.launchInMainIO
import com.noljanolja.android.common.mobiledata.data.ContactsLoader
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.network.ConnectivityObserver
import com.noljanolja.android.common.network.NetworkConnectivityObserver
import com.noljanolja.android.ui.theme.NoljanoljaTheme
import com.noljanolja.core.CoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.Arrays

class MainActivity : ComponentActivity() {
    private val navigationManager: NavigationManager by inject()

    private val coreManager: CoreManager by inject()
    private val contactsLoader: ContactsLoader by inject()
    private val authSdk: AuthSdk by inject()
    private lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("5ECA40C7C6D7F5F65594DF7F55AC5E83")).build()
        )
        connectivityObserver = NetworkConnectivityObserver(this)
        syncContacts()
        Logger.d("DeviceId ${getDeviceId(this)}")
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
        launchInMain {
            connectivityObserver.observe().collect {
                when (it) {
                    ConnectivityObserver.Status.Available -> {
                        coreManager.fetchConversations()
                    }

                    else -> Unit
                }
            }
        }
        onNewIntent(intent)
        subscribeVideoTopic()
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
            while (!MyApplication.isHomeShowed) {
                delay(100)
            }
            with(intent) {
                if (getConversationId() > 0L) {
                    navigationManager.navigate(
                        NavigationDirections.Chat(
                            conversationId = getConversationId()
                        )
                    )
                }
                if (!getVideoId().isNullOrBlank()) {
                    navigationManager.navigate(
                        NavigationDirections.PlayScreen(
                            videoId = getVideoId()!!,
                            isInPipMode = true
                        )
                    )
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = maxOf(newOverride.fontScale, 1.2f)
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }

    fun launchIfLogin(block: suspend () -> Unit) = launchInMainIO {
        authSdk.getIdToken(false)?.takeIf { it.isNotBlank() } ?: return@launchInMainIO
        block.invoke()
    }

    private fun subscribeVideoTopic() {
        val topic = "/topics/promote-video"
        Firebase.messaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Logger.d("$msg $topic")
            }
    }

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    companion object {
        private const val EXTRA_CONVERSATION_ID = "conversationId"
        private const val EXTRA_VIDEO_ID = "videoId"
        private fun getIntent(
            context: Context,
            conversationId: String,
            videoId: String,
        ): Intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_CONVERSATION_ID, conversationId)
            putExtra(EXTRA_VIDEO_ID, videoId)
        }

        fun getPendingIntent(
            context: Context,
            conversationId: String,
            videoId: String,
        ): PendingIntent = with(getIntent(context, conversationId, videoId)) {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            PendingIntent.getActivity(context, 0, this, flags)
        }

        fun Intent.getConversationId(): Long {
            return extras?.getString(EXTRA_CONVERSATION_ID)?.toLongOrNull() ?: 0
        }

        fun Intent.getVideoId(): String? {
            return extras?.getString(EXTRA_VIDEO_ID)
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
