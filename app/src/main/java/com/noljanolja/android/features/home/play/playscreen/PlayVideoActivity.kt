package com.noljanolja.android.features.home.play.playscreen

import android.app.*
import android.content.*
import android.content.res.*
import android.graphics.drawable.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.annotation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.lifecycle.*
import com.noljanolja.*
import com.noljanolja.android.*
import com.noljanolja.android.R
import com.noljanolja.android.common.enums.*
import com.noljanolja.android.common.sharedpreference.*
import com.noljanolja.android.ui.composable.youtube.*
import com.noljanolja.android.ui.theme.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.*
import org.koin.androidx.viewmodel.ext.android.*
import org.koin.core.parameter.*

class PlayVideoActivity : ComponentActivity() {

    private val isInPipMode = mutableStateOf(false)
    private val sharedPreferenceHelper: SharedPreferenceHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val videoId = intent.extras?.getString("videoId").orEmpty()
        val viewModel: VideoDetailViewModel = getViewModel { parametersOf(videoId) }
        viewModel.updateVideo(videoId)
        YoutubeViewWithFullScreen.release()
        MyApplication.backStackActivities.add(this)
        setContent {
            NoljanoljaTheme(
                appColorSetting = EAppColorSetting.getColorByKey(
                    sharedPreferenceHelper.appColor
                )
            ) {
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
            if (intent.extras?.getBoolean("isInPictureInPictureMode") == true) {
                enterPip()
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration,
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipMode.value = isInPictureInPictureMode
    }

    override fun onUserLeaveHint() {
        if (!isInPipMode.value) enterPip()
    }

    private fun enterPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            enterPictureInPictureMode()
        }
    }

    companion object {
        fun createIntent(activity: Context, videoId: String, isInPictureInPictureMode: Boolean) =
            Intent(activity, PlayVideoActivity::class.java).apply {
                putExtra("videoId", videoId)
                putExtra("isInPictureInPictureMode", isInPictureInPictureMode)
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