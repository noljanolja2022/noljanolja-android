package com.noljanolja.android

import android.annotation.*
import android.app.*
import android.content.*
import android.content.res.*
import android.os.*
import android.provider.*
import android.util.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.*
import co.touchlab.kermit.*
import com.d2brothers.firebase_auth.*
import com.google.accompanist.permissions.*
import com.google.android.gms.ads.*
import com.google.android.gms.tasks.*
import com.google.firebase.ktx.*
import com.google.firebase.messaging.*
import com.google.firebase.messaging.ktx.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.enums.*
import com.noljanolja.android.common.mobiledata.data.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.android.common.network.*
import com.noljanolja.android.common.sharedpreference.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.*


class MainActivity : ComponentActivity() {
    private val navigationManager: NavigationManager by inject()

    private val coreManager: CoreManager by inject()
    private val contactsLoader: ContactsLoader by inject()
    private val sharedPreferenceHelper: SharedPreferenceHelper by inject()
    private val authSdk: AuthSdk by inject()
    private var appColorId = mutableStateOf(EAppColorSetting.KEY_DEFAULT_COLOR)
    private lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("5ECA40C7C6D7F5F65594DF7F55AC5E83")).build()
        )
        connectivityObserver = NetworkConnectivityObserver(this)
        syncContacts()
        Logger.d("DeviceId ${getDeviceId(this)}")
        appColorId.value = sharedPreferenceHelper.appColor
        getFirebaseToken()
        setContent {
            val appColorSettingKey by remember {
                appColorId
            }

            RequestPermissions {
                if (it[android.Manifest.permission.READ_CONTACTS] == true) {
                    syncContacts()
                }
            }

            NoljanoljaTheme(
                appColorSetting = EAppColorSetting.getColorByKey(appColorSettingKey)
            ) {
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

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TTT", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result

            launchInMain {
                coreManager.pushTokens(token)
            }
        })
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

    internal fun setAppColorId(key: Int) {
        appColorId.value = key
    }

    private fun launchIfLogin(block: suspend () -> Unit) = launchInMainIO {
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

    @SuppressLint("HardwareIds")
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
