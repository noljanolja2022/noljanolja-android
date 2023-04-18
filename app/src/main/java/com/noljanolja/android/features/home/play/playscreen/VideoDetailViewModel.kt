package com.noljanolja.android.features.home.play.playscreen

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.ui.composable.youtube.YoutubeViewWithFullScreen
import com.noljanolja.core.video.domain.model.Video
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VideoDetailViewModel(private val videoId: String) : BaseViewModel() {
    var youTubePlayer: YouTubePlayer? = null

    private val _uiStateFlow = MutableStateFlow(UiState<VideoDetailUiData>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            val result = coreManager.getVideoDetail(videoId)
            if (result.isSuccess) {
                _uiStateFlow.emit(UiState(data = VideoDetailUiData(video = result.getOrNull()!!)))
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
)