
package com.aquila.pocxpertalerts.data.repository

import com.aquila.pocxpertalerts.data.model.SubscriptionDto
import com.aquila.pocxpertalerts.data.remote.ApiService
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

class SubscriptionRepositoryImpl(
    private val apiService: ApiService,
    private val gson: Gson = Gson()
) : SubscriptionRepository {

    private fun JsonElement.asList(): List<JsonElement> {
        return when {
            isJsonArray -> asJsonArray.toList()
            isJsonObject -> emptyList()
            else -> emptyList()
        }
    }

    private fun JsonElement.errorMessage(): String? {
        if (!isJsonObject) return null
        val obj = asJsonObject
        val code = obj.get("errorCode")?.asString
        if (code == "412") {
            return "Your session has expired. Please log in again."
        }
        return obj.get("message")?.takeIf { !it.isJsonNull }?.asString
            ?: obj.get("errorMessage")?.takeIf { !it.isJsonNull }?.asString
    }

    private fun JsonElement.checkError() {
        if (isJsonObject) {
            val obj = asJsonObject
            val code = obj.get("errorCode")?.let {
                if (it.isJsonNull) 0 else it.asInt
            } ?: 0

            if (code != 0) {
                throw IllegalStateException(
                    errorMessage() ?: "Subscription request failed (code $code)."
                )
            }
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

        response.checkError()

        response.asList().mapNotNull { item ->
            runCatching {
                gson.fromJson(item, SubscriptionDto::class.java)
            }.getOrNull()
        }.filter { it.errorCode == 0 }
    }

    override suspend fun getSubscriptionDetails(
        userSubId: Long,
        sessionId: String
    ): Result<SubscriptionDto> = runCatching {
        val response = apiService.getUserSubDetailsById(
            userSubId = userSubId,
            sessionId = sessionId
        )

        response.checkError()

        val item = response.asList().firstOrNull()
            ?: throw IllegalStateException("No subscription details returned.")

        gson.fromJson(item, SubscriptionDto::class.java)
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
            if (!item.isJsonObject) return@mapNotNull null
            val obj = item.asJsonObject

            val id = obj.get("id")?.takeIf { !it.isJsonNull }?.asString
            val name = obj.get("name")?.takeIf { !it.isJsonNull }?.asString

            if (id.isNullOrBlank() || name.isNullOrBlank()) null
            else SubscriptionApplication(id, name)
        }
    }

    override suspend fun getAlertDefinitions(
        applicationId: String,
        alertFlag: String,
        userId: String,
        sessionId: String
    ): Result<List<SubscriptionAlertDefinition>> = runCatching {
        val response = apiService.getApplicatoinAlertDefinitions(
            applicationId = applicationId,
            alertFlag = alertFlag,
            userId = userId,
            sessionId = sessionId
        )

        response.checkError()

        response.asList().mapNotNull { item ->
            if (!item.isJsonObject) return@mapNotNull null
            val obj = item.asJsonObject
            val id = obj.get("id")?.takeIf { !it.isJsonNull }?.asString
                ?: obj.get("alertDefId")?.takeIf { !it.isJsonNull }?.asString
            val name = obj.get("name")?.takeIf { !it.isJsonNull }?.asString
                ?: obj.get("alertDefName")?.takeIf { !it.isJsonNull }?.asString

            if (id.isNullOrBlank() || name.isNullOrBlank()) null
            else SubscriptionAlertDefinition(id, name)
        }
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

        // The legacy endpoint's exact duplicate response schema needs
        // confirmation from a real response before interpreting the result.
        response.asList().isNotEmpty()
    }

    override suspend fun saveSubscription(
        subscription: SubscriptionSaveRequest
    ): Result<Unit> = runCatching {
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

        response.checkError()
    }
}
