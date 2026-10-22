package com.aquila.pocxpertalerts.data.repository

import android.net.Uri
import android.util.Log
import com.aquila.pocxpertalerts.data.model.ArchivedAlertDto
import com.aquila.pocxpertalerts.data.remote.ApiService
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.aquila.pocxpertalerts.data.repository.ArchivedSearchCriteria

class ArchivedAlertsRepository(private val apiService: ApiService) {
    suspend fun getArchivedAlerts(
        userId: String,
        sessionId: String,
        criteria: ArchivedSearchCriteria
    ): Result<List<ArchivedAlertDto>> = withContext(Dispatchers.IO) {
        try {
            require(userId.isNotBlank()) { "User ID is missing. Please sign in again." }
            require(sessionId.isNotBlank()) { "Session ID is missing. Please sign in again." }

            // Match the legacy API's status values and query parameter names.
            val deviceStatus = when (criteria.deviceStatusSelection) {
                1 -> 0
                2 -> 1
                3 -> 2
                4 -> 3
                5 -> 4
                6 -> 5
                else -> 6
            }
            val emailStatus = when (criteria.emailStatusSelection) {
                1 -> 1
                2 -> 2
                3 -> 3
                4 -> 500
                else -> 0
            }
            val start = criteria.startDate.ifBlank { defaultDate(daysBack = 30) }
            val end = criteria.endDate.ifBlank { defaultDate(daysBack = 0) }

            val response = apiService.getArchivedAlertsForDevices(
                userId = userId,
                startDate = start,
                endDate = end,
                searchByKeyword = criteria.keyword.trim(),
                viewByFlag = criteria.viewByFlag.toString(),
                status = deviceStatus.toString(),
                emailStatus = emailStatus.toString(),
                sessionId = sessionId
            )
            Log.d("ArchivedAlertsAPI", "Archived alerts response received")

            Result.success(parseResponse(response))
        } catch (e: Exception) {
            Log.e("ArchivedAlertsAPI", "Failed to load archived alerts", e)
            Result.failure(e)
        }
    }

    private fun parseResponse(response: JsonElement): List<ArchivedAlertDto> {
        if (response.isJsonArray) {
            val array = response.asJsonArray
            val sessionError = array.firstOrNull { it.isJsonObject }
                ?.asJsonObject?.get("errorCode")
                ?.let { runCatching { it.asInt }.getOrNull() }
            if (sessionError == 412) throw IllegalStateException("Your session has expired. Please log in again.")
            return array.mapNotNull { element ->
                if (element.isJsonObject) ArchivedAlertDto.fromJson(element.asJsonObject) else null
            }.filterNot { it.errorCode == 412 }
        }
        if (response.isJsonObject) {
            val obj = response.asJsonObject
            val errorCode = obj.get("errorCode")?.let { runCatching { it.asInt }.getOrNull() }
            if (errorCode == 412) {
                throw IllegalStateException(obj.get("errorMessage")?.asString ?: "Your session has expired. Please log in again.")
            }
            for (key in listOf("data", "alerts", "results", "archivedAlerts", "alertList")) {
                val value = obj.get(key)
                if (value != null && value.isJsonArray) return parseResponse(value)
            }
            val message = obj.get("errorMessage")?.takeUnless { it.isJsonNull }?.asString
            throw IllegalStateException(message ?: "The server returned an unexpected archived alerts response.")
        }
        throw IllegalStateException("The server returned an unexpected archived alerts response.")
    }

    private fun defaultDate(daysBack: Int): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -daysBack)
        return java.text.SimpleDateFormat("MM-dd-yyyy", java.util.Locale.US).format(calendar.time)
    }
}
