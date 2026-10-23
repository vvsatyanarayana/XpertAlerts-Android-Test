
package com.aquila.pocxpertalerts.data.repository

import android.content.Context
import com.aquila.pocxpertalerts.data.model.ArchivedAlertDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ArchivedAlertsCache(context: Context) {

    private val preferences = context.getSharedPreferences(
        "archived_alerts_cache",
        Context.MODE_PRIVATE
    )

    private val gson = Gson()

    fun save(
        userId: String,
        criteria: ArchivedSearchCriteria,
        alerts: List<ArchivedAlertDto>
    ) {
        preferences.edit()
            .putString(cacheKey(userId, criteria), gson.toJson(alerts))
            .apply()
    }

    fun load(
        userId: String,
        criteria: ArchivedSearchCriteria
    ): List<ArchivedAlertDto> {
        val json = preferences.getString(cacheKey(userId, criteria), null)
            ?: return emptyList()

        return runCatching {
            val type = object : TypeToken<List<ArchivedAlertDto>>() {}.type
            gson.fromJson<List<ArchivedAlertDto>>(json, type) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    private fun cacheKey(
        userId: String,
        criteria: ArchivedSearchCriteria
    ): String {
        val encodedCriteria = android.util.Base64.encodeToString(
            gson.toJson(criteria).toByteArray(Charsets.UTF_8),
            android.util.Base64.URL_SAFE or
                    android.util.Base64.NO_WRAP or
                    android.util.Base64.NO_PADDING
        )

        return "archived_${userId}_$encodedCriteria"
    }
}
