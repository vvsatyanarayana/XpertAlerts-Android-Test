package com.aquila.pocxpertalerts.data.remote

import android.content.Context
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val DEFAULT_BASE_URL =
        "https://www.xpertalerts.com/ams/webservice/"

    /**
     * Returns the currently configured Web Service URL.
     *
     * If the user has not configured a URL yet,
     * the default Xpert Alerts URL is used.
     */
    private suspend fun getBaseUrl(
        context: Context
    ): String {

        val savedUrl =
            SettingsDataStore(context)
                .webServiceUrl
                .first()

        return if (!savedUrl.isNullOrBlank()) {
            savedUrl
        } else {
            DEFAULT_BASE_URL
        }
    }


    /**
     * Creates ApiService using the saved server URL.
     */
    suspend fun getApiService(
        context: Context
    ): ApiService {

        val baseUrl =
            getBaseUrl(context)

        return Retrofit.Builder()
            .baseUrl(
                if (baseUrl.endsWith("/")) {
                    baseUrl
                } else {
                    "$baseUrl/"
                }
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}