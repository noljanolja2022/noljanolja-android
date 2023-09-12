package com.noljanolja

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noljanolja.android.ui.composable.youtube.YoutubeViewWithFullScreen

class VideoBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        YoutubeViewWithFullScreen.togglePlayPause()
    }
}