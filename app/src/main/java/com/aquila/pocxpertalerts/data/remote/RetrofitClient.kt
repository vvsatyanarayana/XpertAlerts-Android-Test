
package com.aquila.pocxpertalerts.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val DEFAULT_BASE_URL =
        "https://www.xpertalerts.com/ams/webservice/check/"

    fun createApiService(configuredUrl: String): ApiService {
        val normalizedUrl = configuredUrl.trim().let { url ->
            if (url.endsWith("/")) url else "$url/"
        }

        require(
            normalizedUrl.startsWith("https://") ||
                    normalizedUrl.startsWith("http://")
        ) {
            "Server URL must start with http:// or https://"
        }

        return Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Temporary compatibility for existing callers.
    val apiService: ApiService by lazy {
        createApiService(DEFAULT_BASE_URL)
    }
}
