package com.noljanolja.android.common.ads.nativeads

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import com.noljanolja.android.databinding.NativeMd1LayoutBinding
import com.noljanolja.android.util.toDp

open class DefaultMediumNative1(val context: Activity) : DynamicNative.NativeViewMapper {
    private val medium1NativeBinding: NativeMd1LayoutBinding =
        NativeMd1LayoutBinding.inflate(context.layoutInflater)

    @ColorInt
    var defaultBackgroundColor: Int = 0

    init {
        medium1NativeBinding.cardContainer.setCardBackgroundColor(defaultBackgroundColor)
    }

    override fun setLayoutContent(radius: Float?, color: Int?) {
        color?.let { medium1NativeBinding.cardContainer.setCardBackgroundColor(it) }
        radius?.let { medium1NativeBinding.cardContainer.radius = it.toDp(context) }
    }

    override fun getNativeAdView(): NativeAdView {
        return medium1NativeBinding.root
    }

    override fun getAppIconView(): ImageView? {
        return medium1NativeBinding.adAppIcon
    }

    override fun getHeadlineView(): TextView {
        return medium1NativeBinding.adHeadline
    }

    override fun getButtonView(): View {
        return medium1NativeBinding.adCallToAction
    }

    override fun setButtonContent(text: String?) {
        medium1NativeBinding.adCallToActionText.text = text
    }

    override fun getMediaView(): MediaView? {
        return medium1NativeBinding.adMedia
    }

    override fun getRatingView(): RatingBar? {
        return medium1NativeBinding.adStars
    }
}
