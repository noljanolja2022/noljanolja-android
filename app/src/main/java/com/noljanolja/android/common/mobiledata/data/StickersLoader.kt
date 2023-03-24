package com.noljanolja.android.common.mobiledata.data

import android.content.Context
import android.content.res.AssetManager
import com.noljanolja.core.CoreManager
import com.noljanolja.core.media.domain.model.StickerPack
import com.noljanolja.core.utils.addOrReplace
import com.noljanolja.core.utils.default
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Load all stickers inside app data
 *
 * The data structure:
 * /
 *  stickers
 *          /sticker_id
 *                     /sticker_id.png <-- tray image
 *                     /xxx.webp <-- stickers
 *                     /contents.json <-- sticker pack info
 */
class StickersLoader(
    private val context: Context,
    private val coreManager: CoreManager,
) {
    private val json = Json.default()
    private val assets: AssetManager = context.assets
    private val stickersPath = "stickers"

    private val _stickerPacks: MutableStateFlow<List<StickerPack>> = MutableStateFlow(emptyList())

    val stickerPacks: StateFlow<List<StickerPack>> = _stickerPacks.asStateFlow()

    suspend fun loadAllRemoteStickerPackages() {
        withContext(Dispatchers.IO) {
            coreManager.loadAllStickerPacks().map {
                it.copy(stickers = emptyList())
            }.also {
                _stickerPacks.emit(it)
            }
            loadStickerPacks(true)
        }
    }

    fun downloadStickerPack(pack: StickerPack) {
        MainScope().launch {
            try {
                updateStickerPack(pack.copy(downloading = true))
                withContext(Dispatchers.IO) {
                    File(context.filesDir, stickersPath).apply {
                        if (!exists()) mkdir()
                    }

                    val mediaTempDir = File(context.externalCacheDir, stickersPath).apply {
                        if (!exists()) mkdir()
                    }
                    val mediaTempFile = getMedia(mediaTempDir, pack.id.toString(), true).apply {
                        if (!exists()) createNewFile()
                    }
                    coreManager.downloadStickerPack(pack.id).toList().forEach {
                        mediaTempFile.appendBytes(it)
                    }
                    unzip(
                        mediaTempFile.inputStream(),
                        File(
                            "${context.filesDir}${File.separator}$stickersPath",
                            pack.id.toString()
                        )
                    )
                    loadStickerPacks(true)
                }
            } catch (error: Exception) {
                error.printStackTrace()
                updateStickerPack(pack.copy(downloading = false))
            }
        }
    }

    suspend fun initStickerPacks() = coroutineScope {
        withContext(Dispatchers.IO) {
            try {
                assets.list(stickersPath)?.forEach {
                    assets.open("$stickersPath${File.separator}$it").use { stickerPack ->
                        unzip(stickerPack, File(context.filesDir, stickersPath))
                    }
                }
            } catch (error: Throwable) {
                error.printStackTrace()
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun loadStickerPacks(force: Boolean = false) = coroutineScope {
        val stickerPacks = _stickerPacks.value.toMutableList()
        withContext(Dispatchers.IO) {
            if (force || stickerPacks.isEmpty()) {
                try {
                    val stickersDir = File(context.filesDir, stickersPath)
                    stickersDir.list()?.forEach {
                        val stickerDir = File(stickersDir, it)
                        File(stickerDir, "contents.json").inputStream().use { inputStream ->
                            val stickerPack = json.decodeFromStream<StickerPack>(inputStream)
                            stickerPacks.addOrReplace(
                                stickerPack.copy(
                                    trayImageFile = File(
                                        stickerDir,
                                        stickerPack.trayImageFile
                                    ).path,
                                    stickers = stickerPack.stickers.map { sticker ->
                                        sticker.copy(
                                            imageFile = File(
                                                stickerDir,
                                                sticker.imageFile
                                            ).path
                                        ).apply {
                                            message = "${stickerPack.id}/${sticker.imageFile}"
                                        }
                                    }
                                ),
                                map = { it.id }
                            )
                        }
                    }
                    _stickerPacks.emit(stickerPacks)
                } catch (error: Throwable) {
                    error.printStackTrace()
                }
            }
        }
    }

    private fun unzip(zipFile: InputStream, outputDir: File) {
        outputDir.takeIf { !it.exists() }?.mkdir()

        var entry: ZipEntry?
        var readLen: Int
        val readBuffer = ByteArray(4096)

        ZipInputStream(zipFile).use { zipInputStream ->
            while (zipInputStream.nextEntry.also { entry = it } != null) {
                try {
                    val file = File(outputDir, entry!!.name)
                    if (entry!!.isDirectory) {
                        file.takeIf { !it.exists() }?.mkdir()
                    } else {
                        file.takeIf { !it.exists() }?.createNewFile()
                        FileOutputStream(file).use { outputStream ->
                            while (zipInputStream.read(readBuffer).also { readLen = it } != -1) {
                                outputStream.write(readBuffer, 0, readLen)
                            }
                        }
                    }
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }
        }
    }

    private fun getMedia(parentDir: File, key: String, deleteOnExit: Boolean): File {
        key.split("/").let {
            var childDir: File? = null
            it.forEachIndexed { index, path ->
                childDir = when (index) {
                    0 -> File(parentDir, path).apply { if (!exists()) mkdir() }
                    it.lastIndex -> childDir
                    else -> File(childDir, path).apply { if (!exists()) mkdir() }
                }
            }
            return File(childDir, it.last()).apply { if (deleteOnExit) deleteOnExit() }
        }
    }

    private suspend fun updateStickerPack(stickerPack: StickerPack) {
        val stickerPacks = _stickerPacks.value.toMutableList()
        stickerPacks.addOrReplace(stickerPack) { it.id }
        _stickerPacks.emit(stickerPacks)
    }
}