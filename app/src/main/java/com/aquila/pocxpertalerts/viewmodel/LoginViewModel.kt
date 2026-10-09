package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.domain.model.User
import com.aquila.pocxpertalerts.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val userId: String = "",
    val password: String = "",

    val userIdError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,

    val isLoading: Boolean = false,

    val loginSuccess: Boolean = false,

    val user: User? = null
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()


    // =========================================================
    // USER ID
    // =========================================================

    fun updateUserId(value: String) {

        _uiState.value = _uiState.value.copy(
            userId = value,
            userIdError = null,
            errorMessage = null
        )
    }


    // =========================================================
    // PASSWORD
    // =========================================================

    fun updatePassword(value: String) {

        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null,
            errorMessage = null
        )
    }


    // =========================================================
    // LOGIN
    // =========================================================

    fun login(deviceId: String) {

        val currentState = _uiState.value

        // -----------------------------------------------------
        // VALIDATE USER ID
        // -----------------------------------------------------

        if (currentState.userId.isBlank()) {

            _uiState.value = currentState.copy(
                userIdError = "Please enter User ID",
                passwordError = null,
                errorMessage = null
            )

            return
        }


        // -----------------------------------------------------
        // VALIDATE PASSWORD
        // -----------------------------------------------------

        if (currentState.password.isBlank()) {

            _uiState.value = currentState.copy(
                userIdError = null,
                passwordError = "Please enter Password",
                errorMessage = null
            )

            return
        }


        // -----------------------------------------------------
        // START API CALL
        // -----------------------------------------------------

        viewModelScope.launch {

            _uiState.value = currentState.copy(
                userIdError = null,
                passwordError = null,
                errorMessage = null,
                isLoading = true,
                loginSuccess = false
            )


            val result = loginUseCase(
                username = currentState.userId.trim(),
                password = currentState.password,
                deviceId = deviceId
            )


            result
                .onSuccess { user ->

                    handleLoginResponse(user)
                }
                .onFailure { exception ->

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = false,
                        errorMessage =
                            exception.message
                                ?: "Unable to connect to server"
                    )
                }
        }
    }


    // =========================================================
    // HANDLE SERVER RESPONSE
    // =========================================================

    private fun handleLoginResponse(user: User) {

        // -----------------------------------------------------
        // INVALID LOGIN
        // -----------------------------------------------------

        if (user.id <= 0) {

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                loginSuccess = false,
                errorMessage =
                    user.errorMsg
                        ?: "Invalid User ID or Password"
            )

            return
        }


        // -----------------------------------------------------
        // ADMIN USER
        // -----------------------------------------------------

        if (
            user.userTypeId == 1 ||
            user.userTypeId == 2
        ) {

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                loginSuccess = false,
                errorMessage =
                    "Admin user cannot login into this app"
            )

            return
        }


        // -----------------------------------------------------
        // SUCCESS
        // -----------------------------------------------------

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            loginSuccess = true,
            errorMessage = null,
            user = user
        )
    }


    // =========================================================
    // CLEAR SUCCESS
    // =========================================================

    fun clearLoginSuccess() {

        _uiState.value =
            _uiState.value.copy(
                loginSuccess = false
            )
    }
}