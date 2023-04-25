package com.noljanolja.android.features.home.play.playscreen

import androidx.annotation.StringRes
import com.noljanolja.android.R
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.ui.composable.youtube.YoutubeViewWithFullScreen
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.video.domain.model.Comment
import com.noljanolja.core.video.domain.model.Video
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

class VideoDetailViewModel(private val videoId: String) : BaseViewModel() {
    var youTubePlayer: YouTubePlayer? = null

    private val _uiStateFlow = MutableStateFlow(UiState<VideoDetailUiData>())
    val uiStateFlow = _uiStateFlow.asStateFlow()
    private val videoStateFlow = MutableStateFlow<PlayerConstants.PlayerState>(PlayerConstants.PlayerState.UNKNOWN)

    init {
        launch {
            val user = coreManager.getCurrentUser().getOrNull()
            coreManager.getVideoDetail(videoId)
                .catch { e ->
                    e.printStackTrace()
                }
                .collect {
                    _uiStateFlow.emit(
                        UiState(
                            data = VideoDetailUiData(
                                video = it,
                                user = user
                            )
                        )
                    )
                }
        }
        launch {
            videoStateFlow.collectLatest {
                trackVideoProgress(it)
            }
        }
    }

    fun handleEvent(event: VideoDetailEvent) {
        launch {
            when (event) {
                VideoDetailEvent.Back -> navigationManager.back()
                VideoDetailEvent.ToggleFullScreen -> youTubePlayer?.toggleFullscreen()
                is VideoDetailEvent.ReadyVideo -> onReady(event.player)
                is VideoDetailEvent.Comment -> commentVideo(event.comment)
            }
        }
    }

    private fun onReady(player: YouTubePlayer) {
        youTubePlayer = player
        player.loadVideo(videoId, 0F)
        player.addListener(object : YouTubePlayerListener {
            override fun onApiChange(youTubePlayer: YouTubePlayer) {
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
            }

            override fun onPlaybackQualityChange(
                youTubePlayer: YouTubePlayer,
                playbackQuality: PlayerConstants.PlaybackQuality,
            ) {
            }

            override fun onPlaybackRateChange(
                youTubePlayer: YouTubePlayer,
                playbackRate: PlayerConstants.PlaybackRate,
            ) {
            }

            override fun onReady(youTubePlayer: YouTubePlayer) {
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                launch {
                    videoStateFlow.emit(state)
                }
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
            }

            override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
            }

            override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
            }
        })
    }

    private fun commentVideo(comment: String) {
        launch {
            coreManager.commentVideo(videoId, comment).exceptionOrNull()?.let {
                sendError(it)
            }
        }
    }

    private fun trackVideoProgress(state: PlayerConstants.PlayerState) {
        launch {
            when (state) {
                PlayerConstants.PlayerState.PAUSED -> {
                    coreManager.trackVideoProgress() { e, t -> e.printStackTrace() }
                }

                else -> Unit
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        YoutubeViewWithFullScreen.release()
    }
}

data class VideoDetailUiData(
    val video: Video,
    val user: User? = null,
)

enum class VideoCommentSortType(
    @StringRes val id: Int,
) {
    Popular(R.string.video_detail_comment_popular),
    Newest(R.string.video_detail_comment_newest),
}