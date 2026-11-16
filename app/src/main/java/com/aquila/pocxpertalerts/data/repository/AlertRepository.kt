package com.aquila.pocxpertalerts.data.repository

import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.data.remote.ApiService
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Represents an option returned by a backend dropdown API.
 */
data class ApiDropdownOption(
    val id: String,
    val name: String
)

class AlertRepository(
    private val apiService: ApiService
) {

    private val gson = Gson()

    /**
     * Fetch alerts using the existing getAlertsForDevices API.
     *
     * No mock data is used. Results come from the backend.
     */
    suspend fun getAlerts(
        userId: String,
        startDate: String,
        endDate: String,
        searchByKeyword: String,
        viewByFlag: String = "0",
        applicationId: String = "0",
        alertDefinitionId: String = "0",
        sessionId: String
    ): Result<List<AlertDto>> = withContext(Dispatchers.IO) {

        try {
            val parameters = mapOf(
                "userId" to userId,
                "startDate" to startDate,
                "endDate" to endDate,
                "searchByKeyword" to searchByKeyword,
                "viewByFlag" to viewByFlag.ifBlank { "0" },
                "applicationId" to applicationId.ifBlank { "0" },
                "alertDefinitionId" to alertDefinitionId.ifBlank { "0" },
                "sessionId" to sessionId
            )




            val response = apiService.getAlertsForDevices(parameters)



            val alerts = parseAlertsResponse(response)

            Result.success(alerts)

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    /**
     * Fetch applications from the backend for the logged-in user.
     */
    suspend fun getApplications(
        userId: String,
        userTypeId: String,
        sessionId: String
    ): Result<List<ApiDropdownOption>> = withContext(Dispatchers.IO) {

        try {
            val response = apiService.getApplicationList(
                userId = userId,
                userTypeId = userTypeId,
                sessionId = sessionId
            )



            parseDropdownResponse(
                response = response,
                idKeys = listOf(
                    "id",
                    "applicationId",
                    "applicationID"
                ),
                nameKeys = listOf(
                    "name",
                    "applicationName",
                    "application"
                )
            )

        } catch (exception: Exception) {



            Result.failure(exception)
        }
    }

    /**
     * Fetch alert definitions for the selected application.
     *
     * The application ID must be the ID returned by the backend,
     * not the application's display name.
     */
    suspend fun getAlertDefinitions(
        applicationId: String,
        sessionId: String
    ): Result<List<ApiDropdownOption>> = withContext(Dispatchers.IO) {

        try {
            val response = apiService.getAlertDefinitionList(
                applicationId = applicationId,
                alertFlag = "1",
                sessionId = sessionId
            )



            parseDropdownResponse(
                response = response,
                idKeys = listOf(
                    "id",
                    "alertDefinitionId",
                    "alertDefinitionID",
                    "definitionId"
                ),
                nameKeys = listOf(
                    "name",
                    "alertDefinitionName",
                    "definitionName",
                    "alertDefinition",
                    "description"
                )
            )

        } catch (exception: Exception) {



            Result.failure(exception)
        }
    }

    /**
     * Parse the getAlertsForDevices response into AlertDto objects.
     */
    private fun parseAlertsResponse(
        response: JsonElement
    ): List<AlertDto> {

        if (response.isJsonArray) {
            return parseAlertArray(response.asJsonArray)
        }

        if (!response.isJsonObject) {
            throw IllegalStateException(
                "Unexpected alerts response format: $response"
            )
        }

        val jsonObject = response.asJsonObject

        // Check whether the backend returned a session/error response.
        val errorCode = jsonObject.get("errorCode")
            ?.takeUnless { it.isJsonNull }
            ?.let {
                runCatching { it.asInt }.getOrNull()
            }

        if (errorCode == 412) {
            val message = jsonObject.get("errorMessage")
                ?.takeUnless { it.isJsonNull }
                ?.asString
                ?: "Session expired. Please log in again."

            throw IllegalStateException(message)
        }

        // Some backend responses wrap the alerts array in an object.
        val possibleArrayKeys = listOf(
            "alerts",
            "data",
            "results",
            "alertList"
        )

        for (key in possibleArrayKeys) {
            val value = jsonObject.get(key)

            if (value != null && value.isJsonArray) {
                return parseAlertArray(value.asJsonArray)
            }
        }

        val errorMessage = jsonObject.get("errorMessage")
            ?.takeUnless { it.isJsonNull }
            ?.asString

        if (!errorMessage.isNullOrBlank()) {
            throw IllegalStateException(errorMessage)
        }

        throw IllegalStateException(
            "The backend did not return an alerts array."
        )
    }

    /**
     * Convert a JSON array into AlertDto objects.
     */
    private fun parseAlertArray(
        jsonArray: JsonArray
    ): List<AlertDto> {

        return jsonArray.mapNotNull { element ->

            if (!element.isJsonObject) {
                return@mapNotNull null
            }

            val item = element.asJsonObject

            val errorCode = item.get("errorCode")
                ?.takeUnless { it.isJsonNull }
                ?.let {
                    runCatching { it.asInt }.getOrNull()
                }

            if (errorCode == 412) {
                val message = item.get("errorMessage")
                    ?.takeUnless { it.isJsonNull }
                    ?.asString
                    ?: "Session expired. Please log in again."

                throw IllegalStateException(message)
            }

            gson.fromJson(item, AlertDto::class.java)
        }
    }

    /**
     * Parse application and alert-definition dropdown responses.
     *
     * This function must be inside AlertRepository.
     */
    private fun parseDropdownResponse(
        response: JsonElement,
        idKeys: List<String>,
        nameKeys: List<String>
    ): Result<List<ApiDropdownOption>> {

        if (!response.isJsonArray) {

            val message = "Unexpected server response for dropdown data."



            return Result.failure(
                IllegalStateException(message)
            )
        }

        val options = response.asJsonArray.mapNotNull { element ->

            if (!element.isJsonObject) {
                return@mapNotNull null
            }

            val item: JsonObject = element.asJsonObject

            fun valueFor(keys: List<String>): String? {
                return keys.firstNotNullOfOrNull { key ->

                    item.get(key)
                        ?.takeUnless { it.isJsonNull }
                        ?.let { value ->
                            if (value.isJsonPrimitive) {
                                value.asString
                            } else {
                                null
                            }
                        }
                        ?.takeIf { it.isNotBlank() }
                }
            }

            val id = valueFor(idKeys)
            val name = valueFor(nameKeys)

            if (id != null && name != null) {

                ApiDropdownOption(
                    id = id,
                    name = name
                )

            } else {






                null
            }
        }






        return Result.success(options)
    }
}