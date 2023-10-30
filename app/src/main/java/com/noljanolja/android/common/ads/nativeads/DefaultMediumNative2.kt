package com.noljanolja.android.common.ads.nativeads

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.gms.ads.nativead.NativeAdView
import com.noljanolja.android.databinding.NativeMd2LayoutBinding
import com.noljanolja.android.util.toDp

open class DefaultMediumNative2(val context: Activity) : DynamicNative.NativeViewMapper {
    private val medium2NativeBinding: NativeMd2LayoutBinding =
        NativeMd2LayoutBinding.inflate(context.layoutInflater)

    @ColorInt
    var defaultBackgroundColor: Int = 0

    init {
        medium2NativeBinding.cardContainer.setCardBackgroundColor(defaultBackgroundColor)
    }

    override fun setLayoutContent(radius: Float?, color: Int?) {
        color?.let { medium2NativeBinding.cardContainer.setCardBackgroundColor(it) }
        radius?.let { medium2NativeBinding.cardContainer.radius = it.toDp(context) }
    }

    override fun getNativeAdView(): NativeAdView {
        return medium2NativeBinding.root
    }

    override fun getAppIconView(): ImageView? {
        return medium2NativeBinding.adAppIcon
    }

    override fun getHeadlineView(): TextView {
        return medium2NativeBinding.adHeadline
    }

    override fun getBodyView(): TextView? {
        return medium2NativeBinding.adBody
    }

    override fun getButtonView(): View {
        return medium2NativeBinding.adCallToAction
    }

    override fun setButtonContent(text: String?) {
        medium2NativeBinding.adCallToActionText.text = text
    }

    override fun getRatingView(): RatingBar? {
        return medium2NativeBinding.adStars
    }
}
