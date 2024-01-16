package com.noljanolja.android.features.home.searchfriends

import androidx.lifecycle.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.core.user.domain.model.*
import kotlinx.coroutines.flow.*

/**
 * Created by tuyen.dang on 1/16/2024.
 */

class SearchFriendsViewModel : BaseViewModel() {

    private val _uiStateFlow = MutableStateFlow<UiState<SearchFriendsUiData>>(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    val searchKeys = coreManager.getContactSearchHistories().map { data ->
        data.sortedByDescending { it.updatedAt }.map { it.text }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun handleEvent(event: SearchFriendsEvent) {
        launch {
            when (event) {
                SearchFriendsEvent.Back -> back()
                is SearchFriendsEvent.Clear -> {
                    coreManager.clearContactTextSearch(event.text)
                }

                SearchFriendsEvent.ClearAll -> {
                    coreManager.clearAllContactSearch()
                }

                is SearchFriendsEvent.OpenFriendOption -> {
                    event.run {
                        navigationManager.navigate(
                            NavigationDirections.FriendOption(
                                friendId = friendId,
                                friendName = friendName,
                                friendAvatar = friendAvatar
                            )
                        )
                    }
                }

                is SearchFriendsEvent.Search -> searchVideos(event.text)
            }
        }
    }

    private suspend fun searchVideos(query: String) {
        _isLoading.emit(true)
        coreManager.insertContactSearchKey(query)
        val result = coreManager.getContacts(1).getOrDefault(emptyList()).filter {
            it.run {
                id.contains(query) || name.contains(query)
            }
        }
        _uiStateFlow.emit(
            UiState(data = SearchFriendsUiData(friends = result))
        )
        _isLoading.emit(false)
    }
}

data class SearchFriendsUiData(
    val friends: List<User>,
)
