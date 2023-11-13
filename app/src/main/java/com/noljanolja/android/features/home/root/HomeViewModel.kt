package com.noljanolja.android.features.home.root

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.base.launchInMain
import com.noljanolja.android.common.mobiledata.data.StickersLoader
import com.noljanolja.android.common.navigation.NavigationDirections
import com.noljanolja.android.common.sharedpreference.SharedPreferenceHelper
import com.noljanolja.android.util.isSeen
import com.noljanolja.core.event.domain.model.EventBanner
import com.noljanolja.core.video.domain.model.PromotedVideo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject

class HomeViewModel(private val sharedPreferenceHelper: SharedPreferenceHelper) : BaseViewModel() {
    private val _showRequireLoginPopupEvent = MutableSharedFlow<Boolean>()
    val showRequireLoginPopupEvent = _showRequireLoginPopupEvent.asSharedFlow()

    private val stickersLoader: StickersLoader by inject()

    private val _readAllConversations = MutableStateFlow(true)
    val readAllConversations = _readAllConversations.asStateFlow()

    private val _eventBannersFlow = MutableStateFlow<List<EventBanner>>(emptyList())
    val eventBannersFlow = _eventBannersFlow.asStateFlow()

    private val _eventPromotedVideoFlow = MutableSharedFlow<PromotedVideo>()
    val eventPromotedVideoFlow = _eventPromotedVideoFlow.asSharedFlow()

    private var promotedVideo: PromotedVideo? = null

    init {
        launchInMain {
            stickersLoader.loadAllRemoteStickerPackages()
        }
        launch {
            coreManager.getLocalConversations().collect { conversations ->
                _readAllConversations.emit(conversations.all { it.isSeen() })
            }
        }
        launch {
            val seenBanners = sharedPreferenceHelper.seenBanners
            coreManager.getEventBanners().getOrNull()?.let {
//                _eventBannersFlow.emit(it.filter { banner -> banner.action == EventAction.CHECKIN || seenBanners.none { it == banner.id } })
            }
        }
        launch {
            coreManager.getPromotedVideos().getOrNull()?.firstOrNull()?.let {
                promotedVideo = it
//                _eventPromotedVideoFlow.emit(it)
            }
        }
    }

    fun handleEvent(event: HomeEvent) {
        launch {
            when (event) {
                HomeEvent.LoginOrVerifyEmail -> loginOrVerifyEmail()
                HomeEvent.CancelBanner -> _eventBannersFlow.emit(emptyList())
                is HomeEvent.CloseBanner -> sharedPreferenceHelper.seenBanners = listOf(event.id)
                HomeEvent.Back -> back()
                is HomeEvent.AutoAction -> reactVideo(event.token)
            }
        }
    }

    private suspend fun reactVideo(token: String) {
        coreManager.reactVideo(promotedVideo?.video?.id.orEmpty(), token)
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

    companion object {
        const val AUTO_COMMENT = "영상 재미있게 잘 봤습니다. 앞으로도 좋은 영상 기대할게요. 화이팅"
    }
}
