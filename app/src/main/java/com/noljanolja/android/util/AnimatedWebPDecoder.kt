package com.noljanolja.android.util

import coil.ImageLoader
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.decode.ImageSource
import coil.fetch.SourceResult
import coil.request.Options
import com.github.penfeizhou.animation.webp.WebPDrawable
import com.github.penfeizhou.animation.webp.decode.WebPParser

class AnimatedWebPDecoder(private val source: ImageSource) : Decoder {

    override suspend fun decode(): DecodeResult {
        return DecodeResult(
            drawable = WebPDrawable.fromFile(source.file().toFile().path),
            isSampled = false
        )
    }

    class Factory : Decoder.Factory {

        override fun create(
            result: SourceResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Decoder? {
            if (WebPParser.isAWebP(result.source.file().toFile().path)) {
                return AnimatedWebPDecoder(result.source)
            } else {
                return null
            }
        }
    }
}