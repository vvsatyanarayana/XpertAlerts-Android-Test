
package com.aquila.pocxpertalerts.data.repository

import com.aquila.pocxpertalerts.BuildConfig
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.model.toDomain
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.aquila.pocxpertalerts.data.remote.UnsafeSslHelper
import com.aquila.pocxpertalerts.domain.model.User
import com.aquila.pocxpertalerts.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
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



            // 3. Test the configured server.
            val serverValid = testConfiguredUrl(configuredUrl)

            if (!serverValid) {
                return@withContext Result.failure(
                    IllegalStateException(
                        "Server configuration failed. Please check the URL."
                    )
                )
            }





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

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {


            Result.failure(
                Exception(
                    e.message ?: "Unable to connect to server.",
                    e
                )
            )
        }
    }


    private fun testConfiguredUrl(urlText: String): Boolean {

        var connection: HttpURLConnection? = null

        try {
            // Remove trailing slash.
            val baseUrl = urlText.trim().trimEnd('/')

            // Build the correct server test endpoint.
            val checkUrl = if (baseUrl.endsWith("/check")) {
                baseUrl
            } else {
                "$baseUrl/check"
            }



            connection = URL(checkUrl)
                .openConnection() as HttpURLConnection

            // Match Retrofit's Debug-only local certificate bypass for the /check probe.
            if (BuildConfig.DEBUG && connection is HttpsURLConnection) {
                connection.sslSocketFactory = UnsafeSslHelper.sslSocketFactory
                connection.hostnameVerifier = HostnameVerifier { _, _ -> true }
            }

            connection.requestMethod = "GET"
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            connection.useCaches = false

            val responseCode = connection.responseCode

            if (responseCode != HttpURLConnection.HTTP_OK) {

                return false
            }

            // Read actual server JSON response.
            val responseBody = connection.inputStream
                .bufferedReader()
                .use { it.readText() }


            // Match the original iOS validation.
            val json = org.json.JSONObject(responseBody)
            val checkValue = json.optString("check")

            return checkValue == "1"

        } catch (e: Exception) {

            return false

        } finally {
            connection?.disconnect()
        }
    }

}
