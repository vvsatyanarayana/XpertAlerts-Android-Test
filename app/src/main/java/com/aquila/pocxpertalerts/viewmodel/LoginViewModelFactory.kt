
package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.aquila.pocxpertalerts.data.repository.LoginRepository
import com.aquila.pocxpertalerts.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class LoginViewModelFactory(
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {

            // Read the configured URL.
            // This factory is called from ViewModel creation;
            // first() retrieves the saved preference.
            val configuredUrl = runBlocking {
                settingsDataStore.webServiceUrl.first()
            }

            // Create the API service with the saved URL.
            val apiService =
                RetrofitClient.createApiService(configuredUrl)

            // Repository
            val repository = LoginRepository(
                apiService = apiService
            )

            // Use case
            val loginUseCase = LoginUseCase(
                authRepository = repository
            )

            // ViewModel
            return LoginViewModel(
                loginUseCase = loginUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
