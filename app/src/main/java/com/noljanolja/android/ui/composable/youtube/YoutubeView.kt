package com.noljanolja.android.ui.composable.youtube

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.get
import androidx.core.view.isVisible
import com.noljanolja.android.util.findActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YoutubeView(
    modifier: Modifier,
    onReady: (YouTubePlayer) -> Unit,
    toggleFullScreen: (Boolean) -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        AndroidView(
            factory = { context ->
                YoutubeViewWithFullScreen.getInstance(
                    context,
                    onReady = onReady,
                    toggleFullScreen = toggleFullScreen
                )
            },
        )
    }
}

@SuppressLint("StaticFieldLeak")
object YoutubeViewWithFullScreen {
    private var instance: RelativeLayout? = null
    private val playerOptions = IFramePlayerOptions.Builder().controls(1).fullscreen(1).build()
    fun getInstance(
        context: Context,
        toggleFullScreen: (Boolean) -> Unit,
        onReady: (YouTubePlayer) -> Unit,
    ): View {
        return instance ?: RelativeLayout(context).apply {
            val fullViewContainer = FrameLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                isVisible = false
            }
            val youtubeView = YouTubePlayerView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                enableAutomaticInitialization = false
                initialize(
                    youTubePlayerListener = object : AbstractYouTubePlayerListener() {
                        override fun onReady(player: YouTubePlayer) {
                            super.onReady(player)
                            onReady(player)
                        }
                    },
                    playerOptions = playerOptions,
                )
                addFullscreenListener(object : FullscreenListener {
                    override fun onEnterFullscreen(
                        fullscreenView: View,
                        exitFullscreen: () -> Unit,
                    ) {
                        toggleFullScreen.invoke(true)
                        context.findActivity()?.requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        isVisible = false
                        fullViewContainer.isVisible = true
                        fullViewContainer.addView(fullscreenView)
                    }

                    override fun onExitFullscreen() {
                        context.findActivity()?.requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        toggleFullScreen.invoke(false)
                        isVisible = true
                        fullViewContainer.isVisible = false
                        fullViewContainer.removeAllViews()
                    }
                })
            }
            addView(youtubeView)
            addView(fullViewContainer)
        }.also {
            instance = it
        }
    }

    fun release() {
        (instance?.get(0) as? YouTubePlayerView)?.release()
        instance?.removeAllViews()
        instance = null
    }
}