package com.noljanolja.android.features.home.play.playscreen

import androidx.annotation.StringRes
import com.noljanolja.android.R
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.ui.composable.youtube.YoutubeViewWithFullScreen
import com.noljanolja.core.user.domain.model.User
import com.noljanolja.core.video.domain.model.Comment
import com.noljanolja.core.video.domain.model.Commenter
import com.noljanolja.core.video.domain.model.Video
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class VideoDetailViewModel(private val videoId: String) : BaseViewModel() {
    var youTubePlayer: YouTubePlayer? = null

    private val _uiStateFlow = MutableStateFlow(UiState<VideoDetailUiData>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            val user = coreManager.getCurrentUser().getOrNull()
            val result = coreManager.getVideoDetail(videoId)
            if (result.isSuccess) {
                _uiStateFlow.emit(
                    UiState(
                        data = VideoDetailUiData(
                            video = result.getOrNull()!!,
                            user = user
                        )
                    )
                )
            } else {
                sendError(result.exceptionOrNull()!!)
            }
        }
    }

    fun handleEvent(event: VideoDetailEvent) {
        launch {
            when (event) {
                VideoDetailEvent.Back -> navigationManager.back()
                VideoDetailEvent.ToggleFullScreen -> youTubePlayer?.toggleFullscreen()
                is VideoDetailEvent.ReadyVideo -> onReady(event.player)
            }
        }
    }

    private fun onReady(player: YouTubePlayer) {
        youTubePlayer = player
        player.loadVideo(videoId, 0F)
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

fun Video.fakeComment() = this.copy(
    comments = listOf(
        com.noljanolja.android.features.home.play.playscreen.fakeComment(),
        com.noljanolja.android.features.home.play.playscreen.fakeComment(),
        com.noljanolja.android.features.home.play.playscreen.fakeComment(),
        com.noljanolja.android.features.home.play.playscreen.fakeComment(),
        com.noljanolja.android.features.home.play.playscreen.fakeComment(),
        com.noljanolja.android.features.home.play.playscreen.fakeComment()
    )
)

fun fakeComment() = Comment(
    comment = Random.nextInt(10000).toString() + "\n" + Random.nextInt(10000).toString(),
    commenter = Commenter(
        name = "Name ${Random.nextInt(100)}",
        avatar = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQaHzXkwxkLiVf9lCpIjZMsXFqAowpQYdOY60kcYmJehMAal8BpAcnFqxpUZHCBzQ4rKxA&usqp=CAU"
    ),
    id = Random.nextInt(10000)
)