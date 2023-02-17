package com.noljanolja.android.services

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.MainActivity
import com.noljanolja.android.common.user.domain.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MyFirebaseService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.body?.let {
            sendNotification(it)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        scope.launch {
            userRepository.sendRegistrationToServer(token)
        }
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = BuildConfig.APPLICATION_ID
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.sym_def_app_icon)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.mipmap.sym_def_app_icon
                    )
                )
                .setContentTitle(channelId)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_menu_close_clear_cancel,
                        "Cancel",
                        PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                        )
                    )
                )
                .addAction(
                    NotificationCompat.Action(
                        R.drawable.sym_call_outgoing,
                        "OK",
                        PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                        )
                    )
                )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}