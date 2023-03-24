package com.noljanolja.core.media.domain.repository

import com.noljanolja.core.media.domain.model.StickerPack
import kotlinx.coroutines.flow.Flow

internal interface MediaRepository {
    suspend fun loadAllStickerPacks(): List<StickerPack>

    suspend fun downloadStickerPack(id: Long): Flow<ByteArray>
}