package com.aquila.pocxpertalerts.notifications

data class NotificationOpenRequest(
    val notificationId: String? = null,
    val alertId: String? = null,
    val title: String? = null,
    val body: String? = null
)

object NotificationIntentKeys {
    const val OPEN_NOTIFICATION = "xpertalerts_open_notification"
    const val NOTIFICATION_ID = "xpertalerts_notification_id"
    const val ALERT_ID = "xpertalerts_alert_id"
    const val TITLE = "xpertalerts_title"
    const val BODY = "xpertalerts_body"
}
