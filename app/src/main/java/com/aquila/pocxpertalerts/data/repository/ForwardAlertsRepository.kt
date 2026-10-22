package com.aquila.pocxpertalerts.data.repository

import com.aquila.pocxpertalerts.data.remote.ApiService
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.google.gson.JsonElement

class ForwardAlertsRepository(
    private val api: ApiService = RetrofitClient.apiService
) {
    suspend fun getForwardAlerts(
        baseUserId: String,
        applicationIds: String,
        sessionId: String,
        searchName: String = "",
        searchEmail: String = "",
        searchActive: Int = 2,
        searchAppId: String = "0"
    ): JsonElement {
        return api.getForwardAlertsList(
            baseUserId = baseUserId,
            lowerBound = 0,
            upperBound = 100,
            searchName = searchName,
            applicationIds = applicationIds,
            searchEmail = searchEmail,
            searchActive = searchActive,
            searchAppId = searchAppId,
            sessionId = sessionId
        )
    }

    suspend fun updateUserStatus(
        userId: String,
        status: String,
        mappedUserId: String,
        applicationId: String,
        sessionId: String
    ): JsonElement {
        return api.updateForwardAlertsUserStatus(
            userId = userId,
            status = status,
            mappedUserId = mappedUserId,
            applicationId = applicationId,
            sessionId = sessionId
        )
    }

    suspend fun updateAllStatus(
        userId: String,
        status: String,
        sessionId: String
    ): JsonElement {
        return api.updateForwardAlertsStatus(
            userId = userId,
            status = status,
            sessionId = sessionId
        )
    }

    suspend fun deleteForwardAlert(
        forwardAlertId: String,
        sessionId: String
    ): JsonElement {
        return api.deleteForwardAlertUser(
            forwardAlertId = forwardAlertId,
            sessionId = sessionId
        )
    }



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
    suspend fun getDuplicateForwardAlert(
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

    suspend fun saveMappedUser(
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

    suspend fun getForwardAlertDetails(
        mappedUserId: String,
        sessionId: String
    ): JsonElement {
        return api.getBaseUserDetailsById(
            mapUserId = mappedUserId,
            sessionId = sessionId
        )
    }
}