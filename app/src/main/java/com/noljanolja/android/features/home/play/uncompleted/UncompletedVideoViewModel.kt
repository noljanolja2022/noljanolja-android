package com.noljanolja.android.features.home.play.uncompleted

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.core.video.domain.model.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch

class UncompletedVideoViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<List<Video>>>(UiState(loading = true))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun handleEvent(event: UncompletedEvent) {
        launch {
            when (event) {
                UncompletedEvent.Back -> back()
                is UncompletedEvent.PlayVideo -> {
                    navigationManager.navigate(
                        NavigationDirections.PlayScreen(
                            videoId = event.id
                        )
                    )
                }
            }
        }
    }

    init {
        launch {
            coreManager.getWatchingVideos()
                .catch {
                    _uiStateFlow.value = UiState(error = it)
                }
                .collect {
                    _uiStateFlow.value = UiState(data = it)
                }
        }
    }
}