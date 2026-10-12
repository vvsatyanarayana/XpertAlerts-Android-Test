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
}