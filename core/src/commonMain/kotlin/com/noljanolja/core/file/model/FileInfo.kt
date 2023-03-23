package com.noljanolja.core.file.model

import okio.Path
import okio.Path.Companion.toPath

data class FileInfo(
    val name: String = "",
    val path: Path = "".toPath(),
    val contentType: String = "",
    val contents: ByteArray = ByteArray(0),
)