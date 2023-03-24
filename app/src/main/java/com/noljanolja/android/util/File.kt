package com.noljanolja.android.util

import java.io.File

fun String.checkIfExits() = File(this).exists()

fun String.getFileName() = this.split(File.separator).lastOrNull().orEmpty()