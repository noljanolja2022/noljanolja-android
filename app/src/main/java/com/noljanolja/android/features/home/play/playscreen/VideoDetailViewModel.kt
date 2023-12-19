package com.noljanolja.android.features.home.play.playscreen

import androidx.annotation.*
import com.noljanolja.android.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.sharedpreference.*
import com.noljanolja.android.extensions.*
import com.noljanolja.android.ui.composable.youtube.*
import com.noljanolja.core.user.domain.model.*
import com.noljanolja.core.video.data.model.request.*
import com.noljanolja.core.video.domain.model.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.*

class VideoDetailViewModel() : BaseViewModel() {
    private var videoId: String = ""
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
    private val _playerStateFlow =
        MutableStateFlow<PlayerConstants.PlayerState>(PlayerConstants.PlayerState.UNKNOWN)
    val playerStateFlow = _playerStateFlow.asStateFlow()

    fun updateVideo(videoId: String?) {
        if (videoId == null) {
            onCleared()
            return
        }
        launch {
            this.videoId = videoId
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
                    youTubePlayer?.loadVideo(videoId, (it.currentProgressMs / 1000).toFloat())
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
                is VideoDetailEvent.TogglePlayPause -> togglePlayPause()
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
                    YoutubeViewWithFullScreen.playState = state
                    _playerStateFlow.value = state
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

    private fun togglePlayPause() {
        if (playerStateFlow.value == PlayerConstants.PlayerState.PLAYING) {
            youTubePlayer?.pause()
        } else {
            youTubePlayer?.play()
        }
    }

    private suspend fun trackVideoProgress(state: PlayerConstants.PlayerState, durationMs: Long) {
        val event = when (state) {
            PlayerConstants.PlayerState.PAUSED -> VideoProgressEvent.PAUSE
            PlayerConstants.PlayerState.ENDED -> VideoProgressEvent.PLAY
            PlayerConstants.PlayerState.PLAYING -> VideoProgressEvent.PLAY
            else -> return
        }
        if (lastTrackEvent == null || lastTrackEvent?.first != event
            || durationMs !in lastTrackEvent?.second.convertToLong()..(lastTrackEvent?.second.convertToLong() + delayTimeTrackProgress)
        ) {
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