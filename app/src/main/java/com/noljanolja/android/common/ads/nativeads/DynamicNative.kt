package com.noljanolja.android.common.ads.nativeads

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import co.touchlab.kermit.Logger
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DynamicNative(
    val unitId: String = "ca-app-pub-3940256099942544/2247696110",
    var enable: StateFlow<Boolean> = MutableStateFlow(true),
) {
    var currentNativeAd: NativeAd? = null

    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun render(
        nativeViewMapper: NativeViewMapper,
        modifier: Modifier = Modifier.fillMaxWidth(),
        shimmer: Boolean = true,
        isShow: Boolean = true,
        shimmerColor: Int = Color.parseColor("#f8f8f8"),
    ) {
        Logger.d("isShow render: $isShow")
        val enableAds by enable.collectAsState()
        if (enableAds) {
            val context = LocalContext.current as Activity
            var nativeAdView: NativeAdView? by remember { mutableStateOf(null) }

            AndroidView(
                modifier = modifier,
                factory = {
                    ShimmerFrameLayout(it).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        val placeholder = View(it).apply {
                            if (shimmer) {
                                layoutParams = LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                )
                                setBackgroundColor(shimmerColor)
                            } else {
                                layoutParams = LinearLayout.LayoutParams(0, 0)
                            }
                        }
                        addView(placeholder)
                    }
                },
                update = {
                    if (nativeAdView != null) {
                        it.removeAllViews()
                        it.hideShimmer()
                        it.addView(
                            nativeAdView
                        )
                    }
                }
            )

            LaunchedEffect(isShow) {
                loadAdAsync(context) { nativeAd ->
                    // You must call destroy on old ads when you are done with them,
                    // otherwise you will have a memory leak.
                    Logger.d("Native ad populating..")
                    if (isShow) {
                        nativeAdView = bindingAdNative(nativeAd, nativeViewMapper)
                    }
                }
            }
        }
    }

    private fun bindingAdNative(
        nativeAd: NativeAd,
        viewMapper: NativeViewMapper,
    ): NativeAdView {
        val nativeAdView = viewMapper.getNativeAdView()

        // binding Custom View to NativeAdView, so NativeAdView can track all the changed, and send it to Google SDK.
        nativeAdView.mediaView = viewMapper.getMediaView()
        nativeAdView.headlineView = viewMapper.getHeadlineView()
        nativeAdView.bodyView = viewMapper.getBodyView()
        nativeAdView.callToActionView = viewMapper.getButtonView()
        nativeAdView.iconView = viewMapper.getAppIconView()
        nativeAdView.priceView = viewMapper.getPriceView()
        nativeAdView.starRatingView = viewMapper.getRatingView()
        nativeAdView.storeView = viewMapper.getStoreView()
        nativeAdView.advertiserView = viewMapper.getAdvertiserView()

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        viewMapper.getHeadlineView().text = nativeAd.headline
        nativeAd.mediaContent?.let { viewMapper.getMediaView()?.mediaContent = it }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            viewMapper.getBodyView()?.visibility = View.INVISIBLE
        } else {
            viewMapper.getBodyView()?.visibility = View.VISIBLE
            viewMapper.getBodyView()?.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            viewMapper.getButtonView().visibility = View.INVISIBLE
        } else {
            viewMapper.getButtonView().visibility = View.VISIBLE
            viewMapper.setButtonContent(nativeAd.callToAction)
        }

        if (nativeAd.icon == null) {
            viewMapper.getAppIconView()?.visibility = View.GONE
        } else {
            viewMapper.getAppIconView()?.setImageDrawable(nativeAd.icon?.drawable)
            viewMapper.getAppIconView()?.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            viewMapper.getPriceView()?.visibility = View.INVISIBLE
        } else {
            viewMapper.getPriceView()?.visibility = View.VISIBLE
            viewMapper.getPriceView()?.text = nativeAd.price
        }

        if (nativeAd.store == null) {
            viewMapper.getStoreView()?.visibility = View.INVISIBLE
        } else {
            viewMapper.getStoreView()?.visibility = View.VISIBLE
            viewMapper.getStoreView()?.text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            viewMapper.getRatingView()?.visibility = View.INVISIBLE
        } else {
            viewMapper.getRatingView()?.rating = nativeAd.starRating!!.toFloat()
            viewMapper.getRatingView()?.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            viewMapper.getAdvertiserView()?.visibility = View.INVISIBLE
        } else {
            viewMapper.getAdvertiserView()?.text = nativeAd.advertiser
            viewMapper.getAdvertiserView()?.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        nativeAdView.setNativeAd(nativeAd)

        return viewMapper.getNativeAdView()
    }

    private fun loadAdAsync(context: Activity, onBinding: (NativeAd) -> Unit) {
        if (currentNativeAd != null) {
            onBinding(currentNativeAd!!)
            return
        }
        val builder = AdLoader.Builder(context, unitId)
        builder.forNativeAd { nativeAd ->
            // OnUnifiedNativeAdLoadedListener implementation.
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            var activityDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                activityDestroyed = context.isDestroyed
            }
            if (activityDestroyed || context.isFinishing || context.isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            currentNativeAd?.destroy()
            currentNativeAd = nativeAd
            onBinding(currentNativeAd!!)
            currentNativeAd!!.setOnPaidEventListener { p0 ->
                Logger.d("Ad_native_paid ${p0.valueMicros} ${p0.currencyCode}")
                if (p0.valueMicros > 0) {
                    Firebase.analytics.logEvent("Ad_native_paid_valid") {}
                } else {
                    Firebase.analytics.logEvent("Ad_native_paid_0") {}
                }
            }
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder
            .withAdListener(
                object : AdListener() {

                    override fun onAdOpened() {
                        super.onAdOpened()
                        Logger.d("Ad_native_opened")
                        currentNativeAd = null
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        Logger.v("Ad_native_clicked")
                        Firebase.analytics.logEvent("Ad_native_clicked") {
                            param("unit_id", unitId)
                        }
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
//                            Toast.makeText(context, "Impressed", Toast.LENGTH_SHORT).show()
                        Logger.v("Ad_native_impression")
                        Firebase.analytics.logEvent("Ad_native_impression") {
                            param("unit_id", unitId)
                        }
                        currentNativeAd = null
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Logger.d("Ad_native_loaded")
                        Firebase.analytics.logEvent("Ad_native_loaded") {
                            param("unit_id", unitId)
                        }
//                            Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Logger.d("Ad_native_failed $loadAdError")
                        Firebase.analytics.logEvent("Ad_native_load_failed") {
                            param("unit_id", unitId)
                            param("message", loadAdError.message)
                            param("code", loadAdError.code.toLong())
                            param("cause", loadAdError.cause.toString())
                        }
                    }
                }
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    interface NativeViewMapper {
        // Must have
        fun getNativeAdView(): NativeAdView
        fun getHeadlineView(): TextView
        fun getButtonView(): View
        fun setButtonContent(text: String?)

        // Optional
        fun getMediaView(): MediaView? = null
        fun getBodyView(): TextView? = null
        fun getAppIconView(): ImageView? = null
        fun getPriceView(): TextView? = null
        fun getStoreView(): TextView? = null
        fun getRatingView(): RatingBar? = null
        fun getAdvertiserView(): TextView? = null

        fun setLayoutContent(radius: Float? = null, color: Int? = null) = run { }

        fun getLayoutHeight() = 60
    }
}
