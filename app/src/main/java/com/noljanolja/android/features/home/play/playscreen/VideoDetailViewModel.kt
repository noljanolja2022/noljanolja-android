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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch

class VideoDetailViewModel(private val videoId: String) : BaseViewModel() {
    var youTubePlayer: YouTubePlayer? = null

    private val _uiStateFlow = MutableStateFlow(UiState<VideoDetailUiData>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

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
    }

    private fun commentVideo(comment: String) {
        launch {
            coreManager.commentVideo(videoId, comment).exceptionOrNull()?.let {
                sendError(it)
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