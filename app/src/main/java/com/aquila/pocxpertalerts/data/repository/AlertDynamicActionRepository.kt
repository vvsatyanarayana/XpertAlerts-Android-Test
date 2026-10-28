
package com.aquila.pocxpertalerts.data.repository

import android.util.Log
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.data.remote.ApiService
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AlertDynamicAction(
    val caption: String,
    val action: String,
    val amsAction: String,
    val confirmationPrompt: String
)

class AlertDynamicActionRepository(
    private val api: ApiService = RetrofitClient.apiService
) {

    companion object {
        private const val TAG = "XPERT_API"
    }

    suspend fun getActions(
        userId: String,
        sessionId: String,
        applicationId: Long,
        alertDefId: Int
    ): Result<List<AlertDynamicAction>> =
        withContext(Dispatchers.IO) {
            runCatching {
                require(userId.isNotBlank() && sessionId.isNotBlank()) {
                    "Your session has expired. Please sign in again."
                }
                require(applicationId > 0L) {
                    "Application ID is missing."
                }
                require(alertDefId > 0) {
                    "Alert definition ID is missing."
                }

                Log.d(
                    TAG,
                    "Calling getAlertButtonDetails: " +
                            "applicationId=$applicationId, " +
                            "alertDefId=$alertDefId, userId=$userId"
                )

                val response = api.getAlertButtonDetails(
                    applicationId = applicationId.toString(),
                    alertDefId = alertDefId.toString(),
                    userId = userId,
                    sessionId = sessionId
                )

                Log.d(TAG, "getAlertButtonDetails response: $response")

                val obj = response.firstObject()
                    ?: error("No action details were returned by the server.")

                val errorCode = obj.intValue("errorCode")

                if (errorCode == 412) {
                    error("Your session has expired. Please sign in again.")
                }

                if (errorCode > 0) {
                    error(
                        obj.stringValue("message").ifBlank {
                            "Unable to load alert actions. Error code: $errorCode"
                        }
                    )
                }

                val captionsValue = obj.stringValue("captions")
                val actionsValue = obj.stringValue("actions")
                val amsActionsValue = obj.stringValue("AmsActionIds")
                val promptsValue = obj.stringValue("confirmPrompt")

                if (captionsValue.isBlank()) {
                    Log.w(
                        TAG,
                        "No action captions returned. " +
                                "applicationId=$applicationId, " +
                                "alertDefId=$alertDefId"
                    )
                    return@runCatching emptyList<AlertDynamicAction>()
                }

                val captions = captionsValue.split(",")
                val actions = actionsValue.split(",")
                val amsActions = amsActionsValue.split(",")
                val prompts = promptsValue.split(",")

                captions.indices.mapNotNull { index ->
                    val caption = captions[index].trim()

                    if (
                        caption.isBlank() ||
                        caption.equals("null", ignoreCase = true)
                    ) {
                        return@mapNotNull null
                    }

                    AlertDynamicAction(
                        caption = caption,
                        action = actions.getOrElse(index) { "" }.trim(),
                        amsAction = amsActions.getOrElse(index) { "" }.trim(),
                        confirmationPrompt = prompts.getOrElse(index) { "" }.trim()
                    )
                }.also { result ->
                    Log.d(
                        TAG,
                        "Parsed dynamic action captions: ${result.map { it.caption }}"
                    )
                }
            }.onFailure { exception ->
                Log.e(TAG, "Failed to load alert button details", exception)
            }
        }

    /**
     * Executes one action for multiple selected alerts.
     * All selected alerts must belong to the same application and alert definition.
     */
    suspend fun executeActionForAlerts(
        userId: String,
        sessionId: String,
        alerts: List<AlertDto>,
        action: AlertDynamicAction
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(userId.isNotBlank() && sessionId.isNotBlank()) {
                "Your session has expired. Please sign in again."
            }
            require(alerts.isNotEmpty()) {
                "No alerts were selected."
            }

            val applicationIds = alerts.map { it.applicationId }.distinct()
            val definitionIds = alerts.map { it.alertDefId }.distinct()

            require(applicationIds.size == 1 && applicationIds.first() > 0L) {
                "Select alerts from the same application."
            }
            require(definitionIds.size == 1 && definitionIds.first() > 0) {
                "Select alerts of the same type."
            }
            require(alerts.all { it.id > 0L }) {
                "One or more alert dispatch IDs are missing."
            }

            val dispatchIds = alerts.joinToString(",") { it.id.toString() }
            val alertIds = alerts.joinToString(",") { it.alertId.toString() }
            val aggregateIds = alerts.joinToString(",") {
                it.aggregateAlertId.orEmpty()
            }

            Log.d(
                TAG,
                "Calling batch processDynamicActions: " +
                        "count=${alerts.size}, " +
                        "applicationId=${applicationIds.first()}, " +
                        "caption=${action.caption}"
            )

            val response = api.processDynamicActions(
                userId = userId,
                applicationId = applicationIds.first().toString(),
                captions = action.caption,
                actions = action.action,
                amsActions = action.amsAction,
                ids = dispatchIds,
                alertIds = alertIds,
                aggregateAlertIds = aggregateIds,
                sessionId = sessionId
            )

            Log.d(TAG, "Batch processDynamicActions response: $response")

            val objects = when {
                response.isJsonArray -> response.asJsonArray.mapNotNull {
                    if (it.isJsonObject) it.asJsonObject else null
                }

                response.isJsonObject -> listOf(response.asJsonObject)

                else -> emptyList()
            }

            require(objects.isNotEmpty()) {
                "The server returned an empty action response."
            }

            val errorObject = objects.firstOrNull {
                it.intValue("errorCode") > 0
            }

            if (errorObject != null) {
                val code = errorObject.intValue("errorCode")
                error(
                    if (code == 412) {
                        "Your session has expired. Please sign in again."
                    } else {
                        errorObject.stringValue("message").ifBlank {
                            "The server could not complete this action. Error code: $code"
                        }
                    }
                )
            }

            val failedObject = objects.firstOrNull {
                it.longValue("returnFlag") <= 0L
            }

            if (failedObject != null) {
                error(
                    failedObject.stringValue("message").ifBlank {
                        "The server did not confirm that the action succeeded."
                    }
                )
            }

            objects.first().stringValue("message").ifBlank {
                "${action.caption} completed successfully."
            }
        }.onFailure { exception ->
            Log.e(
                TAG,
                "Failed to execute batch dynamic action: ${action.caption}",
                exception
            )
        }
    }

    suspend fun executeAction(
        userId: String,
        sessionId: String,
        applicationId: Long,
        dispatchId: Long,
        alertId: Int,
        aggregateAlertId: String,
        action: AlertDynamicAction
    ): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                require(userId.isNotBlank() && sessionId.isNotBlank()) {
                    "Your session has expired. Please sign in again."
                }
                require(applicationId > 0L) {
                    "Application ID is missing."
                }
                require(dispatchId > 0L) {
                    "Alert dispatch ID is missing."
                }

                Log.d(
                    TAG,
                    "Calling processDynamicActions: " +
                            "applicationId=$applicationId, " +
                            "dispatchId=$dispatchId, " +
                            "alertId=$alertId, " +
                            "caption=${action.caption}, " +
                            "amsAction=${action.amsAction}"
                )

                val response = api.processDynamicActions(
                    userId = userId,
                    applicationId = applicationId.toString(),
                    captions = action.caption,
                    actions = action.action,
                    amsActions = action.amsAction,
                    ids = dispatchId.toString(),
                    alertIds = alertId.toString(),
                    aggregateAlertIds = aggregateAlertId,
                    sessionId = sessionId
                )

                Log.d(TAG, "processDynamicActions response: $response")

                val obj = response.firstObject()
                    ?: error("The server returned an empty action response.")

                val errorCode = obj.intValue("errorCode")

                if (errorCode == 412) {
                    error("Your session has expired. Please sign in again.")
                }

                if (errorCode > 0) {
                    error(
                        obj.stringValue("message").ifBlank {
                            "The server could not complete this action. Error code: $errorCode"
                        }
                    )
                }

                if (obj.longValue("returnFlag") <= 0L) {
                    error(
                        obj.stringValue("message").ifBlank {
                            "The server did not confirm that the action succeeded."
                        }
                    )
                }

                obj.stringValue("message").ifBlank {
                    "${action.caption} completed successfully."
                }
            }.onFailure { exception ->
                Log.e(
                    TAG,
                    "Failed to execute dynamic action: ${action.caption}",
                    exception
                )
            }
        }

    private fun JsonElement.firstObject(): JsonObject? =
        when {
            isJsonArray ->
                asJsonArray.firstOrNull { it.isJsonObject }?.asJsonObject

            isJsonObject -> asJsonObject

            else -> null
        }

    private fun JsonObject.stringValue(key: String): String {
        val value = get(key) ?: return ""

        return if (value.isJsonNull) {
            ""
        } else {
            runCatching { value.asString }.getOrDefault("")
        }
    }

    private fun JsonObject.intValue(key: String): Int =
        runCatching {
            stringValue(key).toInt()
        }.getOrDefault(0)

    private fun JsonObject.longValue(key: String): Long =
        runCatching {
            stringValue(key).toLong()
        }.getOrDefault(0L)
}
