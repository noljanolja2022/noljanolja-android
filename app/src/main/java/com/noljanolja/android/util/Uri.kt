package com.noljanolja.android.util

import android.net.Uri

fun String.toUri(): Uri = if (this.startsWith("content:/")) {
    Uri.parse(this.replace("content:/", "content://"))
} else {
    Uri.parse(this)
}