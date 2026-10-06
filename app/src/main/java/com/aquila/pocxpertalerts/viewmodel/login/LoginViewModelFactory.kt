package com.aquila.pocxpertalerts.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import com.aquila.pocxpertalerts.data.repository.LoginRepository
import com.aquila.pocxpertalerts.domain.usecase.LoginUseCase

class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                LoginViewModel::class.java
            )
        ) {

            // -------------------------------------------------
            // API
            // -------------------------------------------------

            val apiService =
                RetrofitClient.apiService


            // -------------------------------------------------
            // REPOSITORY
            // -------------------------------------------------

            val repository =
                LoginRepository(
                    apiService = apiService
                )


            // -------------------------------------------------
            // USE CASE
            // -------------------------------------------------

            val loginUseCase =
                LoginUseCase(
                    authRepository = repository
                )


            // -------------------------------------------------
            // VIEW MODEL
            // -------------------------------------------------

            return LoginViewModel(
                loginUseCase = loginUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}