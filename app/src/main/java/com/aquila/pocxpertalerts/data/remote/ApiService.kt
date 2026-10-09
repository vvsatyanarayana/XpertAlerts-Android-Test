package com.aquila.pocxpertalerts.data.remote

import com.aquila.pocxpertalerts.data.model.UserDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("authenticateUserForDevices")
    suspend fun authenticateUserForDevices(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("deviceId") deviceId: String
    ): List<UserDto>
}


