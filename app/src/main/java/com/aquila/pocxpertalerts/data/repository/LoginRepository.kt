
package com.aquila.pocxpertalerts.data.repository

import android.util.Log
import com.aquila.pocxpertalerts.BuildConfig
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.model.toDomain
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.aquila.pocxpertalerts.data.remote.UnsafeSslHelper
import com.aquila.pocxpertalerts.domain.model.User
import com.aquila.pocxpertalerts.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.HostnameVerifier

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
            // 1. Read the latest saved server URL.
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

            Log.d(TAG, "Testing configured server URL")

            // 3. Test the configured server.
            val responseCode = testConfiguredUrl(configuredUrl)

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Server test failed: HTTP $responseCode")

                return@withContext Result.failure(
                    IllegalStateException(
                        "Server test failed: HTTP $responseCode. Login cancelled."
                    )
                )
            }

            Log.d(TAG, "Server test succeeded")

            // 4. Create the API service.
            val apiService =
                RetrofitClient.createApiService(configuredUrl)

            // 5. Authenticate.
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
            Log.e(TAG, "Server test or authentication failed", e)

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

            // Bypass SSL certificate and hostname checks in Debug only.
            if (BuildConfig.DEBUG && connection is HttpsURLConnection) {
                connection.sslSocketFactory =
                    UnsafeSslHelper.sslSocketFactory

                connection.hostnameVerifier =
                    HostnameVerifier { _, _ -> true }
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
