package com.aquila.pocxpertalerts.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val DEFAULT_BASE_URL =
        "https://www.xpertalerts.com/ams/webservice/"

    fun createApiService(configuredUrl: String): ApiService {
        var normalizedUrl = configuredUrl.trim().trimEnd('/')

        require(
            normalizedUrl.startsWith("https://") ||
                    normalizedUrl.startsWith("http://")
        ) {
            "Server URL must start with http:// or https://"
        }

        // The legacy app uses /check for testing,
        // but removes it before constructing the login API URL.
        if (normalizedUrl.endsWith("/check")) {
            normalizedUrl = normalizedUrl.removeSuffix("/check")
        }

        normalizedUrl += "/"

        return Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .client(UnsafeSslHelper.okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Compatibility for existing callers.
    val apiService: ApiService by lazy {
        createApiService(DEFAULT_BASE_URL)
    }
}