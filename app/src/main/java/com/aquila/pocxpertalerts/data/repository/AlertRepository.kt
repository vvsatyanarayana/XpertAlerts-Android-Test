
package com.aquila.pocxpertalerts.data.repository

import android.util.Log
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.data.remote.ApiService
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AlertRepository(
    private val apiService: ApiService
) {
    private val gson = Gson()

    suspend fun getAlerts(
        userId: String,
        startDate: String,
        endDate: String,
        keyword: String,
        viewByFlag: String,
        applicationId: String,
        alertDefinitionId: String,
        sessionId: String
    ): Result<List<AlertDto>> = withContext(Dispatchers.IO) {
        try {
            val parameters = mapOf(
                "userId" to userId,
                "startDate" to startDate,
                "endDate" to endDate,
                "searchByKeyword" to keyword,
                "viewByFlag" to viewByFlag.ifBlank { "0" },
                "applicationId" to applicationId.ifBlank { "0" },
                "alertDefinitionId" to alertDefinitionId.ifBlank { "0" },
                "sessionId" to sessionId
            )

            Log.d("AlertsAPI", "Request parameters: $parameters")

            val response: JsonElement =
                try {
                    apiService.getAlertsForDevices(parameters)
                } catch (e: HttpException) {
                    val errorBody = e.response()
                        ?.errorBody()
                        ?.string()

                    Log.e(
                        "AlertsAPI",
                        "HTTP ${e.code()} response: $errorBody",
                        e
                    )

                    throw e
                }

            if (response.isJsonArray) {
                val array: JsonArray = response.asJsonArray

                val alerts = array.mapNotNull { element ->
                    if (element.isJsonObject) {
                        gson.fromJson(
                            element,
                            AlertDto::class.java
                        )
                    } else {
                        null
                    }
                }
                Log.d(
                    "AlertsAPI",
                    "First alert errorCode=${alerts.firstOrNull()?.errorCode}"
                )
                if (alerts.firstOrNull()?.errorCode == 412) {
                    Result.failure(
                        Exception(
                            "Your session has expired. Please log in again."
                        )
                    )
                } else {
                    Result.success(alerts)
                }
            } else if (response.isJsonObject) {
                val error = response.asJsonObject

                val message = error.get("message")
                    ?.takeUnless { it.isJsonNull }
                    ?.asString
                    ?: "The server returned an error."

                Result.failure(Exception(message))
            } else {
                Result.failure(
                    Exception(
                        "Unexpected response from the alerts API."
                    )
                )
            }
        } catch (exception: Exception) {
            Log.e(
                "AlertsAPI",
                "Failed to retrieve alerts: ${exception.message}",
                exception
            )

            Result.failure(exception)
        }
    }
}
