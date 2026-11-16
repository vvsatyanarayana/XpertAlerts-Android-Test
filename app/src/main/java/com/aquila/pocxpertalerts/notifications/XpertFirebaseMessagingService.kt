package com.aquila.pocxpertalerts.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aquila.pocxpertalerts.MainActivity
import com.aquila.pocxpertalerts.R
import com.aquila.pocxpertalerts.data.local.UserSessionDataStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID

class XpertFirebaseMessagingService : FirebaseMessagingService() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val deviceId = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        ).orEmpty()
        FcmTokenRegistrar.enqueueRegistration(applicationContext, token, deviceId)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val title = message.notification?.title
            ?: data["title"]
            ?: data["subject"]
            ?: getString(R.string.app_name)
        val body = message.notification?.body
            ?: data["body"]
            ?: data["message"]
            ?: data["alertMessage"]
            ?: "You have a new alert."

        val alertId = data["alertId"]
            ?: data["alertID"]
            ?: data["alert_id"]
            ?: data["dispatchId"]
            ?: data["id"]
        val notificationId = message.messageId
            ?: data["notificationId"]
            ?: UUID.randomUUID().toString()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            // Suppress delivery when no authenticated session exists on this device.
            val user = runCatching {
                UserSessionDataStore(applicationContext).readUser()
            }.getOrNull() ?: return@launch
            if (user.sessionId.isNullOrBlank()) return@launch

            runCatching {
                NotificationHistoryStore(applicationContext).add(
                    user.id.toString(),
                    LocalNotification(
                        id = notificationId,
                        title = title,
                        body = body,
                        receivedAt = System.currentTimeMillis(),
                        alertId = alertId
                    )
                )
            }
            showNotification(notificationId, title, body, alertId)
        }
    }

    private fun showNotification(
        notificationId: String,
        title: String,
        body: String,
        alertId: String?
    ) {
        createNotificationChannel()

        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(NotificationIntentKeys.OPEN_NOTIFICATION, true)
            putExtra(NotificationIntentKeys.NOTIFICATION_ID, notificationId)
            putExtra(NotificationIntentKeys.ALERT_ID, alertId)
            putExtra(NotificationIntentKeys.TITLE, title)
            putExtra(NotificationIntentKeys.BODY, body)
        }
        val requestCode = notificationId.hashCode()
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        runCatching {
            NotificationManagerCompat.from(this).notify(requestCode, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "XpertAlerts notifications" }
            )
        }
    }

    private companion object {
        const val CHANNEL_ID = "xpertalerts_alerts"
    }
}
