package com.noljanolja.android.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import co.touchlab.kermit.Logger
import coil.Coil
import coil.request.ImageRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.noljanolja.android.MainActivity
import com.noljanolja.android.MyApplication
import com.noljanolja.android.R
import com.noljanolja.core.CoreManager
import com.noljanolja.core.conversation.domain.model.ConversationType
import com.noljanolja.core.conversation.domain.model.MessageType
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class MyFirebaseService : FirebaseMessagingService() {

    private val coreManager: CoreManager by inject()
    private val scope = MainScope()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Logger.d("onMessageReceived: ${message.data}")

        scope.launch {
            displayNotification(message.data)
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

    private suspend fun displayNotification(data: Map<String, String>) {
        val conversationId = data["conversationId"]?.toLong() ?: 0
        if (!isShowNotification(conversationId)) return

        val channelId = getString(R.string.app_channel_id)
        val notificationManager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
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
        notificationManager.notify(conversationId.toInt(), notification)
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
            ?: AppCompatResources.getDrawable(this, R.drawable.placeholder_avatar)!!.toBitmap()

        val sender = Person.Builder()
            .setIcon(IconCompat.createWithBitmap(senderIcon))
            .setName(senderName)
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
            .setContentIntent(MainActivity.getPendingIntent(this, conversationId.toString()))
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

    private fun isShowNotification(conversationId: Long): Boolean {
        return !with(MyApplication) { conversationId == latestConversationId && isAppInForeground }
    }
}
