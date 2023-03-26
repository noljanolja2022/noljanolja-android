package com.noljanolja.android.util

import android.os.Build
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

fun ImageRequest.Builder.setAnimated(animated: Boolean = false) = apply {
    if (animated) {
        decoderFactory(
            if (Build.VERSION.SDK_INT >= 28) {
                ImageDecoderDecoder.Factory()
            } else {
                GifDecoder.Factory()
                AnimatedWebPDecoder.Factory()
            }
        )
    }
}
