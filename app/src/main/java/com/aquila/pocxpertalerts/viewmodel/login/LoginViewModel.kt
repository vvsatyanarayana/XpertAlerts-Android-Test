package com.aquila.pocxpertalerts.viewmodel.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginUiState(
    val isLoading: Boolean = false,
    val userIdError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isLoginSuccess: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    fun login(
        userId: String,
        password: String
    ) {

        // Clear previous errors
        _uiState.value = _uiState.value.copy(
            userIdError = null,
            passwordError = null,
            loginError = null,
            isLoginSuccess = false
        )

        // Validate User ID
        if (userId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                userIdError = "Please enter user ID"
            )
            return
        }

        // Validate Password
        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                passwordError = "Please enter password"
            )
            return
        }

        // Validation successful
        _uiState.value = _uiState.value.copy(
            isLoading = true
        )

        // Real API will be connected here later
    }

    fun clearLoginError() {
        _uiState.value = _uiState.value.copy(
            loginError = null
        )
    }
}