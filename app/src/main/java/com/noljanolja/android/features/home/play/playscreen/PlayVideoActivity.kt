package com.noljanolja.android.features.home.play.playscreen

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import co.touchlab.kermit.Logger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.noljanolja.VideoBroadcastReceiver
import com.noljanolja.android.MyApplication
import com.noljanolja.android.R
import com.noljanolja.android.features.home.play.playscreen.composable.getAccount
import com.noljanolja.android.features.home.play.playscreen.composable.getTokenFromAccount
import com.noljanolja.android.ui.composable.youtube.YoutubeViewWithFullScreen
import com.noljanolja.android.ui.theme.NoljanoljaTheme
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayVideoActivity : ComponentActivity() {

    private val isInPipMode = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoId = intent.getVideoId()
        val autoAction = intent.autoAction()
        val viewModel: VideoDetailViewModel = getViewModel { parametersOf(videoId) }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestIdToken(getString(R.string.web_client_id))
            .requestScopes(YOUTUBE_FORCE_SCOPE, YOUTUBE_SCOPE, YOUTUBE_PARTNER_SCOPE)
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        viewModel.updateVideo(
            videoId,
        )
        val onTokenResult = { token: String ->
            Logger.d("Request scope success $token")
            viewModel.handleEvent(VideoDetailEvent.AutoAction(token))
        }
        val onError: (Throwable) -> Unit = { error: Throwable ->
            Logger.e("Request scope error $error")
        }
        YoutubeViewWithFullScreen.release()
        MyApplication.backStackActivities.add(this)
        setContent {
            val googleSignInLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { result ->
                        lifecycleScope.launch {
                            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                            val account = task.getAccount(onError) ?: return@launch
                            getTokenFromAccount(
                                this@PlayVideoActivity,
                                account,
                                onError = { error ->
                                    onError(error)
                                },
                                onTokenResult = onTokenResult
                            )
                        }
                    }
                )
            val requestYoutubeScope = suspend {
                val account = GoogleSignIn.getLastSignedInAccount(this)
                if (account != null) {
                    val newAccount = if (account.isExpired) {
                        googleSignInClient.silentSignIn().getAccount(onError)
                    } else {
                        account
                    }
                    if (newAccount != null) {
                        getTokenFromAccount(
                            this,
                            newAccount,
                            onError = { error ->
                                onError(error)
                            },
                            onTokenResult = onTokenResult
                        )
                    }
                } else {
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                }
            }
            LaunchedEffect(autoAction) {
                requestYoutubeScope.invoke()
            }
            NoljanoljaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    VideoDetailScreen(
                        isPipMode = isInPipMode.value,
                        viewModel = viewModel,
                        onBack = {
                            enterPip()
                        }
                    )
                }
            }
        }
        lifecycleScope.launch {
            viewModel.playerStateFlow.collectLatest {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val params = PictureInPictureParams.Builder()
                        .setActions(listOf(createCustomActions(this@PlayVideoActivity, it)))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        params.setSeamlessResizeEnabled(true)
                    }
                    setPictureInPictureParams(params.build())
                }
            }
        }
        if (intent.isInPictureInPictureMode()) {
            enterPip()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration,
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipMode.value = isInPictureInPictureMode
    }

    private fun enterPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            enterPictureInPictureMode()
        }
    }

    companion object {

        private val YOUTUBE_FORCE_SCOPE = Scope("https://www.googleapis.com/auth/youtube.force-ssl")
        private val YOUTUBE_SCOPE = Scope("https://www.googleapis.com/auth/youtube")
        private val YOUTUBE_PARTNER_SCOPE = Scope("https://www.googleapis.com/auth/youtubepartner")
        private fun Intent.getVideoId() = extras?.getString("videoId").orEmpty()
        private fun Intent.isInPictureInPictureMode() =
            extras?.getBoolean("isInPictureInPictureMode") ?: false

        private fun Intent.autoAction() = extras?.getBoolean("autoAction") ?: false

        fun createIntent(
            activity: Context,
            videoId: String,
            isInPictureInPictureMode: Boolean,
            autoAction: Boolean,
        ) =
            Intent(activity, PlayVideoActivity::class.java).apply {
                putExtra("videoId", videoId)
                putExtra("isInPictureInPictureMode", isInPictureInPictureMode)
                putExtra("autoAction", autoAction)
            }

        @RequiresApi(Build.VERSION_CODES.O)
        fun createCustomActions(
            context: Context,
            playerState: PlayerConstants.PlayerState,
        ): RemoteAction {
            val iconId = if (playerState == PlayerConstants.PlayerState.PAUSED) {
                R.drawable.ic_play
            } else {
                R.drawable.ic_pause
            }
            val requestCode = if (playerState == PlayerConstants.PlayerState.PAUSED) {
                0
            } else {
                1
            }
            val intent = Intent(context, VideoBroadcastReceiver::class.java)
            intent.putExtra("play", true)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            return RemoteAction(
                Icon.createWithResource(context, iconId),
                "",
                "",
                pendingIntent
            )
        }
    }
}