package com.noljanolja.android.common.mobiledata.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.noljanolja.android.services.PermissionChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MediaLoader(
    private val context: Context,
) {
    private val resolver: ContentResolver = context.contentResolver
    private val permissionChecker: PermissionChecker = PermissionChecker(context)

    private val job = SupervisorJob()
    private val coroutineContext = job + Dispatchers.IO

    private val documentPath = "documents"
    private val photoPath = "photos"
//    private val mediaLoaders: MutableMap<String, ControlledRunner<String>> = mutableMapOf()

    private val projections = arrayOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Video.VideoColumns.DURATION,
        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
    )

    private val _albumsFlow = mutableListOf<String>()

    fun getPhotoDir(): File = File(context.getExternalFilesDir(null), photoPath).apply {
        if (!exists()) mkdir()
    }

    fun getDocumentDir(): File = File(context.getExternalFilesDir(null), documentPath).apply {
        if (!exists()) mkdir()
    }

//    suspend fun loadDocument(
//        localPath: String,
//        remoteKey: String,
//    ): String = withContext(coroutineContext) {
//        val document = getMedia(getDocumentDir(), remoteKey, false)
//        if (document.exists()) return@withContext document.path
//
//        val localDocument = File(localPath).takeIf { localPath.isNotBlank() && it.exists() }
//        if (localDocument != null) {
//            copyMedia(localDocument, document)
//            document.path
//        } else {
//            mediaLoaders.getOrPut(remoteKey) { ControlledRunner() }.joinPreviousOrRun {
//                loadMedia(documentPath, remoteKey)?.path.orEmpty()
//            }
//        }
//    }

    suspend fun loadPhoto(
        localPath: String,
        localKey: String,
        remoteKey: String,
    ): String = withContext(coroutineContext) {
        val photo = getMedia(getPhotoDir(), remoteKey, false)
        if (photo.exists()) return@withContext photo.path

        val localPhoto = File(localPath).takeIf { localPath.isNotBlank() && it.exists() }
        if (localPhoto != null) {
            copyMedia(localPhoto, photo)
            photo.path
        } else {
            ""
//            val preloadedPhotoPath = context.imageLoader.diskCache?.get(localKey)?.use {
//                copyMedia(it.data, photo)
//                photo.path
//            }
//            if (preloadedPhotoPath != null) return@withContext preloadedPhotoPath
//
//            mediaLoaders.getOrPut(remoteKey) { ControlledRunner() }.joinPreviousOrRun {
//                loadMedia(photoPath, remoteKey)?.path.orEmpty()
//            }
        }
    }

    // mediaPath: photos / documents
    // mediaKey: conversations/$conversationId/messages/$id/$mediaName
//    private suspend fun loadMedia(
//        mediaPath: String,
//        mediaKey: String,
//    ): File? = withContext(Dispatchers.IO) {
//        try {
//            val mediaDir = File(context.getExternalFilesDir(null), mediaPath).apply {
//                if (!exists()) mkdir()
//            }
//            val mediaFile = getMedia(mediaDir, mediaKey, false)
//            if (!mediaFile.exists()) {
//                val mediaTempDir = File(context.externalCacheDir, mediaPath).apply {
//                    if (!exists()) mkdir()
//                }
//                val mediaTempFile = getMedia(mediaTempDir, mediaKey, true).apply {
//                    if (!exists()) createNewFile()
//                }
//                appMediaManager.downloadMedia(mediaKey, mediaTempFile)
//                moveMedia(mediaTempFile, mediaFile)
//            }
//            mediaFile
//        } catch (error: Throwable) {
//            Logger.e(error) {
//                "loadMedia: $mediaPath - $mediaKey"
//            }
//            null
//        }
//    }

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

    private fun moveMedia(from: File, to: File) {
        copyMedia(from, to)
        from.delete()
    }

    private fun copyMedia(from: File, to: File) {
        var readLen: Int
        val readBuffer = ByteArray(4096)
        FileInputStream(from).use { input ->
            FileOutputStream(to).use { output ->
                while (input.read(readBuffer).also { readLen = it } != -1) {
                    output.write(readBuffer, 0, readLen)
                }
            }
        }
    }

    fun loadMedia(): Flow<Pair<Uri, Long?>> = flow {
        val imageContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val videoContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val selection = (
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
            )
        val queryUri = MediaStore.Files.getContentUri("external")
        if (permissionChecker.canReadExternalStorage()) {
            resolver.query(
                queryUri,
                projections,
                selection,
                null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC",
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val mediaId = cursor.getColumnIndex(projections[0])
                            .takeIf { it >= 0 }?.let { cursor.getLong(it) }

                        val mediaType = cursor.getColumnIndex(projections[2])
                            .takeIf { it >= 0 }?.let { cursor.getInt(it) }

                        val duration = cursor.getColumnIndex(projections[3])
                            .takeIf { it >= 0 }?.let { cursor.getLong(it) }

                        val album = cursor.getColumnIndex(projections[4])
                            .takeIf { it >= 0 }?.let { cursor.getString(it) }
                        album?.let {
                            _albumsFlow.add(it)
                        }

                        mediaId?.let {
                            val mediaUri =
                                if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                                    Uri.withAppendedPath(imageContentUri, "" + mediaId)
                                } else {
                                    Uri.withAppendedPath(videoContentUri, "" + mediaId)
                                }
                            emit(Pair(mediaUri, duration))
                        }
                    } while (cursor.moveToNext())
                }
            }
        }
    }.flowOn(Dispatchers.Default)
}