package com.noljanolja.android.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.net.*
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.*
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import co.touchlab.kermit.Logger
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.noljanolja.android.MainActivity
import com.noljanolja.android.MyApplication
import com.noljanolja.android.R
import com.noljanolja.android.TurnOffReceiver
import com.noljanolja.android.util.orZero
import com.noljanolja.core.CoreManager
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.conversation.domain.model.MessageType
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.*

class MyFirebaseService : FirebaseMessagingService() {

    private val coreManager: CoreManager by inject()
    private val scope = MainScope()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Logger.d("onMessageReceived: ${message.data}")

        scope.launch {
            val data = message.data
            when {
                data["points"]?.toLongOrNull().orZero() > 0L -> {
                    if (MyApplication.isAppInForeground) {
                        showPointMessage(
                            title = message.notification?.title.orEmpty(),
                            content = message.notification?.title.orEmpty(),
                            senderAvatar = message.notification?.imageUrl
                        )
                    } else {
                        displayMessageNotification(message.data)
                    }
                }

                data["conversationId"]?.toLongOrNull().orZero() > 0L -> {
                    displayMessageNotification(message.data)
                }

                !data["id"].isNullOrBlank() -> {
                    displayPlayVideoNotification(message.data)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        scope.launch {
            coreManager.pushTokens(token)
        }
    }

    private suspend fun displayMessageNotification(data: Map<String, String>) {
        val conversationId = data["conversationId"]?.toLongOrNull() ?: 0
        if (!isShowMessageNotification(conversationId)) return

        val channelId = getString(R.string.app_channel_id)
        val notificationManager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.apply {
                createMessageNotificationChannel(channelId)
            }
        val notification = createMessageNotification(
            channelId = channelId,
            conversationId = conversationId,
            conversationType = data["conversationType"].orEmpty(),
            senderAvatar = data["senderAvatar"].orEmpty(),
            senderName = data["senderName"].orEmpty(),
            messageType = data["messageType"].orEmpty(),
            message = data["message"].orEmpty(),
            messageTime = data["messageTime"]?.toLong() ?: 0,
        )
        notificationManager?.notify(conversationId.toInt(), notification)
    }

    private suspend fun showPointMessage(
        title: String,
        content: String,
        senderAvatar: Uri?
    ) {
        val channelId = getString(R.string.app_channel_id)
        val notificationManager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.apply {
                createMessageNotificationChannel(channelId)
            }

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        val senderIconRequest = ImageRequest.Builder(this)
            .data(senderAvatar)
            .allowHardware(false)
            .build()
        val senderIcon = Coil.imageLoader(this)
            .execute(senderIconRequest)
            .drawable
            ?.let { (it as BitmapDrawable).bitmap }
            ?: AppCompatResources.getDrawable(this, R.drawable.placeholder_account)?.toBitmap()

        val notification = NotificationCompat.Builder(this, channelId)

        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 1,
            intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        notification.run {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(title)
            setLargeIcon(senderIcon)
            setAutoCancel(true)
            setContentText(content)
            setContentIntent(pendingIntent)
        }
        notificationManager?.notify(
            Calendar.getInstance().timeInMillis.toInt(),
            notification.build()
        )
    }

    private suspend fun createMessageNotification(
        channelId: String,
        conversationId: Long,
        conversationType: String,
        senderAvatar: String,
        senderName: String,
        messageType: String,
        message: String,
        messageTime: Long,
    ): Notification {
        val senderIconRequest = ImageRequest.Builder(this)
            .data(senderAvatar)
            .allowHardware(false)
            .build()
        val senderIcon = Coil.imageLoader(this)
            .execute(senderIconRequest)
            .drawable
            ?.let { (it as BitmapDrawable).bitmap }
            ?: AppCompatResources.getDrawable(this, R.drawable.placeholder_account)?.toBitmap()

        val sender = Person.Builder()
            .apply {
                senderIcon?.let {
                    setIcon(IconCompat.createWithBitmap(it))
                }
            }
            .setName(senderName.ifBlank { "Test" })
            .build()

        val messageContent = when (messageType) {
            MessageType.PLAINTEXT.name -> message
            MessageType.PLAINTEXT.name -> "Sticker"
            else -> "New message"
        }

        val messageStyle = NotificationCompat.MessagingStyle(sender)
            .addMessage(messageContent, messageTime, sender)
            .setGroupConversation(conversationType == ConversationType.GROUP.name)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("New message from ${sender.name}")
            .setContentText(messageContent)
            .setSmallIcon(R.drawable.logo)
            .setStyle(messageStyle)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(
                MainActivity.getPendingIntent(
                    this,
                    conversationId.toString(),
                    videoId = ""
                )
            )
            .build()
    }

    private fun NotificationManager.createMessageNotificationChannel(
        channelId: String,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.app_channel_name)
            val channelDescription = getString(R.string.app_channel_description)
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
            }

            createNotificationChannel(channel)
        }
    }

    private fun isShowMessageNotification(conversationId: Long): Boolean {
        return !with(MyApplication) { conversationId == latestConversationId && isAppInForeground }
    }

    private fun displayPlayVideoNotification(data: Map<String, String>) {
        val id = data["id"].orEmpty()
        val url = data["url"].orEmpty()
        val title = data["title"].orEmpty()
        val thumbnail = data["thumbnail"]
        val duration = data["duration"]
        val notificationId = id.hashCode()
        val imageLoader = ImageLoader.Builder(this)
            .crossfade(true)
            .build()
        val channelId = getString(R.string.app_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        notificationBuilder.addVideoNotificationAction(notificationId, id)
        val notificationManager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                createMessageNotificationChannel(channelId)
            }
        val imageRequest = ImageRequest.Builder(this)
            .data(thumbnail)
            .target { drawable ->
                if (drawable is BitmapDrawable) {
                    notificationBuilder.setLargeIcon(drawable.bitmap)
                    notificationBuilder.setStyle(
                        NotificationCompat.BigPictureStyle().bigPicture(drawable.bitmap)
                    )
                    notificationManager.notify(
                        notificationId,
                        notificationBuilder.build()
                    )
                }
            }
            .build()

        imageLoader.enqueue(imageRequest)
    }

    private fun NotificationCompat.Builder.addVideoNotificationAction(
        notificationId: Int,
        videoId: String,
    ) {
        val playIntent = MainActivity.getPendingIntent(
            this@MyFirebaseService,
            conversationId = "",
            videoId = videoId
        )

        addAction(
            R.drawable.ic_play,
            this@MyFirebaseService.getString(R.string.common_play),
            playIntent
        )

        val turnOffIntent = PendingIntent.getBroadcast(
            this@MyFirebaseService,
            0,
            Intent(this@MyFirebaseService, TurnOffReceiver::class.java).apply {
                putExtra("notificationId", notificationId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        addAction(
            com.nhn.android.oauth.R.drawable.close_btn_img,
            this@MyFirebaseService.getString(R.string.common_turn_off),
            turnOffIntent
        )
    }
}
