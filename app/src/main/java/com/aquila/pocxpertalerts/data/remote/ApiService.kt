package com.aquila.pocxpertalerts.data.remote

import com.aquila.pocxpertalerts.data.model.UserDto
import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {

    @GET("authenticateUserForDevices")
    suspend fun authenticateUserForDevices(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("deviceId") deviceId: String
    ): List<UserDto>

    @GET("getAlertsForDevices")
    suspend fun getAlertsForDevices(
        @QueryMap parameters: Map<String, String>
    ): JsonElement

    @GET("getArchivedAlertsForDevices")
    suspend fun getArchivedAlertsForDevices(
        @Query("userId") userId: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("searchByKeyword") searchByKeyword: String,
        @Query("viewByFlag") viewByFlag: String,
        @Query("status") status: String,
        @Query("emailStatus") emailStatus: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("getApplicationList")
    suspend fun getApplicationList(
        @Query("userId") userId: String,
        @Query("userTypeId") userTypeId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("getAlertDefinitionList")
    suspend fun getAlertDefinitionList(
        @Query("applicationId") applicationId: String,
        @Query("alertFlag") alertFlag: String = "1",
        @Query("sessionId") sessionId: String
    ): JsonElement




    @GET("getUserSubscriptionList")
    suspend fun getUserSubscriptionList(
        @Query("userId") userId: String,
        @Query("lowerBound") lowerBound: Int = 0,
        @Query("upperBound") upperBound: Int = 100,
        @Query("searchName") searchName: String,
        @Query("applicationId") applicationId: String,
        @Query("searchActive") searchActive: Int,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("getUserSubDetailsById")
    suspend fun getUserSubDetailsById(
        @Query("userSubId") userSubId: Long,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("checkDuplicateUserSubscribe")
    suspend fun checkDuplicateUserSubscribe(
        @Query("userId") userId: String,
        @Query("applicationId") applicationId: String,
        @Query("alertDefinitionId") alertDefinitionId: String,
        @Query("alertFlag") alertFlag: String,
        @Query("userSubId") userSubId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("getApplicatoinAlertDefinitions")
    suspend fun getApplicatoinAlertDefinitions(
        @Query("applicationId") applicationId: String,
        @Query("alertFlag") alertFlag: String,
        @Query("userId") userId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("saveUserSubscription")
    suspend fun saveUserSubscription(
        @Query("userSubId") userSubId: String,
        @Query("alertName") alertName: String,
        @Query("applicationId") applicationId: String,
        @Query("alertDefId") alertDefId: String,
        @Query("userId") userId: String,
        @Query("alertTypes") alertTypes: String = "1",
        @Query("mailId") mailId: String,
        @Query("messageBody") messageBody: String,
        @Query("subject") subject: String,
        @Query("alertFlag") alertFlag: String,
        @Query("threshold") threshold: String,
        @Query("sortOrder") sortOrder: String,
        @Query("active") active: String,
        @Query("sessionId") sessionId: String
    ): JsonElement


    // ============================================================
    // FORWARD ALERTS API
    // ============================================================

    @GET("getForwardAlertsList")
    suspend fun getForwardAlertsList(
        @Query("baseUserId") baseUserId: String,
        @Query("lowerBound") lowerBound: Int = 0,
        @Query("upperBound") upperBound: Int = 100,
        @Query("searchName") searchName: String,
        @Query("applicationIds") applicationIds: String,
        @Query("searchEmail") searchEmail: String,
        @Query("searchActive") searchActive: Int,
        @Query("searchAppId") searchAppId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("getForwardMappedApplicationList")
    suspend fun getForwardMappedApplicationList(
        @Query("userId") userId: String,
        @Query("userTypeId") userTypeId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("getBaseUserList")
    suspend fun getBaseUserList(
        @Query("userId") userId: String,
        @Query("userTypeId") userTypeId: String,
        @Query("applicationIds") applicationId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("getUserEmail")
    suspend fun getUserEmail(
        @Query("userId") userId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("getBaseUserDetailsById")
    suspend fun getBaseUserDetailsById(
        @Query("mapUserId") mapUserId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("duplicateForwardAlert")
    suspend fun duplicateForwardAlert(
        @Query("forwardAlertId") forwardAlertId: String,
        @Query("baseUserId") baseUserId: String,
        @Query("mappedUserId") mappedUserId: String,
        @Query("applicationId") applicationId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("saveMappedUsers")
    suspend fun saveMappedUsers(
        @Query("forwardAlertId") forwardAlertId: String,
        @Query("baseUserId") baseUserId: String,
        @Query("mappedUserId") mappedUserId: String,
        @Query("email") email: String,
        @Query("sortOrder") sortOrder: String,
        @Query("active") active: String,
        @Query("applicationId") applicationId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("deleteForwardAlertUser")
    suspend fun deleteForwardAlertUser(
        @Query("forwardAlertId") forwardAlertId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("updateForwardAlertsUserStatus")
    suspend fun updateForwardAlertsUserStatus(
        @Query("userId") userId: String,
        @Query("status") status: String,
        @Query("mappedUserId") mappedUserId: String,
        @Query("applicationId") applicationId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("updateForwardAlertsStatus")
    suspend fun updateForwardAlertsStatus(
        @Query("userId") userId: String,
        @Query("status") status: String,
        @Query("sessionId") sessionId: String
    ): JsonElement


    @GET("getAlertButtonDetails")
    suspend fun getAlertButtonDetails(
        @Query("applicationId") applicationId: String,
        @Query("alertDefId") alertDefId: String,
        @Query("userId") userId: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

    @GET("processDynamicActions")
    suspend fun processDynamicActions(
        @Query("userId") userId: String,
        @Query("applicationId") applicationId: String,
        @Query("captions") captions: String,
        @Query("actions") actions: String,
        @Query("amsActions") amsActions: String,
        @Query("ids") ids: String,
        @Query("alertIds") alertIds: String,
        @Query("aggregateAlertIds") aggregateAlertIds: String,
        @Query("sessionId") sessionId: String
    ): JsonElement

// ============================================================
// CHANGE PASSWORD API
// ============================================================

    @GET("saveChangePassword")
    suspend fun saveChangePassword(
        @Query("username") username: String,
        @Query("oldPwd") oldPassword: String,
        @Query("newPwd") newPassword: String,
        @Query("userId") userId: String
    ): JsonElement

}