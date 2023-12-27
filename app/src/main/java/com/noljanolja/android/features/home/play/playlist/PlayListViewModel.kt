package com.noljanolja.android.features.home.play.playlist

import androidx.lifecycle.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.core.user.domain.model.*
import com.noljanolja.core.video.domain.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PlayListViewModel : BaseViewModel() {
    private val _uiStateFlow = MutableStateFlow(UiState<PlayListUIData>())
    val uiStateFlow = _uiStateFlow.asStateFlow()
    private val _userStateFlow = MutableStateFlow(User())
    val userStateFlow = _userStateFlow.asStateFlow()

    init {
        refresh()
        viewModelScope.launch {
            coreManager.getCurrentUser(forceRefresh = true, onlyLocal = false).getOrNull()?.let {
                _userStateFlow.emit(it)
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

                PlayListEvent.Refresh -> {
                    val data = _uiStateFlow.value.data ?: PlayListUIData()
                    _uiStateFlow.emit(
                        UiState(
                            data = data,
                            loading = true
                        )
                    )
                    delay(50)
                    refresh()
                }

                PlayListEvent.Search -> {
                    navigationManager.navigate(NavigationDirections.SearchVideos)
                }

                PlayListEvent.Uncompleted -> {
                    navigationManager.navigate(NavigationDirections.UncompletedVideos)
                }

                PlayListEvent.Setting -> {
                    navigationManager.navigate(NavigationDirections.Setting)
                }
            }
        }
    }

    private fun refresh() {
        launch {
            coreManager.getTrendingVideos(TrendingVideoDuration.Month).collect {
                val data = _uiStateFlow.value.data ?: PlayListUIData()
                _uiStateFlow.emit(
                    UiState(
                        data = data.copy(
                            todayVideos = it
                        ),
                        loading = false
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
                        ),
                        loading = false
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
                        ),
                        loading = false
                    )
                )
            }
        }
    }
}

data class PlayListUIData(
    val watchingVideos: List<Video> = listOf(),
    val highlightVideos: List<Video> = listOf(),
    val todayVideos: List<Video> = listOf(),
    val searchedVideos: List<Video> = listOf(),
)