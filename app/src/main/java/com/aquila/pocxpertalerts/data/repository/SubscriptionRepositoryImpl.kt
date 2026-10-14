
package com.aquila.pocxpertalerts.data.repository

import android.util.Log
import com.aquila.pocxpertalerts.data.model.SubscriptionDto
import com.aquila.pocxpertalerts.data.remote.ApiService
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

class SubscriptionRepositoryImpl(
    private val apiService: ApiService,
    private val gson: Gson = Gson()
) : SubscriptionRepository {

    private fun JsonElement.asList(): List<JsonElement> {
        if (isJsonArray) {
            return asJsonArray.toList()
        }

        if (!isJsonObject) {
            return emptyList()
        }

        val obj = asJsonObject

        val possibleKeys = listOf(
            "data",
            "result",
            "results",
            "list",
            "items",
            "records",
            "definitions",
            "alertDefinitions",
            "alertDefinitionList",
            "applicationAlertDefinitions"
        )

        for (key in possibleKeys) {
            val entry = obj.entrySet()
                .firstOrNull { it.key.equals(key, ignoreCase = true) }
                ?.value ?: continue

            if (entry.isJsonArray) {
                return entry.asJsonArray.toList()
            }

            if (entry.isJsonObject) {
                val nested = entry.asList()
                if (nested.isNotEmpty()) {
                    return nested
                }
            }
        }

        return emptyList()
    }

    private fun JsonObject.readString(vararg keys: String): String? {
        for (key in keys) {
            val element = entrySet()
                .firstOrNull { it.key.equals(key, ignoreCase = true) }
                ?.value ?: continue

            if (element.isJsonNull || !element.isJsonPrimitive) {
                continue
            }

            val primitive: JsonPrimitive = element.asJsonPrimitive

            if (
                primitive.isString ||
                primitive.isNumber ||
                primitive.isBoolean
            ) {
                val value = primitive.asString.trim()
                if (value.isNotEmpty()) {
                    return value
                }
            }
        }

        return null
    }

    private fun JsonElement.errorMessage(): String? {
        if (!isJsonObject) return null

        val obj = asJsonObject
        val code = obj.get("errorCode")
            ?.takeIf { !it.isJsonNull }
            ?.asString

        if (code == "412") {
            return "Your session has expired. Please log in again."
        }

        return obj.get("message")
            ?.takeIf { !it.isJsonNull }
            ?.asString
            ?: obj.get("errorMessage")
                ?.takeIf { !it.isJsonNull }
                ?.asString
    }

    private fun JsonElement.checkError() {
        if (!isJsonObject) return

        val obj = asJsonObject
        val code = obj.get("errorCode")
            ?.let {
                if (it.isJsonNull) 0 else it.asInt
            } ?: 0

        if (code != 0) {
            throw IllegalStateException(
                errorMessage()
                    ?: "Subscription request failed (code $code)."
            )
        }
    }

    override suspend fun getSubscriptions(
        userId: String,
        sessionId: String,
        applicationId: String,
        searchName: String,
        searchActive: Int
    ): Result<List<SubscriptionDto>> = runCatching {

        val response = apiService.getUserSubscriptionList(
            userId = userId,
            lowerBound = 0,
            upperBound = 100,
            searchName = searchName,
            applicationId = applicationId,
            searchActive = searchActive,
            sessionId = sessionId
        )

        Log.d(
            "SubscriptionMapping",
            "Raw getUserSubscriptionList response: $response"
        )

        response.checkError()

        response.asList()
            .mapNotNull { item ->
                runCatching {
                    gson.fromJson(
                        item,
                        SubscriptionDto::class.java
                    )
                }.getOrNull()
            }
            .filter { it.errorCode == 0 }
            .also { subscriptions ->
                subscriptions.forEach { subscription ->
                    Log.d(
                        "SubscriptionMapping",
                        "userSubId=${subscription.userSubId}, " +
                                "alertName=${subscription.alertName}, " +
                                "alertDefName=${subscription.alertDefName}, " +
                                "applicationName=${subscription.applicationName}"
                    )
                }
            }
    }

    override suspend fun getSubscriptionDetails(
        userSubId: Long,
        sessionId: String
    ): Result<SubscriptionDto> = runCatching {

        val response = apiService.getUserSubDetailsById(
            userSubId = userSubId,
            sessionId = sessionId
        )

        Log.d(
            "SubscriptionMapping",
            "Raw subscription details response: $response"
        )

        response.checkError()

        val item = response.asList().firstOrNull()
            ?: throw IllegalStateException(
                "No subscription details returned."
            )

        gson.fromJson(
            item,
            SubscriptionDto::class.java
        )
    }

    override suspend fun getApplications(
        userId: String,
        userTypeId: String,
        sessionId: String
    ): Result<List<SubscriptionApplication>> = runCatching {

        val response = apiService.getApplicationList(
            userId = userId,
            userTypeId = userTypeId,
            sessionId = sessionId
        )

        response.checkError()

        response.asList().mapNotNull { item ->
            if (!item.isJsonObject) {
                return@mapNotNull null
            }

            val obj = item.asJsonObject

            val id = obj.get("id")
                ?.takeIf { !it.isJsonNull }
                ?.asString

            val name = obj.get("name")
                ?.takeIf { !it.isJsonNull }
                ?.asString

            if (id.isNullOrBlank() || name.isNullOrBlank()) {
                null
            } else {
                SubscriptionApplication(
                    id = id,
                    name = name
                )
            }
        }
    }

    override suspend fun getAlertDefinitions(
        applicationId: String,
        alertFlag: String,
        userId: String,
        sessionId: String
    ): Result<List<SubscriptionAlertDefinition>> = runCatching {

        Log.d(
            "SubscriptionDefinitions",
            "Request: applicationId=$applicationId, alertFlag=$alertFlag"
        )

        val response = apiService.getAlertDefinitionList(
            applicationId = applicationId,
            alertFlag = alertFlag,
            sessionId = sessionId
        )

        Log.d(
            "SubscriptionDefinitions",
            "Raw API response: $response"
        )

        response.checkError()

        val definitions = response.asList().mapNotNull { item ->
            if (!item.isJsonObject) {
                return@mapNotNull null
            }

            val obj = item.asJsonObject

            val id = obj.readString(
                "id",
                "alertDefId",
                "alertDefinitionId",
                "alertDefinitionID",
                "alertDefID"
            )

            val name = obj.readString(
                "name",
                "alertDefName",
                "alertDefinitionName",
                "definitionName",
                "alertName",
                "description"
            )

            if (id == null || name == null) {
                Log.w(
                    "SubscriptionDefinitions",
                    "Skipping definition with missing ID/name: $obj"
                )
                null
            } else {
                SubscriptionAlertDefinition(
                    id = id,
                    name = name
                )
            }
        }

        Log.d(
            "SubscriptionDefinitions",
            "Parsed ${definitions.size} definitions: " +
                    definitions.joinToString {
                        "${it.id}=${it.name}"
                    }
        )

        definitions
    }

    override suspend fun checkDuplicate(
        userId: String,
        applicationId: String,
        alertDefinitionId: String,
        alertFlag: String,
        userSubId: String,
        sessionId: String
    ): Result<Boolean> = runCatching {

        val response = apiService.checkDuplicateUserSubscribe(
            userId = userId,
            applicationId = applicationId,
            alertDefinitionId = alertDefinitionId,
            alertFlag = alertFlag,
            userSubId = userSubId,
            sessionId = sessionId
        )

        response.checkError()

        Log.d(
            "SubscriptionDuplicate",
            "Duplicate-check response: $response"
        )

        fun parseDuplicateValue(element: JsonElement): Boolean? {

            if (element.isJsonNull) {
                return null
            }

            if (element.isJsonArray) {
                val items = element.asJsonArray

                if (items.size() == 0) {
                    return false
                }

                var foundFalse = false

                for (item in items) {
                    val result = parseDuplicateValue(item)

                    if (result == true) {
                        return true
                    }

                    if (result == false) {
                        foundFalse = true
                    }
                }

                return if (foundFalse) false else null
            }

            if (element.isJsonPrimitive) {
                val primitive = element.asJsonPrimitive
                val value = primitive.asString.trim()

                return when {
                    value.equals("true", ignoreCase = true) -> true
                    value.equals("false", ignoreCase = true) -> false

                    value.toIntOrNull() != null ->
                        value.toInt() > 0

                    else -> null
                }
            }

            if (!element.isJsonObject) {
                return null
            }

            val obj = element.asJsonObject

            // Prefer the exact duplicateCount field used by the old app.
            val duplicateEntry = obj.entrySet().firstOrNull {
                it.key.equals("duplicateCount", ignoreCase = true)
            }

            if (duplicateEntry != null) {
                return parseDuplicateValue(duplicateEntry.value)
            }

            val otherDuplicateKeys = setOf(
                "count",
                "recordCount",
                "totalCount",
                "isDuplicate",
                "duplicate",
                "exists",
                "alreadyExists",
                "isExist"
            )

            for ((key, value) in obj.entrySet()) {
                if (key.lowercase() in otherDuplicateKeys) {
                    val result = parseDuplicateValue(value)
                    if (result != null) {
                        return result
                    }
                }
            }

            val wrapperKeys = setOf(
                "data",
                "result",
                "results",
                "response",
                "details"
            )

            for ((key, value) in obj.entrySet()) {
                if (key.lowercase() in wrapperKeys) {
                    val result = parseDuplicateValue(value)
                    if (result != null) {
                        return result
                    }
                }
            }

            return null
        }

        val isDuplicate = parseDuplicateValue(response)
            ?: throw IllegalStateException(
                "Could not determine duplicate status from the server response. " +
                        "Check the SubscriptionDuplicate Logcat output."
            )

        Log.d(
            "SubscriptionDuplicate",
            "Parsed duplicate status: $isDuplicate, " +
                    "userSubId=$userSubId, applicationId=$applicationId, " +
                    "alertDefinitionId=$alertDefinitionId"
        )

        isDuplicate
    }

    override suspend fun saveSubscription(
        subscription: SubscriptionSaveRequest
    ): Result<Unit> = runCatching {

        Log.d(
            "SubscriptionSave",
            "Saving subscription: userSubId=${subscription.userSubId}, " +
                    "applicationId=${subscription.applicationId}, " +
                    "alertDefId=${subscription.alertDefId}"
        )

        val response = apiService.saveUserSubscription(
            userSubId = subscription.userSubId,
            alertName = subscription.alertName,
            applicationId = subscription.applicationId,
            alertDefId = subscription.alertDefId,
            userId = subscription.userId,
            alertTypes = "1",
            mailId = subscription.mailId,
            messageBody = subscription.messageBody,
            subject = subscription.subject,
            alertFlag = subscription.alertFlag,
            threshold = subscription.threshold,
            sortOrder = subscription.sortOrder,
            active = subscription.active,
            sessionId = subscription.sessionId
        )

        Log.d(
            "SubscriptionSave",
            "Save API response: $response"
        )

        response.checkError()
    }
}
