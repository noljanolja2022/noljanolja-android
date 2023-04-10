package com.noljanolja.core.media.data.repository

import com.noljanolja.core.media.data.datasource.MediaApi
import com.noljanolja.core.media.domain.model.StickerPack
import com.noljanolja.core.media.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class MediaRepositoryImpl(
    private val mediaApi: MediaApi,
) : MediaRepository {
    override suspend fun loadAllStickerPacks(): List<StickerPack> {
        return try {
            mediaApi.loadAllStickerPacks().data.orEmpty()
        } catch (error: Throwable) {
            error.printStackTrace()
            emptyList()
        }
    }

    override suspend fun downloadStickerPack(id: Long): Flow<ByteArray> {
        return try {
            mediaApi.downloadStickerPack(id)
        } catch (error: Throwable) {
            error.printStackTrace()
            flow { }
        }
    }
}