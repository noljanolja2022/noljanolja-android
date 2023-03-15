package com.noljanolja.android

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import co.touchlab.kermit.Logger
import com.d2brothers.firebase_auth.AuthSdk
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.noljanolja.android.common.contact.data.ContactsLoader
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.user.data.AuthDataSourceImpl
import com.noljanolja.android.features.auth.countries.CountriesViewModel
import com.noljanolja.android.features.auth.forget.ForgotViewModel
import com.noljanolja.android.features.auth.login.LoginViewModel
import com.noljanolja.android.features.auth.login_or_signup.LoginOrSignupViewModel
import com.noljanolja.android.features.auth.otp.OTPViewModel
import com.noljanolja.android.features.auth.signup.SignupViewModel
import com.noljanolja.android.features.auth.terms_of_service.TermsOfServiceViewModel
import com.noljanolja.android.features.auth.updateprofile.UpdateProfileViewModel
import com.noljanolja.android.features.home.chat.ChatViewModel
import com.noljanolja.android.features.home.contacts.ContactsViewModel
import com.noljanolja.android.features.home.conversations.ConversationsViewModel
import com.noljanolja.android.features.home.info.MyInfoViewModel
import com.noljanolja.android.features.home.menu.MenuViewModel
import com.noljanolja.android.features.home.mypage.MyPageViewModel
import com.noljanolja.android.features.home.require_login.RequireLoginViewModel
import com.noljanolja.android.features.home.root.HomeViewModel
import com.noljanolja.android.features.setting.SettingViewModel
import com.noljanolja.android.features.splash.SplashViewModel
import com.noljanolja.android.services.analytics.AppAnalytics
import com.noljanolja.android.services.analytics.firebase.FirebaseLogger
import com.noljanolja.android.services.analytics.firebase.FirebaseTracker
import com.noljanolja.core.auth.domain.repository.AuthRepository
import com.noljanolja.core.di.initKoin
import com.noljanolja.core.service.ktor.KtorClient
import com.noljanolja.core.service.ktor.KtorConfig
import com.noljanolja.core.user.data.datasource.AuthDataSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class MyApplication : Application() {
    companion object {
        var isAppInForeground: Boolean = false
        var latestConversationId: Long = 0L
    }

    override fun onCreate() {
        super.onCreate()
        initKoin()
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                isAppInForeground = true
                Logger.d("Noljanolja: foregrounded: ${ProcessLifecycleOwner.get().lifecycle.currentState.name}")
            }

            override fun onStop(owner: LifecycleOwner) {
                isAppInForeground = false
                Logger.d("Noljanolja: backgrounded: ${ProcessLifecycleOwner.get().lifecycle.currentState.name}")
            }
        })
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
                single {
                    ContactsLoader(get())
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
                        refreshToken = {
                            get<AuthRepository>().saveAuthToken(
                                get<AuthSdk>().getIdToken(true).orEmpty()
                            )
                        }
                    )
                }
                single<AuthDataSource> {
                    AuthDataSourceImpl(get())
                }
                viewModel {
                    ChatViewModel()
                }
                viewModel {
                    ContactsViewModel()
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
            }
        )
    }
}
