package com.aquila.pocxpertalerts.data.remote

import com.aquila.pocxpertalerts.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    fun createApiService(configuredUrl: String): ApiService {
        var normalizedUrl = configuredUrl.trim().trimEnd('/')

        require(
            normalizedUrl.startsWith("https://", ignoreCase = true) ||
                    normalizedUrl.startsWith("http://", ignoreCase = true)
        ) {
            "Server URL must start with http:// or https://"
        }

        if (normalizedUrl.endsWith("/check", ignoreCase = true)) {
            normalizedUrl = normalizedUrl.removeSuffix("/check")
        }

        normalizedUrl += "/"

        val parsedBaseUrl = normalizedUrl.toHttpUrlOrNull()
            ?: throw IllegalArgumentException("Invalid server URL.")
        require(parsedBaseUrl.query == null && parsedBaseUrl.fragment == null) {
            "Server URL must not contain a query string or fragment."
        }

        // Keep the app's existing local-development SSL behaviour in Debug.
        // Release builds always use the platform's normal certificate validation.
        val clientBuilder = if (BuildConfig.DEBUG) {
            UnsafeSslHelper.okHttpClient.newBuilder()
        } else {
            OkHttpClient.Builder()
        }

        // Never log request/response bodies. Redact credentials in query strings
        // because the existing backend contract currently uses GET parameters.


        val client = clientBuilder
            .addInterceptor(SessionExpirationInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
