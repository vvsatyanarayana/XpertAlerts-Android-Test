
package com.aquila.pocxpertalerts.data.repository

import com.aquila.pocxpertalerts.data.model.SubscriptionDto

interface SubscriptionRepository {

    suspend fun getSubscriptions(
        userId: String,
        sessionId: String,
        applicationId: String = "0",
        searchName: String = "",
        searchActive: Int = 1
    ): Result<List<SubscriptionDto>>

    suspend fun getSubscriptionDetails(
        userSubId: Long,
        sessionId: String
    ): Result<SubscriptionDto>

    suspend fun getApplications(
        userId: String,
        userTypeId: String,
        sessionId: String
    ): Result<List<SubscriptionApplication>>

    suspend fun getAlertDefinitions(
        applicationId: String,
        alertFlag: String,
        userId: String,
        sessionId: String
    ): Result<List<SubscriptionAlertDefinition>>

    suspend fun checkDuplicate(
        userId: String,
        applicationId: String,
        alertDefinitionId: String,
        alertFlag: String,
        userSubId: String,
        sessionId: String
    ): Result<Boolean>

    suspend fun saveSubscription(
        subscription: SubscriptionSaveRequest
    ): Result<Unit>
}

data class SubscriptionApplication(
    val id: String,
    val name: String
)

data class SubscriptionAlertDefinition(
    val id: String,
    val name: String
)

data class SubscriptionSaveRequest(
    val userSubId: String = "0",
    val alertName: String,
    val applicationId: String,
    val alertDefId: String,
    val userId: String,
    val mailId: String,
    val messageBody: String = "",
    val subject: String = "",
    val alertFlag: String = "1",
    val threshold: String = "0",
    val sortOrder: String = "0",
    val active: String = "1",
    val sessionId: String
)
