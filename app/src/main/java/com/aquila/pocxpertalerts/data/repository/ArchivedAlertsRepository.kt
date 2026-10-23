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



data class ArchivedDynamicAction(
    val caption: String,
    val action: String,
    val amsAction: String,
    val confirmationPrompt: String
)

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
            Log.d(
                "ArchivedAlertsAPI",
                "Raw archived alerts response: $response"
            )

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


    suspend fun getDeleteAction(
        userId: String,
        sessionId: String,
        applicationId: Long,
        alertDefId: Int
    ): Result<ArchivedDynamicAction> = withContext(Dispatchers.IO) {
        try {
            require(userId.isNotBlank()) { "User ID is missing." }
            require(sessionId.isNotBlank()) { "Session ID is missing." }
            require(applicationId > 0L) { "Application ID is missing." }
            require(alertDefId > 0) { "Alert definition ID is missing." }

            val response = apiService.getAlertButtonDetails(
                applicationId = applicationId.toString(),
                alertDefId = alertDefId.toString(),
                userId = userId,
                sessionId = sessionId
            )
            Log.d(
                "ArchivedDelete",
                "getAlertButtonDetails response: $response"
            )

            require(response.isJsonArray && response.asJsonArray.size() > 0) {
                "The server did not return any available actions."
            }

            val obj = response.asJsonArray[0].asJsonObject
            val errorCode = obj.get("errorCode")
                ?.takeUnless { it.isJsonNull }
                ?.let { runCatching { it.asInt }.getOrNull() } ?: 0

            if (errorCode == 412) {
                throw IllegalStateException(
                    "Your session has expired. Please sign in again."
                )
            }
            if (errorCode > 0) {
                throw IllegalStateException(
                    obj.get("message")?.takeUnless { it.isJsonNull }
                        ?.asString ?: "Unable to retrieve available actions."
                )
            }

            val captions = obj.get("captions")
                ?.takeUnless { it.isJsonNull }?.asString.orEmpty()
                .split(",")
            val actions = obj.get("actions")
                ?.takeUnless { it.isJsonNull }?.asString.orEmpty()
                .split(",")
            val amsActions = obj.get("AmsActionIds")
                ?.takeUnless { it.isJsonNull }?.asString.orEmpty()
                .split(",")
            val prompts = obj.get("confirmPrompt")
                ?.takeUnless { it.isJsonNull }?.asString.orEmpty()
                .split(",")

            val deleteIndex = captions.indexOfFirst {
                it.trim().equals("Dismiss", ignoreCase = true)

            }

            require(
                deleteIndex >= 0 &&
                        deleteIndex < actions.size &&
                        deleteIndex < amsActions.size &&
                        deleteIndex < prompts.size
            ) {
                "The server did not provide a usable Dismiss action."
            }

            Result.success(
                ArchivedDynamicAction(
                    caption = captions[deleteIndex].trim(),
                    action = actions[deleteIndex].trim(),
                    amsAction = amsActions[deleteIndex].trim(),
                    confirmationPrompt = prompts[deleteIndex].trim()
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeArchivedAction(
        userId: String,
        sessionId: String,
        applicationId: Long,
        archiveId: Long,
        alertId: Int,
        aggregateAlertId: String,
        action: ArchivedDynamicAction
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            require(userId.isNotBlank()) { "User ID is missing." }
            require(sessionId.isNotBlank()) { "Session ID is missing." }
            require(applicationId > 0L) { "Application ID is missing." }
            require(archiveId > 0L) {
                "The archived record ID is missing. The alert was not deleted."
            }

            val response = apiService.processDynamicActions(
                userId = userId,
                applicationId = applicationId.toString(),
                captions = action.caption,
                actions = action.action,
                amsActions = action.amsAction,
                ids = archiveId.toString(),
                alertIds = alertId.toString(),
                aggregateAlertIds = aggregateAlertId,
                sessionId = sessionId
            )

            require(response.isJsonArray && response.asJsonArray.size() > 0) {
                "The server returned an unexpected delete response."
            }

            val first = response.asJsonArray[0]
            require(first.isJsonObject) {
                "The server returned an unexpected delete response."
            }

            val obj = first.asJsonObject
            val errorCode = obj.get("errorCode")
                ?.takeUnless { it.isJsonNull }
                ?.let { runCatching { it.asInt }.getOrNull() } ?: 0

            if (errorCode == 412) {
                throw IllegalStateException(
                    "Your session has expired. Please sign in again."
                )
            }
            if (errorCode > 0) {
                throw IllegalStateException(
                    obj.get("message")?.takeUnless { it.isJsonNull }
                        ?.asString ?: "The server could not delete this alert."
                )
            }

            val returnFlag = obj.get("returnFlag")
                ?.takeUnless { it.isJsonNull }
                ?.let { runCatching { it.asLong }.getOrNull() } ?: 0L

            if (returnFlag <= 0L) {
                throw IllegalStateException(
                    "The server did not confirm that the action succeeded."
                )
            }

            Result.success("Archived alert deleted successfully.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
