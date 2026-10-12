
package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.aquila.pocxpertalerts.data.repository.AlertRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AlertsViewModelFactory(
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {

            val configuredUrl = runBlocking {
                settingsDataStore.webServiceUrl.first()
            }

            val apiService =
                RetrofitClient.createApiService(configuredUrl)

            val repository = AlertRepository(apiService)

            return AlertsViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
