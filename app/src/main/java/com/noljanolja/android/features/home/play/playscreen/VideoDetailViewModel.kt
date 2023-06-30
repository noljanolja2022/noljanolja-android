package com.noljanolja.android.features.home.play.playscreen

import androidx.annotation.StringRes
import com.noljanolja.android.R
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.sharedpreference.SharedPreferenceHelper
import com.noljanolja.android.ui.composable.youtube.YoutubeViewWithFullScreen
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.video.data.model.request.VideoProgressEvent
import com.noljanolja.core.video.domain.model.Comment
import com.noljanolja.core.video.domain.model.Video
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import org.koin.core.component.inject

class VideoDetailViewModel(private val videoId: String) : BaseViewModel() {
    private val sharedPreferenceHelper: SharedPreferenceHelper by inject()
    var youTubePlayer: YouTubePlayer? = null

    private val _uiStateFlow = MutableStateFlow(UiState<VideoDetailUiData>())
    val uiStateFlow = _uiStateFlow.asStateFlow()
    private val videoStateFlow =
        MutableStateFlow<PlayerConstants.PlayerState>(PlayerConstants.PlayerState.UNKNOWN)
    private val videoDurationSecondFlow = MutableStateFlow<Float>(0F)
    private var lastTrackEvent: Pair<VideoProgressEvent, Long>? = null
    private val _eventForceLoginGoogle = MutableSharedFlow<String>()
    val eventForceLoginGoogle = _eventForceLoginGoogle.asSharedFlow()

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
                    youTubePlayer?.seekTo((it.currentProgressMs / 1000).toFloat())
                }
        }
        launch {
            videoStateFlow.combine(videoDurationSecondFlow) { state, second ->
                Pair(state, second)
            }.collectLatest {
                trackVideoProgress(state = it.first, durationMs = (it.second * 1000).toLong())
            }
        }
    }

    fun handleEvent(event: VideoDetailEvent) {
        launch {
            when (event) {
                VideoDetailEvent.Back -> navigationManager.back()
                VideoDetailEvent.ToggleFullScreen -> youTubePlayer?.toggleFullscreen()
                is VideoDetailEvent.ReadyVideo -> onReady(event.player)
                is VideoDetailEvent.Comment -> commentVideo(event.comment, event.token)
                is VideoDetailEvent.SendError -> sendError(event.error)
            }
        }
    }

    private fun onReady(player: YouTubePlayer) {
        val video = _uiStateFlow.value.data?.video
        youTubePlayer = player
        player.loadVideo(videoId, ((video?.currentProgressMs ?: 0) / 1000).toFloat())
        player.addListener(
            listener = object : AbstractYouTubePlayerListener() {
                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState,
                ) {
                    super.onStateChange(youTubePlayer, state)
                    launch {
                        videoStateFlow.emit(state)
                    }
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    super.onCurrentSecond(youTubePlayer, second)
                    launch {
                        videoDurationSecondFlow.emit(second)
                    }
                }
            }
        )
    }

    private fun commentVideo(comment: String, token: String) {
        launch {
            val data = _uiStateFlow.value.data ?: return@launch
            val result = coreManager.commentVideo(videoId, comment, token)
            if (result.isFailure) {
                sendError(result.exceptionOrNull()!!)
            } else {
                _uiStateFlow.emit(
                    UiState(
                        data = data.copy(
                            user = data.user,
                            video = addCommentToVideo(data.video, result.getOrNull())
                        )
                    )
                )
            }
        }
    }

    private fun addCommentToVideo(video: Video, comment: Comment?): Video {
        if (comment == null) return video
        return video.copy(
            commentCount = video.commentCount + 1,
            comments = listOf(comment) + video.comments
        )
    }

    private suspend fun trackVideoProgress(state: PlayerConstants.PlayerState, durationMs: Long) {
        val event = when (state) {
            PlayerConstants.PlayerState.PAUSED -> VideoProgressEvent.PAUSE
            PlayerConstants.PlayerState.ENDED -> VideoProgressEvent.FINISH
            PlayerConstants.PlayerState.PLAYING -> VideoProgressEvent.PLAY
            else -> return
        }
        if (lastTrackEvent == null || lastTrackEvent!!.first != event || durationMs !in lastTrackEvent!!.second..(lastTrackEvent!!.second + delayTimeTrackProgress)) {
            coreManager.trackVideoProgress(
                videoId = videoId,
                event = event,
                durationMs = durationMs
            )
            lastTrackEvent = Pair(event, durationMs)
        }
    }

    override fun onCleared() {
        super.onCleared()
        coreManager.cancelTrackVideo()
        YoutubeViewWithFullScreen.release()
    }

    companion object {
        const val delayTimeTrackProgress = 10_000
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