package com.noljanolja.android.features.home.root

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.base.launchInMain
import com.noljanolja.android.common.mobiledata.data.StickersLoader
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.util.isSeen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject

class HomeViewModel : BaseViewModel() {
    private val _showRequireLoginPopupEvent = MutableSharedFlow<Boolean>()
    val showRequireLoginPopupEvent = _showRequireLoginPopupEvent.asSharedFlow()

    private val stickersLoader: StickersLoader by inject()

    private val _readAllConversations = MutableStateFlow(true)
    val readAllConversations = _readAllConversations.asStateFlow()

    init {
        launchInMain {
            stickersLoader.loadAllRemoteStickerPackages()
        }
        launch {
            coreManager.getLocalConversations().collect { conversations ->
                _readAllConversations.emit(conversations.all { it.isSeen() })
            }
        }
    }

    fun handleEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.LoginOrVerifyEmail -> loginOrVerifyEmail()
        }
    }

    private fun loginOrVerifyEmail() {
        launch {
            val user = coreManager.getCurrentUser().getOrNull()
            when {
                // TODO : Check verify if need after
                true -> {
                    _showRequireLoginPopupEvent.emit(false)
                    navigationManager.navigate(NavigationDirections.Auth)
                }
//                !user!!.isVerify -> sendError(Throwable("Verify fail"))
                else -> _showRequireLoginPopupEvent.emit(false)
            }
        }
    }
}
