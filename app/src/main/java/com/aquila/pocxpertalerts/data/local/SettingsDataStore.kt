
package com.aquila.pocxpertalerts.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(
    name = "settings"
)

class SettingsDataStore(
    private val context: Context
) {
    companion object {
        private val WEB_SERVICE_URL =
            stringPreferencesKey("web_service_url")

        const val DEFAULT_URL =
            "https://www.xpertalerts.com/ams/webservice/check"
    }

    val webServiceUrl: Flow<String> =
        context.settingsDataStore.data.map { preferences ->
            preferences[WEB_SERVICE_URL] ?: DEFAULT_URL
        }

    suspend fun saveWebServiceUrl(url: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[WEB_SERVICE_URL] = url.trim()
        }
    }
}
