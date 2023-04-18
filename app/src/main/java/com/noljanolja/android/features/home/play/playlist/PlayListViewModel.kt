package com.noljanolja.android.features.home.play.playlist

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.video.domain.model.TrendingVideoDuration
import com.noljanolja.core.video.domain.model.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayListViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UiState<PlayListUIData>())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        launch {
            coreManager.getTrendingVideos(TrendingVideoDuration.Day).collect {
                val data = _uiStateFlow.value.data ?: PlayListUIData()
                _uiStateFlow.emit(
                    UiState(
                        data = data.copy(
                            todayVideos = it
                        )
                    )
                )
            }
        }
        launch {
            coreManager.getVideos(isHighlight = true).collect {
                val data = _uiStateFlow.value.data ?: PlayListUIData()
                _uiStateFlow.emit(
                    UiState(
                        data = data.copy(
                            highlightVideos = it
                        )
                    )
                )
            }
        }
        launch {
            coreManager.getWatchingVideos().collect {
                val data = _uiStateFlow.value.data ?: PlayListUIData()
                _uiStateFlow.emit(
                    UiState(
                        data = data.copy(
                            watchingVideos = it
                        )
                    )
                )
            }
        }
    }

    fun handleEvent(event: PlayListEvent) {
        launch {
            when (event) {
                PlayListEvent.Back -> navigationManager.back()
                is PlayListEvent.PlayVideo -> {
                    navigationManager.navigate(
                        NavigationDirections.PlayScreen(
                            videoId = event.id
                        )
                    )
                }
            }
        }
    }
}

data class PlayListUIData(
    val watchingVideos: List<Video> = listOf(),
    val highlightVideos: List<Video> = listOf(),
    val todayVideos: List<Video> = listOf(),
)