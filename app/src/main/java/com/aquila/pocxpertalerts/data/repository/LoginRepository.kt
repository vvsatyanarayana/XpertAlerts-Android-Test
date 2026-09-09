package com.aquila.pocxpertalerts.data.repository

import android.content.Context
import android.provider.Settings
import com.aquila.pocxpertalerts.data.model.User
import com.aquila.pocxpertalerts.data.remote.RetrofitClient

class LoginRepository(
    private val context: Context
) {

    // ========================================================
    // LOGIN
    // ========================================================

    suspend fun login(
        username: String,
        password: String
    ): Result<User?> {

        return try {

            // ------------------------------------------------
            // ANDROID DEVICE ID
            // ------------------------------------------------

            val deviceId =
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )

            // ------------------------------------------------
            // GET CURRENT API SERVICE
            // ------------------------------------------------

            val apiService =
                RetrofitClient.getApiService(
                    context
                )

            // ------------------------------------------------
            // CALL REAL XPERT ALERTS API
            // ------------------------------------------------

            val response =
                apiService.authenticateUserForDevices(
                    username = username,
                    password = password,
                    deviceId = deviceId
                )

            // ------------------------------------------------
            // RESPONSE
            // ------------------------------------------------

            if (response.isNotEmpty()) {

                Result.success(
                    response[0]
                )

            } else {

                Result.success(null)
            }

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure(e)
        }
    }
}