package com.noljanolja.android.common.ads.nativeads

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.gms.ads.nativead.NativeAdView
import com.noljanolja.android.databinding.NativeSm1LayoutBinding
import com.noljanolja.android.util.toDp

open class DefaultSmallNative1(val context: Activity) : DynamicNative.NativeViewMapper {
    private val smallAdBinding: NativeSm1LayoutBinding =
        NativeSm1LayoutBinding.inflate(context.layoutInflater)

    @ColorInt
    var defaultBackgroundColor: Int = 0xfffffff

    init {
        smallAdBinding.cardContainer.setCardBackgroundColor(defaultBackgroundColor)
    }

    override fun setLayoutContent(radius: Float?, color: Int?) {
        color?.let { smallAdBinding.cardContainer.setCardBackgroundColor(it) }
        radius?.let { smallAdBinding.cardContainer.radius = it.toDp(context) }
    }

    override fun getNativeAdView(): NativeAdView {
        return smallAdBinding.root
    }

    override fun getAppIconView(): ImageView? {
        return smallAdBinding.adAppIcon
    }

    override fun getHeadlineView(): TextView {
        return smallAdBinding.adHeadline
    }

    override fun getRatingView(): RatingBar? {
        return smallAdBinding.adStars
    }

    override fun getBodyView(): TextView? {
        return smallAdBinding.adBody
    }

    override fun getButtonView(): View {
        return smallAdBinding.adCallToAction
    }

    override fun setButtonContent(text: String?) {
        smallAdBinding.adCallToActionText.text = text
    }
}
