package com.noljanolja.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class TurnOffReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = NotificationManagerCompat.from(context)
        val notificationId = intent.getIntExtra("notificationId", 0)
        notificationManager.cancel(notificationId)
    }
}