package com.noljanolja.core.media.domain.model

import com.noljanolja.core.utils.Const
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class StickerPack(
    val id: Long,
    val name: String,
    val publisher: String,
    val trayImageFile: String,
    @SerialName("isAnimated")
    val animatedStickerPack: Boolean = false,
    val stickers: List<Sticker> = listOf(),
    @Transient
    var downloading: Boolean = false,
) {
    fun getImageUrl() =
        "${Const.BASE_URL}/api/v1/media/sticker-packs/$id/$trayImageFile"
}

@Serializable
data class Sticker(
    val imageFile: String,
    val emojis: List<String>,
) {
    var message: String = ""
}