package com.noljanolja.android

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import co.touchlab.kermit.Logger
import coil.Coil
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.util.DebugLogger
import com.d2brothers.firebase_auth.AuthSdk
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.noljanolja.android.common.base.launchInMainIO
import com.noljanolja.android.common.mobiledata.data.ContactsLoader
import com.noljanolja.android.common.mobiledata.data.MediaLoader
import com.noljanolja.android.common.mobiledata.data.StickersLoader
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.data.AuthDataSourceImpl
import com.noljanolja.android.common.user.data.TokenRepoImpl
import com.noljanolja.android.features.auth.countries.CountriesViewModel
import com.noljanolja.android.features.auth.forget.ForgotViewModel
import com.noljanolja.android.features.auth.login.LoginViewModel
import com.noljanolja.android.features.auth.login_or_signup.LoginOrSignupViewModel
import com.noljanolja.android.features.auth.otp.OTPViewModel
import com.noljanolja.android.features.auth.signup.SignupViewModel
import com.noljanolja.android.features.auth.terms_of_service.TermsOfServiceViewModel
import com.noljanolja.android.features.auth.updateprofile.UpdateProfileViewModel
import com.noljanolja.android.features.edit_chat_title.EditChatTitleViewModel
import com.noljanolja.android.features.home.chat.ChatViewModel
import com.noljanolja.android.features.home.chat_options.ChatOptionsViewModel
import com.noljanolja.android.features.home.contacts.ContactsViewModel
import com.noljanolja.android.features.home.conversations.ConversationsViewModel
import com.noljanolja.android.features.home.info.MyInfoViewModel
import com.noljanolja.android.features.home.menu.MenuViewModel
import com.noljanolja.android.features.home.mypage.MyPageViewModel
import com.noljanolja.android.features.home.play.playlist.PlayListViewModel
import com.noljanolja.android.features.home.play.playscreen.VideoDetailViewModel
import com.noljanolja.android.features.home.require_login.RequireLoginViewModel
import com.noljanolja.android.features.home.root.HomeViewModel
import com.noljanolja.android.features.home.wallet.WalletViewModel
import com.noljanolja.android.features.setting.SettingViewModel
import com.noljanolja.android.features.splash.SplashViewModel
import com.noljanolja.android.services.PermissionChecker
import com.noljanolja.android.services.analytics.AppAnalytics
import com.noljanolja.android.services.analytics.firebase.FirebaseLogger
import com.noljanolja.android.services.analytics.firebase.FirebaseTracker
import com.noljanolja.core.CoreManager
import com.noljanolja.core.di.initKoin
import com.noljanolja.core.service.ktor.KtorClient
import com.noljanolja.core.service.ktor.KtorConfig
import com.noljanolja.core.user.data.datasource.AuthDataSource
import com.noljanolja.socket.SocketUserAgent
import com.noljanolja.socket.TokenRepo
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

class MyApplication : Application() {

    private val okHttpClient: OkHttpClient by inject(named("Coil"))
    private val coreManager: CoreManager by inject()
    private val authSdk: AuthSdk by inject()

    companion object {
        var isAppInForeground: Boolean = false
        var latestConversationId: Long = 0L
    }

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initCoil()
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
                    Logger.d("Noljanolja: backgrounded: ${ProcessLifecycleOwner.get().lifecycle.currentState.name}")
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    coreManager.onDestroy()
                }
            })
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
                        googleWebClientId = get<Context>().getString(R.string.web_client_id),
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
                        get(),
                    )
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
                    CountriesViewModel()
                }
                viewModel {
                    ForgotViewModel()
                }
                viewModel {
                    HomeViewModel()
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
                    VideoDetailViewModel(get())
                }
                viewModel {
                    PlayListViewModel()
                }
            }
        )
    }

    private fun initCoil() {
        Coil.setImageLoader(
            ImageLoader.Builder(this).okHttpClient(okHttpClient).components {
                add(VideoFrameDecoder.Factory())
            }.logger(DebugLogger()).respectCacheHeaders(false).build()
        )
    }
}
