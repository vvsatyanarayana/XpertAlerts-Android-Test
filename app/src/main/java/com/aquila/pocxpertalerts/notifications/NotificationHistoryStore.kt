package com.aquila.pocxpertalerts.notifications

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.notificationHistoryDataStore by preferencesDataStore(
    name = "notification_history"
)

data class LocalNotification(
    val id: String,
    val title: String,
    val body: String,
    val receivedAt: Long,
    val alertId: String? = null,
    val isRead: Boolean = false
)

/** Local, account-scoped notification history. The backend has no history endpoint. */
class NotificationHistoryStore(private val context: Context) {
    private fun keyFor(userId: String) = stringPreferencesKey("notifications_$userId")

    fun notifications(userId: String): Flow<List<LocalNotification>> =
        context.notificationHistoryDataStore.data.map { preferences ->
            decode(preferences[keyFor(userId)].orEmpty())
        }

    suspend fun add(userId: String, notification: LocalNotification) {
        if (userId.isBlank()) return
        context.notificationHistoryDataStore.edit { preferences ->
            val key = keyFor(userId)
            val updated = (listOf(notification) + decode(preferences[key].orEmpty())
                .filterNot { it.id == notification.id })
                .take(MAX_ITEMS)
            preferences[key] = encode(updated)
        }
    }

    suspend fun markRead(userId: String, notificationId: String) {
        if (userId.isBlank() || notificationId.isBlank()) return
        context.notificationHistoryDataStore.edit { preferences ->
            val key = keyFor(userId)
            preferences[key] = encode(
                decode(preferences[key].orEmpty()).map { item ->
                    if (item.id == notificationId) item.copy(isRead = true) else item
                }
            )
        }
    }

    suspend fun clear(userId: String) {
        if (userId.isBlank()) return
        context.notificationHistoryDataStore.edit { it.remove(keyFor(userId)) }
    }

    private fun encode(items: List<LocalNotification>): String {
        val array = JSONArray()
        items.forEach { item ->
            array.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("body", item.body)
                    put("receivedAt", item.receivedAt)
                    put("alertId", item.alertId)
                    put("isRead", item.isRead)
                }
            )
        }
        return array.toString()
    }

    private fun decode(raw: String): List<LocalNotification> = runCatching {
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val id = item.optString("id").takeIf { it.isNotBlank() } ?: continue
                add(
                    LocalNotification(
                        id = id,
                        title = item.optString("title", "XpertAlerts"),
                        body = item.optString("body"),
                        receivedAt = item.optLong("receivedAt"),
                        alertId = item.optString("alertId").takeIf { it.isNotBlank() && it != "null" },
                        isRead = item.optBoolean("isRead", false)
                    )
                )
            }
        }
    }.getOrDefault(emptyList())

    private companion object {
        const val MAX_ITEMS = 100
    }
}
