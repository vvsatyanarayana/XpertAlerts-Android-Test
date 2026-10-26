package com.aquila.pocxpertalerts.data.remote
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val DEFAULT_BASE_URL =
        "https://testapps.aquilasoftware.com/ams/webservice/"

    fun createApiService(configuredUrl: String): ApiService {


        var normalizedUrl = configuredUrl.trim().trimEnd('/')

        require(
            normalizedUrl.startsWith("https://") ||
                    normalizedUrl.startsWith("http://")
        ) {
            "Server URL must start with http:// or https://"
        }

        // Remove /check if an older saved URL contains it.
        if (normalizedUrl.endsWith("/check")) {
            normalizedUrl = normalizedUrl.removeSuffix("/check")
        }

        normalizedUrl += "/"

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("ApiHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        // Preserve the existing SSL configuration.
        val client = UnsafeSslHelper.okHttpClient.newBuilder()
            .addInterceptor(loggingInterceptor)
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

    // Compatibility for existing callers.
    val apiService: ApiService by lazy {
        createApiService(DEFAULT_BASE_URL)
    }
}
