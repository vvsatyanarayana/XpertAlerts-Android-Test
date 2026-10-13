
package com.aquila.pocxpertalerts.data.local

import android.content.Context
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class SavedAlertSearchFilters(
    val keyword: String = "",
    val startDate: String = "10/09/2026",
    val endDate: String = "10/12/2026",
    val viewBy: String = "- Select -",
    val applicationName: String = "- Select Application -",
    val applicationId: String = "",
    val alertDefinitionName: String = "- Select Alert Definition -",
    val alertDefinitionId: String = ""
)

class SearchAlertsPersistence(context: Context) {

    private val preferences = context.getSharedPreferences(
        "saved_alert_searches",
        Context.MODE_PRIVATE
    )

    private val gson = Gson()

    private fun key(userId: String, name: String): String =
        "user_${userId}_$name"

    fun saveAlerts(userId: String, alerts: List<AlertDto>) {
        if (userId.isBlank()) return

        preferences.edit()
            .putString(key(userId, "alerts"), gson.toJson(alerts))
            .apply()
    }

    fun loadAlerts(userId: String): List<AlertDto> {
        if (userId.isBlank()) return emptyList()

        val json = preferences.getString(
            key(userId, "alerts"),
            null
        ) ?: return emptyList()

        return try {
            val type = object : TypeToken<List<AlertDto>>() {}.type
            gson.fromJson<List<AlertDto>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveFilters(userId: String, filters: SavedAlertSearchFilters) {
        if (userId.isBlank()) return

        preferences.edit()
            .putString(key(userId, "filters"), gson.toJson(filters))
            .apply()
    }

    fun loadFilters(userId: String): SavedAlertSearchFilters {
        if (userId.isBlank()) return SavedAlertSearchFilters()

        val json = preferences.getString(
            key(userId, "filters"),
            null
        ) ?: return SavedAlertSearchFilters()

        return try {
            gson.fromJson(json, SavedAlertSearchFilters::class.java)
                ?: SavedAlertSearchFilters()
        } catch (_: Exception) {
            SavedAlertSearchFilters()
        }
    }
}
