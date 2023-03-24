package com.noljanolja.core.media.data.model.response

import com.noljanolja.core.base.BaseResponse
import com.noljanolja.core.media.domain.model.StickerPack

@kotlinx.serialization.Serializable
internal data class GetStickerPacksResponse(
    override val code: Int,
    override val message: String,
    override val data: List<StickerPack>?,
) : BaseResponse<List<StickerPack>>()