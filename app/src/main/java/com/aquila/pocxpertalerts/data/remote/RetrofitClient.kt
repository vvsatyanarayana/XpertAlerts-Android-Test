package com.aquila.pocxpertalerts.data.remote

import android.content.Context
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val DEFAULT_BASE_URL =
        "https://xpertalerts.com/ams/webservice/"

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

    suspend fun getApiService(
        context: Context
    ): ApiService {

        val baseUrl =
            getBaseUrl(context)

        val finalUrl =
            if (baseUrl.endsWith("/")) {
                baseUrl
            } else {
                "$baseUrl/"
            }

        return Retrofit.Builder()
            .baseUrl(finalUrl)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}