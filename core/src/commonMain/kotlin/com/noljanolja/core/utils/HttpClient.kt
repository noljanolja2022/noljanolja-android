package com.noljanolja.core.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val DEFAULT_BUFFER_SIZE = 8 * 1024
suspend fun HttpClient.download(url: String): Flow<ByteArray> = flow {
    val channel: ByteReadChannel = this@download.get(url).body()
    while (!channel.isClosedForRead) {
        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
        while (!packet.isEmpty) {
            emit(packet.readBytes())
        }
    }
}
