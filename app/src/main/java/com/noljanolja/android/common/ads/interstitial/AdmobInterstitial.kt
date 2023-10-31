package com.noljanolja.android.common.ads.interstitial

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import co.touchlab.kermit.Logger
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AdmobInterstitial(
    private val unitId: String = "ca-app-pub-3940256099942544/1033173712",
    var enable: MutableStateFlow<Boolean> = MutableStateFlow(true),
) : Interstitial {

    private var loadAdsDeferred: Deferred<InterstitialAd?>? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var isShowing = false

    @SuppressLint("VisibleForTests")
    override suspend fun load(activity: Activity) = suspendCoroutine<LoadResult> { continuation ->
        if (!enable.value) {
            continuation.resume(LoadResult.Success)
            return@suspendCoroutine
        }
        if (mInterstitialAd != null) {
            continuation.resume(LoadResult.Success)
            return@suspendCoroutine
        }
        var isContinuationResumed = false // Flag to track if the continuation has been resumed
        val resumeSuspendCoroutine: (LoadResult) -> Unit = {
            if (!isContinuationResumed) {
                continuation.resume(it)
                isContinuationResumed = true
            }
        }
        Logger.d("Interstitial loading")
        InterstitialAd.load(
            activity,
            unitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Firebase.analytics.logEvent("Admob_interstitial_load_failed") {
                        param("unit_id", unitId)
                        param("error", adError.message)
                        param("code", adError.code.toLong())
                    }
                    resumeSuspendCoroutine(
                        LoadResult.Error(
                            code = adError.code,
                            message = adError.message
                        )
                    )
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Firebase.analytics.logEvent("Admob_interstitial_loaded") {
                        param("unit_id", unitId)
                    }
                    Logger.d("Interstitial was loaded.")
                    Logger.d(
                        "Interstitial adapter class name:" +
                            interstitialAd.responseInfo.mediationAdapterClassName
                    )
                    mInterstitialAd = interstitialAd
                    resumeSuspendCoroutine(LoadResult.Success)
                }
            }
        )
        val timeoutMillis = 20000L // Timeout duration in milliseconds
        CoroutineScope(Dispatchers.Default).launch {
            delay(timeoutMillis)
            resumeSuspendCoroutine(LoadResult.Error())
        }
    }

    suspend fun hardload(context: Context) = suspendCoroutine<Boolean> { continuation ->
        if (!enable.value) {
            continuation.resume(true)
            return@suspendCoroutine
        }
        loadAdsDeferred = CoroutineScope(Dispatchers.Main).async {
            // gán biến loadAdsDeferred vào async mới
            suspendCoroutine<InterstitialAd?> {
                Logger.d("Interstitial loading")

                InterstitialAd.load(
                    context,
                    unitId,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Logger.d("Interstitial failed: $adError")
                            Firebase.analytics.logEvent("Admob_interstitial_load_failed") {
                                param("unit_id", unitId)
                                param("error", adError.message)
                            }
                            it.resume(null)
                            continuation.resume(false)
                        }

                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            Firebase.analytics.logEvent("Admob_interstitial_loaded") {
                                param("unit_id", unitId)
                            }
                            Logger.d("Interstitial was loaded.")
                            Logger.d(
                                "Interstitial adapter class name:" +
                                    interstitialAd.responseInfo.mediationAdapterClassName
                            )
                            it.resume(interstitialAd)
                            continuation.resume(true)
                        }
                    }
                )
            }
        }
    }

    override fun isReady() = mInterstitialAd != null

    // just for demo purpose
    suspend fun delayShow(
        activity: Activity,
        millis: Long = 0L,
        enableDialog: Boolean = true,
        onCompleted: (result: Any?) -> Unit,
    ) {
        if (!enable.value) {
            onCompleted(false)
            return
        }
        load(activity)
//        val loadingDialog = HakiDialog.Simple.navigating(activity).also {
//            it.setCancelable(false)
//            if (enableDialog) it.show()
//        }
        delay(millis)
        if (enableDialog) {
            try {
//                loadingDialog.dismiss()
            } catch (e: Exception) {
                Logger.d("loading Dialog Exception: ${e.message}")
            }
        }
        show(activity, enableDialog, onCompleted)
    }

    /** Shows the ad if one isn't already showing. */
    override fun show(
        activity: Activity,
        enableDialog: Boolean,
        onCompleted: (result: Any?) -> Unit,
    ) {
        if (!enable.value) {
            onCompleted(false)
            return
        }

        // If the app open ad is already showing, do not show the ad again.
        if (isShowing) {
            Logger.d("The Interstitial ad is already showing.")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val resultLoad = load(activity)
            if (resultLoad is LoadResult.Success && mInterstitialAd != null) {
                showAvailableAd(activity, mInterstitialAd!!, onCompleted)
            } else {
                onCompleted(false)
            }
        }
    }

    private fun showAvailableAd(
        activity: Activity,
        interstitial: InterstitialAd,
        onCompleted: (result: Any?) -> Unit,
    ) {
        val onCompletedWrapped = {
            isShowing = false
            CoroutineScope(Dispatchers.IO).launch {
                load(activity)
            }
        }

        interstitial.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdClicked() {
                super.onAdClicked()
                Logger.d("Admob_interstitial_clicked")
                Firebase.analytics.logEvent("Admob_interstitial_clicked") {
                    param("unit_id", unitId)
                }
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Logger.d("Admob_interstitial_impression")
                Firebase.analytics.logEvent("Admob_interstitial_impression") {
                    param("unit_id", unitId)
                }
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                onCompleted(true)
                Logger.d("Admob_interstitial_full_content_dismiss")
                Firebase.analytics.logEvent("Admob_interstitial_full_content_dismiss") {
                    param("unit_id", unitId)
                }
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                onCompletedWrapped()
                onCompleted(false)
                Logger.d("Admob_interstitial_full_content_failed")
                Firebase.analytics.logEvent("Admob_interstitial_full_content_failed") {
                    param("unit_id", unitId)
                    param("message", p0.message)
                    param("code", p0.code.toLong())
                    param("cause", p0.cause.toString())
                }
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                Logger.d("Admob_interstitial_full_content_showed")
                Firebase.analytics.logEvent("Admob_interstitial_full_content_showed") {
                    param("unit_id", unitId)
                }
            }
        }

        interstitial.setOnPaidEventListener { p0 ->
            Logger.d("interstitial ads was paid ${p0.valueMicros} ${p0.currencyCode}")
            if (p0.valueMicros > 0) {
                Firebase.analytics.logEvent("Admob_interstitial_paid_valid") {
                    param("unit_id", unitId)
                }
            } else {
                Firebase.analytics.logEvent("Admob_interstitial_paid_0") {
                    param("unit_id", unitId)
                }
            }
        }

        isShowing = true
        interstitial.show(activity)
    }

    // get interstitial from loadAdsDeferred with timeout
    private suspend fun getInterstitialWithTimeout(
        activity: Activity,
        timeout: Long,
    ): InterstitialAd? {
        var interstitial: InterstitialAd? = null
        // nếu loadAdsDeferred là null, nghĩa là không có hàm load nào đang được gọi hoặc đã được gọi truớc đó mà chưa được dùng
        if (loadAdsDeferred == null) {
            // gọi hàm load
            load(activity)
        }
        try {
            withTimeout(timeout) {
                // nếu chưa hết giờ, lấy ra biến interstitial, sau đó gán loadAds lại = null
                interstitial = loadAdsDeferred!!.await()
                loadAdsDeferred = null
            }
        } catch (e: Exception) {
            // vào catch nghĩa là đã timeout, biến interstitial của loadAdsDeferred chưa được dùng, sẽ đc await tiếp trong lần gọi tới
            Logger.d("interstitial load timeout ${e.message}")
        }
        // nullable
        return interstitial
    }

    companion object {
        private const val TIME_OUT_MILLIS_AD_REQUEST = 15000L
    }
}
