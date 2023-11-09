package com.noljanolja.core.media.data.datasource

import com.noljanolja.core.media.data.model.response.GetStickerPacksResponse
import com.noljanolja.core.utils.BASE_URL
import com.noljanolja.core.utils.download
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow

internal class MediaApi(
    private val client: HttpClient,
) {
    suspend fun loadAllStickerPacks(): GetStickerPacksResponse {
        return client.get("$BASE_URL/api/v1/media/sticker-packs").body()
    }

    suspend fun downloadStickerPack(id: Long): Flow<ByteArray> = client.download(
        "$BASE_URL/api/v1/media/sticker-packs/$id"
    )
}