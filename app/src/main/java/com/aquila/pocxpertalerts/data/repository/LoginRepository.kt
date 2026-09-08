package com.aquila.pocxpertalerts.data.repository

import android.content.Context
import com.aquila.pocxpertalerts.data.model.User
import com.aquila.pocxpertalerts.data.remote.RetrofitClient

class LoginRepository(
    private val context: Context
) {

    suspend fun login(
        username: String,
        password: String,
        deviceId: String
    ): Result<User?> {

        return try {

            // Get Retrofit using the currently saved server URL
            val apiService =
                RetrofitClient.getApiService(context)

            val response =
                apiService.authenticateUserForDevices(
                    username = username,
                    password = password,
                    deviceId = deviceId
                )

            if (response.isNotEmpty()) {

                Result.success(response[0])

            } else {

                Result.success(null)
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}