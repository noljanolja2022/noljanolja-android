package com.noljanolja.android

import android.app.*
import android.content.*
import android.os.*
import androidx.lifecycle.*
import co.touchlab.kermit.Logger
import coil.*
import coil.decode.*
import coil.util.*
import com.d2brothers.firebase_auth.*
import com.google.firebase.analytics.ktx.*
import com.google.firebase.crashlytics.ktx.*
import com.google.firebase.ktx.*
import com.google.firebase.remoteconfig.*
import com.noljanolja.android.common.base.*
import com.noljanolja.android.common.mobiledata.data.*
import com.noljanolja.android.common.navigation.*
import com.noljanolja.android.common.sharedpreference.*
import com.noljanolja.android.common.user.data.*
import com.noljanolja.android.features.addfriend.*
import com.noljanolja.android.features.addreferral.*
import com.noljanolja.android.features.auth.countries.*
import com.noljanolja.android.features.auth.forget.*
import com.noljanolja.android.features.auth.login.*
import com.noljanolja.android.features.auth.login_or_signup.*
import com.noljanolja.android.features.auth.otp.*
import com.noljanolja.android.features.auth.signup.*
import com.noljanolja.android.features.auth.terms_of_service.*
import com.noljanolja.android.features.auth.updateprofile.*
import com.noljanolja.android.features.chatsettings.*
import com.noljanolja.android.features.conversationmedia.*
import com.noljanolja.android.features.edit_chat_title.*
import com.noljanolja.android.features.home.*
import com.noljanolja.android.features.home.chat.*
import com.noljanolja.android.features.home.chat_options.*
import com.noljanolja.android.features.home.contacts.*
import com.noljanolja.android.features.home.conversations.*
import com.noljanolja.android.features.home.friend_notification.*
import com.noljanolja.android.features.home.friendoption.*
import com.noljanolja.android.features.home.friends.*
import com.noljanolja.android.features.home.info.*
import com.noljanolja.android.features.home.menu.*
import com.noljanolja.android.features.home.mypage.*
import com.noljanolja.android.features.home.play.optionsvideo.*
import com.noljanolja.android.features.home.play.playlist.*
import com.noljanolja.android.features.home.play.playscreen.*
import com.noljanolja.android.features.home.play.search.*
import com.noljanolja.android.features.home.play.uncompleted.*
import com.noljanolja.android.features.home.require_login.*
import com.noljanolja.android.features.home.root.*
import com.noljanolja.android.features.home.searchchat.*
import com.noljanolja.android.features.home.searchfriends.*
import com.noljanolja.android.features.home.sendpoint.*
import com.noljanolja.android.features.home.wallet.*
import com.noljanolja.android.features.home.wallet.dashboard.*
import com.noljanolja.android.features.home.wallet.detail.*
import com.noljanolja.android.features.home.wallet.exchange.*
import com.noljanolja.android.features.home.wallet.myranking.*
import com.noljanolja.android.features.home.wallet.transaction.*
import com.noljanolja.android.features.images.*
import com.noljanolja.android.features.qrcode.*
import com.noljanolja.android.features.referral.*
import com.noljanolja.android.features.setting.*
import com.noljanolja.android.features.setting.more.*
import com.noljanolja.android.features.sharemessage.*
import com.noljanolja.android.features.shop.coupons.*
import com.noljanolja.android.features.shop.giftdetail.*
import com.noljanolja.android.features.shop.main.*
import com.noljanolja.android.features.shop.productbycategory.*
import com.noljanolja.android.features.shop.search.*
import com.noljanolja.android.features.splash.*
import com.noljanolja.android.services.*
import com.noljanolja.android.services.analytics.*
import com.noljanolja.android.services.analytics.firebase.*
import com.noljanolja.android.util.*
import com.noljanolja.android.util.Constant.*
import com.noljanolja.core.*
import com.noljanolja.core.di.*
import com.noljanolja.core.service.ktor.*
import com.noljanolja.core.user.data.datasource.*
import com.noljanolja.socket.*
import okhttp3.*
import org.koin.android.ext.android.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.core.qualifier.*
import org.koin.dsl.*

class MyApplication : Application() {

    private val okHttpClient: OkHttpClient by inject(named("Coil"))
    private val coreManager: CoreManager by inject()
    private val authSdk: AuthSdk by inject()

    companion object {
        var isAppInForeground: Boolean = false
        var latestConversationId: Long = 0L
        val backStackActivities = mutableListOf<Activity>()
        var isHomeShowed: Boolean = false
        var localeSystem: String = LocaleDefine.KOREAN

        fun clearAllPipActivities() {
            backStackActivities.apply {
                forEach { it.finish() }
                clear()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (getLocaleSystem().country == LocaleDefine.INDIAN) localeSystem = LocaleDefine.INDIAN
        initKoin()
        initCoil()
        initRemoteConfig()
        ProcessLifecycleOwner.get().lifecycle.apply {
            addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    isAppInForeground = true
                    Logger.d("Noljanolja: foregrounded: ${ProcessLifecycleOwner.get().lifecycle.currentState.name}")
                    launchIfLogin {
                        coreManager.forceRefreshConversations()
                    }
                }

                override fun onStop(owner: LifecycleOwner) {
                    isAppInForeground = false
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) clearAllPipActivities()
                    Logger.d("Noljanolja: backgrounded: ${ProcessLifecycleOwner.get().lifecycle.currentState.name}")
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    coreManager.onDestroy()
                }
            })
        }
        launchInMainIO {
            coreManager.getReactIcons()
        }
    }

    fun launchIfLogin(block: suspend () -> Unit) = launchInMainIO {
        authSdk.getIdToken(false)?.takeIf { it.isNotBlank() } ?: return@launchInMainIO
        block.invoke()
    }

    private fun initKoin() {
        initKoin(
            module {
                single<Context> { this@MyApplication }
                single { SharedPreferenceHelper(get()) }
                single {
                    FirebaseTracker(
                        Firebase.analytics,
                    ).apply {
                        // TODO: Should fetch from remote config or use BuildConfig
                        isEnable = true
                    }
                }
                single {
                    FirebaseLogger(
                        Firebase.crashlytics,
                    ).apply {
                        // TODO: Should fetch from remote config or use BuildConfig
                        isEnable = true
                    }
                }
                single {
                    AppAnalytics(
                        trackers = mutableListOf(get()),
                        loggers = mutableListOf(get()),
                    )
                }
                single {
                    NavigationManager()
                }
                single {
                    AuthSdk.init(
                        context = get(),
                        kakaoApiKey = get<Context>().getString(R.string.kakao_api_key),
                        googleWebClientId = get<Context>().getClientId(),
                        naverClientId = "3zDg6vMsJmoFk2TGOjcq",
                        naverClientSecret = "8keRny2c_4",
                        naverClientName = "놀자놀자",
                        region = "asia-northeast3",
                    )
                }
                single<TokenRepo> {
                    TokenRepoImpl(get(), get())
                }
                single {
                    PermissionChecker(get())
                }
                single {
                    ContactsLoader(get())
                }
                single {
                    MediaLoader(get())
                }
                single {
                    StickersLoader(get(), get())
                }
                single {
                    SocketUserAgent(
                        userAgent = "noljanolja/${BuildConfig.VERSION_NAME} (Mobile; Android ${Build.VERSION.RELEASE}; ${Build.MANUFACTURER} ${Build.MODEL})"
                    )
                }
                single {
                    KtorConfig(
                        userAgent = "noljanolja/${BuildConfig.VERSION_NAME} (Mobile; Android ${Build.VERSION.RELEASE}; ${Build.MANUFACTURER} ${Build.MODEL})"
                    )
                }
                single {
                    KtorClient.createInstance(
                        get(),
                        get(),
                        get(),
                        get()
                    ) {
                        getLocaleSystem().language
                    }
                }
                single<AuthDataSource> {
                    AuthDataSourceImpl(get())
                }
                viewModel {
                    ChatViewModel(get(), get(), get(), get())
                }
                viewModel {
                    ContactsViewModel(get(), get())
                }
                viewModel {
                    ConversationsViewModel()
                }
                viewModel {
                    SearchChatViewModel()
                }
                viewModel {
                    CountriesViewModel()
                }
                viewModel {
                    ForgotViewModel()
                }
                viewModel {
                    HomeViewModel(get())
                }
                viewModel {
                    LoginOrSignupViewModel()
                }
                viewModel {
                    LoginViewModel()
                }
                viewModel {
                    MenuViewModel()
                }
                viewModel {
                    MyInfoViewModel()
                }
                viewModel {
                    MyPageViewModel()
                }
                viewModel {
                    OTPViewModel()
                }
                viewModel {
                    RequireLoginViewModel()
                }
                viewModel {
                    SettingViewModel()
                }
                viewModel {
                    SignupViewModel()
                }
                viewModel {
                    SplashViewModel()
                }
                viewModel {
                    TermsOfServiceViewModel()
                }
                viewModel {
                    UpdateProfileViewModel()
                }
                viewModel {
                    ChatOptionsViewModel(get())
                }
                viewModel {
                    EditChatTitleViewModel(get())
                }
                viewModel {
                    WalletViewModel()
                }
                viewModel {
                    VideoDetailViewModel()
                }
                viewModel {
                    PlayListViewModel()
                }
                viewModel {
                    WalletDashboardViewModel()
                }
                viewModel {
                    TransactionHistoryViewModel()
                }
                viewModel {
                    MyRankingViewModel()
                }
                viewModel {
                    (id: String, reason: String) -> TransactionDetailViewModel(id, reason)
                }
                viewModel {
                    AppInfoViewModel()
                }
                viewModel {
                    ChatSettingsViewModel()
                }
                viewModel {
                    AddFriendViewModel()
                }
                viewModel {
                    ScanQrCodeViewModel()
                }
                viewModel {
                    ShopViewModel()
                }
                viewModel {
                    SearchProductViewModel()
                }
                viewModel {
                    ProductByCategoryViewModel()
                }
                viewModel { (giftId: String, code: String, log: String) -> GiftDetailViewModel(giftId, code, log) }
                viewModel {
                    CouponsViewModel()
                }
                viewModel {
                    SelectShareMessageViewModel(get())
                }
                viewModel {
                    SearchVideosViewModel()
                }
                viewModel {
                    OptionsVideoViewModel()
                }
                viewModel {
                    CheckinViewModel()
                }
                viewModel { ReferralViewModel() }
                viewModel {
                    AddReferralViewModel()
                }
                viewModel {
                    UncompletedVideoViewModel()
                }
                viewModel {
                    ViewImagesViewModel()
                }
                viewModel {
                    ConversationMediaViewModel(get())
                }
                viewModel {
                    FriendsViewModel()
                }
                viewModel {
                    SearchFriendsViewModel()
                }
                viewModel {
                    FriendNotificationViewModel()
                }
                viewModel {
                    (friendId: String) -> SendPointViewModel(friendId)
                }
                viewModel {
                    (friendId: String, friendName: String) -> FriendOptionViewModel(friendId, friendName)
                }
                viewModel {
                    ExchangePointViewModel()
                }
            }
        )
    }

    private fun getLocaleSystem() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0]
    } else {
        resources.configuration.locale
    }

    private fun initCoil() {
        Coil.setImageLoader(
            ImageLoader.Builder(this).okHttpClient(okHttpClient).components {
                add(VideoFrameDecoder.Factory())
            }.logger(DebugLogger()).respectCacheHeaders(false).build()
        )
    }

    private fun initRemoteConfig() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
    }
}
