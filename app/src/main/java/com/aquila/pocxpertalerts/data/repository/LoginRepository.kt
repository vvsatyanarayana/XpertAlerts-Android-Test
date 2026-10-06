package com.aquila.pocxpertalerts.data.repository

import android.net.Uri
import android.util.Log
import com.aquila.pocxpertalerts.data.model.toDomain
import com.aquila.pocxpertalerts.data.remote.ApiService
import com.aquila.pocxpertalerts.domain.model.User
import com.aquila.pocxpertalerts.domain.repository.AuthRepository

class LoginRepository(
    private val apiService: ApiService
) : AuthRepository {

    companion object {
        private const val TAG = "XpertRepository"
    }

    override suspend fun login(
        username: String,
        password: String,
        deviceId: String
    ): Result<User> {

        return try {

            Log.d(TAG, "Starting authenticateUserForDevices API")
            Log.d(TAG, "Username: $username")
            Log.d(TAG, "Device ID available: ${deviceId.isNotBlank()}")

            /*
             * The legacy application used:
             *
             * android.net.Uri.encode(password)
             *
             * before sending the password.
             *
             * We preserve that behavior here.
             */
            val encodedPassword = Uri.encode(password)

            Log.d(TAG, "Password encoded successfully")

            val users = apiService.authenticateUserForDevices(
                username = username,
                password = encodedPassword,
                deviceId = deviceId
            )

            Log.d(TAG, "API response received")
            Log.d(TAG, "Number of users returned: ${users.size}")

            if (users.isEmpty()) {

                Log.e(
                    TAG,
                    "API returned an empty user list"
                )

                Result.failure(
                    Exception(
                        "No response received from server"
                    )
                )

            } else {

                val user = users.first().toDomain()

                Log.d(
                    TAG,
                    "Response user ID: ${user.id}"
                )

                Log.d(
                    TAG,
                    "Response user type: ${user.userTypeId}"
                )

                Result.success(user)
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Exception while calling login API",
                e
            )

            Result.failure(e)
        }
    }
}