
package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.repository.LoginRepository
import com.aquila.pocxpertalerts.domain.usecase.LoginUseCase

class LoginViewModelFactory(
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {

            val repository = LoginRepository(
                settingsDataStore = settingsDataStore
            )

            val loginUseCase = LoginUseCase(
                authRepository = repository
            )

            return LoginViewModel(
                loginUseCase = loginUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
