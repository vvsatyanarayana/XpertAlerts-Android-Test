
package com.aquila.pocxpertalerts.data.repository

import android.util.Log
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.model.toDomain
import com.aquila.pocxpertalerts.data.remote.ApiService
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.aquila.pocxpertalerts.domain.model.User
import com.aquila.pocxpertalerts.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import com.aquila.pocxpertalerts.data.remote.UnsafeSslHelper
class LoginRepository(
    private val settingsDataStore: SettingsDataStore
) : AuthRepository {

    companion object {
        private const val TAG = "XpertRepository"
        private const val TIMEOUT_MS = 3_000
    }

    override suspend fun login(
        username: String,
        password: String,
        deviceId: String
    ): Result<User> = withContext(Dispatchers.IO) {

        try {
            // 1. Always read the latest saved server URL.
            val configuredUrl =
                settingsDataStore.webServiceUrl.first().trim()

            if (configuredUrl.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException(
                        "Server URL is not configured. Open Settings and save it first."
                    )
                )
            }

            // 2. Validate URL format.
            val parsedUrl = try {
                URL(configuredUrl)
            } catch (e: Exception) {
                return@withContext Result.failure(
                    IllegalArgumentException("Invalid server URL.")
                )
            }

            if (
                (parsedUrl.protocol != "http" &&
                        parsedUrl.protocol != "https") ||
                parsedUrl.host.isNullOrBlank()
            ) {
                return@withContext Result.failure(
                    IllegalArgumentException(
                        "Please configure a valid HTTP or HTTPS server URL."
                    )
                )
            }

            Log.d(TAG, "STEP 1: Testing configured server URL")
            Log.d(TAG, "Configured URL: $configuredUrl")

            // 3. Test the exact saved URL before authentication.
            val responseCode = testConfiguredUrl(configuredUrl)

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(
                    TAG,
                    "Server test failed: HTTP $responseCode. Authentication cancelled."
                )

                return@withContext Result.failure(
                    IllegalStateException(
                        "Server test failed: HTTP $responseCode. Login cancelled."
                    )
                )
            }

            Log.d(TAG, "STEP 2: Server test succeeded")

            // 4. Create the API service using the same saved URL.
            val apiService =
                RetrofitClient.createApiService(configuredUrl)

            // 5. Authenticate ONLY after the server test succeeds.
            Log.d(TAG, "STEP 3: Calling authentication API")

            val users = apiService.authenticateUserForDevices(
                username = username,
                password = password,
                deviceId = deviceId
            )

            if (users.isEmpty()) {
                Result.failure(
                    IllegalStateException(
                        "No user response received from server."
                    )
                )
            } else {
                Result.success(users.first().toDomain())
            }

        } catch (e: Exception) {
            Log.e(
                TAG,
                "Server test or authentication failed",
                e
            )

            Result.failure(
                Exception(
                    e.message ?: "Unable to connect to server.",
                    e
                )
            )
        }
    }


    private fun testConfiguredUrl(urlText: String): Int {
        var connection: HttpURLConnection? = null

        try {
            connection = URL(urlText)
                .openConnection() as HttpURLConnection

            // Match the existing Settings screen SSL behavior.
            if (connection is HttpsURLConnection) {
                connection.sslSocketFactory =
                    UnsafeSslHelper.sslSocketFactory

                connection.hostnameVerifier =
                    javax.net.ssl.HostnameVerifier { _, _ -> true }
            }

            connection.requestMethod = "GET"
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            connection.useCaches = false

            return connection.responseCode

        } finally {
            connection?.disconnect()
        }
    }

}
