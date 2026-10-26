
package com.aquila.pocxpertalerts.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.domain.model.User
import com.aquila.pocxpertalerts.domain.usecase.LoginUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

data class LoginUiState(
    val userId: String = "",
    val password: String = "",
    val userIdError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val user: User? = null
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateUserId(value: String) {
        _uiState.value = _uiState.value.copy(
            userId = value,
            userIdError = null,
            errorMessage = null,
            successMessage = null
        )
    }

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null,
            errorMessage = null,
            successMessage = null
        )
    }

    fun login(deviceId: String) {
        val currentState = _uiState.value

        if (currentState.isLoading) return

        if (currentState.userId.isBlank()) {
            _uiState.value = currentState.copy(
                userIdError = "Please enter your User ID.",
                passwordError = null,
                errorMessage = null,
                successMessage = null
            )
            return
        }

        if (currentState.password.isBlank()) {
            _uiState.value = currentState.copy(
                userIdError = null,
                passwordError = "Please enter your password.",
                errorMessage = null,
                successMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                userIdError = null,
                passwordError = null,
                errorMessage = null,
                successMessage = null,
                isLoading = true,
                loginSuccess = false,
                user = null
            )

            try {
                val result = loginUseCase(
                    username = currentState.userId.trim(),
                    password = currentState.password,
                    deviceId = deviceId
                )

                result.fold(
                    onSuccess = { user ->
                        handleLoginResponse(user)
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Login request failed", exception)

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            loginSuccess = false,
                            successMessage = null,
                            errorMessage = friendlyError(exception)
                        )
                    }
                )
            } catch (exception: Exception) {
                Log.e(TAG, "Unexpected login error", exception)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loginSuccess = false,
                    errorMessage = friendlyError(exception),
                    successMessage = null
                )
            }
        }
    }

    private fun handleLoginResponse(user: User) {
        Log.d(
            "AlertsAPI",
            "Login userId=${user.id}, " +
                    "sessionId=${user.sessionId}, " +
                    "userTypeId=${user.userTypeId}"
        )

        if (user.id <= 0) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                loginSuccess = false,
                errorMessage = "Invalid User ID or password.",
                successMessage = null
            )
            return
        }

        if (user.userTypeId == 1 || user.userTypeId == 2) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                loginSuccess = false,
                errorMessage = "This account cannot access the mobile app.",
                successMessage = null
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            loginSuccess = false,
            errorMessage = null,
            successMessage = "Login successful. Welcome!",
            user = user
        )

        // Allow the success message to appear before navigation.
        viewModelScope.launch {
            delay(900)

            val state = _uiState.value
            if (state.user == user && state.successMessage != null) {
                _uiState.value = state.copy(
                    loginSuccess = true,
                    successMessage = null
                )
            }
        }
    }

    private fun friendlyError(exception: Throwable): String {
        val message = exception.message.orEmpty().lowercase()

        return when {
            exception is UnknownHostException ||
                    message.contains("unable to resolve host") ->
                "Unable to reach the server. Check your server URL and internet connection."

            exception is SocketTimeoutException ||
                    message.contains("timed out") ->
                "The server is taking too long to respond. Please try again."

            exception is SSLException ||
                    message.contains("certpathvalidatorexception") ||
                    message.contains("trust anchor") ||
                    message.contains("sslhandshakeexception") ->
                "Secure connection failed. Please contact support."

            exception is ConnectException ||
                    message.contains("failed to connect") ||
                    message.contains("connection refused") ->
                "Unable to connect to the server. Please try again later."

            message.contains("server url is not configured") ||
                    message.contains("configure the server url") ->
                "Please configure the server URL in Settings."

            message.contains("server test failed") ||
                    message.contains("login cancelled") ->
                "Server connection failed. Please check your server URL in Settings."

            message.contains("http 401") ||
                    message.contains("http 403") ||
                    message.contains("invalid user") ||
                    message.contains("invalid credentials") ->
                "Invalid User ID or password."




            message.contains("http 5") ->
                "The server is temporarily unavailable. Please try again later."

            else ->
                "Server URL is incorrect. Please check Settings."
        }
    }

    fun clearLoginSuccess() {
        _uiState.value = _uiState.value.copy(
            loginSuccess = false,
            successMessage = null
        )
    }
}
