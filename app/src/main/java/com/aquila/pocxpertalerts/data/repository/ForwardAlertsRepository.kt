
package com.aquila.pocxpertalerts.data.repository

import com.aquila.pocxpertalerts.data.remote.ApiService
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.google.gson.JsonElement

class ForwardAlertsRepository(
    private val api: ApiService = RetrofitClient.apiService
) {

    suspend fun getApplications(
        userId: String,
        userTypeId: String,
        sessionId: String
    ): JsonElement {
        return api.getForwardMappedApplicationList(
            userId = userId,
            userTypeId = userTypeId,
            sessionId = sessionId
        )
    }

    suspend fun getUsers(
        userId: String,
        userTypeId: String,
        applicationId: String,
        sessionId: String
    ): JsonElement {
        return api.getBaseUserList(
            userId = userId,
            userTypeId = userTypeId,
            applicationId = applicationId,
            sessionId = sessionId
        )
    }

    suspend fun getUserEmail(
        userId: String,
        sessionId: String
    ): JsonElement {
        return api.getUserEmail(
            userId = userId,
            sessionId = sessionId
        )
    }

    suspend fun checkDuplicate(
        forwardAlertId: String,
        baseUserId: String,
        mappedUserId: String,
        applicationId: String,
        sessionId: String
    ): JsonElement {
        return api.duplicateForwardAlert(
            forwardAlertId = forwardAlertId,
            baseUserId = baseUserId,
            mappedUserId = mappedUserId,
            applicationId = applicationId,
            sessionId = sessionId
        )
    }

    suspend fun saveMapping(
        forwardAlertId: String,
        baseUserId: String,
        mappedUserId: String,
        email: String,
        sortOrder: String,
        active: String,
        applicationId: String,
        sessionId: String
    ): JsonElement {
        return api.saveMappedUsers(
            forwardAlertId = forwardAlertId,
            baseUserId = baseUserId,
            mappedUserId = mappedUserId,
            email = email,
            sortOrder = sortOrder,
            active = active,
            applicationId = applicationId,
            sessionId = sessionId
        )
    }

    suspend fun getForwardAlerts(
        baseUserId: String,
        applicationIds: String,
        sessionId: String,
        searchName: String = "",
        searchEmail: String = "",
        searchActive: Int = -1,
        searchAppId: String = "",
        lowerBound: Int = 0,
        upperBound: Int = 100
    ): JsonElement {
        return api.getForwardAlertsList(
            baseUserId = baseUserId,
            lowerBound = lowerBound,
            upperBound = upperBound,
            searchName = searchName,
            applicationIds = applicationIds,
            searchEmail = searchEmail,
            searchActive = searchActive,
            searchAppId = searchAppId,
            sessionId = sessionId
        )
    }
}
