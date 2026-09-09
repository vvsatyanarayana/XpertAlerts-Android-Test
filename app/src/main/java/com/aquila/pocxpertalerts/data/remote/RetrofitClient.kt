package com.aquila.pocxpertalerts.data.remote

import android.content.Context
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val DEFAULT_TEST_URL =
        "https://www.xpertalerts.com/ams/webservice/check"

    // ========================================================
    // GET API SERVICE
    // ========================================================

    suspend fun getApiService(
        context: Context
    ): ApiService {

        val savedUrl =
            SettingsDataStore(context)
                .webServiceUrl
                .first()

        val configuredUrl =
            if (!savedUrl.isNullOrBlank()) {
                savedUrl
            } else {
                DEFAULT_TEST_URL
            }

        // ----------------------------------------------------
        // Convert:
        //
        // https://www.xpertalerts.com/ams/webservice/check
        //
        // TO:
        //
        // https://www.xpertalerts.com/ams/webservice/
        // ----------------------------------------------------

        val baseUrl =
            configuredUrl
                .trimEnd('/')
                .removeSuffix("/check")
                .trimEnd('/')

        return Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}