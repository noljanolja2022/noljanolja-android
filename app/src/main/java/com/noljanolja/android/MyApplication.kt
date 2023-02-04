package com.noljanolja.android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "ad4c67b3f151f5f38835e54f39d1aaaa")
    }
}