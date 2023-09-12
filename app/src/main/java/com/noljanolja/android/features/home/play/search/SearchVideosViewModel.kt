package com.noljanolja.android.features.home.play.search

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.base.launch
import com.noljanolja.core.video.domain.model.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn

class SearchVideosViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow<UiState<SearchVideosUiData>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    val searchKeys = coreManager.getSearchVideoHistories().map {
        it.sortedByDescending { it.updatedAt }.map { it.text }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun handleEvent(event: SearchVideosEvent) {
        launch {
            when (event) {
                is SearchVideosEvent.Clear -> {
                    coreManager.clearSearchVideoText(event.text)
                }

                SearchVideosEvent.ClearAll -> {
                    coreManager.clearSearchVideoHistories()
                }

                is SearchVideosEvent.Search -> searchVideos(event.text)
            }
        }
    }

    private suspend fun searchVideos(query: String) {
        val videos = coreManager.getVideos(query = query).single()
        _uiStateFlow.emit(
            UiState(data = SearchVideosUiData(videos = videos))
        )
    }
}

data class SearchVideosUiData(
    val videos: List<Video>,
)