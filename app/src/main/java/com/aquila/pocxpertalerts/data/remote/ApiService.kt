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

}