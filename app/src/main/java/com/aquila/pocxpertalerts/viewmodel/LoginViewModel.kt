package com.aquila.pocxpertalerts.viewmodel

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
    val user: User? = null,
    val passwordExpiredUser: User? = null
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
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
        val current = _uiState.value
        if (current.isLoading) return

        if (current.userId.isBlank()) {
            _uiState.value = current.copy(
                userIdError = "Please enter user ID",
                passwordError = null,
                errorMessage = null,
                successMessage = null
            )
            return
        }
        if (current.password.isBlank()) {
            _uiState.value = current.copy(
                userIdError = null,
                passwordError = "Please enter password",
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
                user = null,
                passwordExpiredUser = null
            )

            val result = try {
                loginUseCase(
                    username = current.userId.trim(),
                    password = current.password,
                    deviceId = deviceId
                )
            } catch (e: Exception) {
                Result.failure(e)
            }

            result.fold(
                onSuccess = ::handleLoginResponse,
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = false,
                        successMessage = null,
                        errorMessage = friendlyError(error)
                    )
                }
            )
        }
    }

    private fun handleLoginResponse(user: User) {
        if (user.id <= 0) {
            val serverMessage = user.errorMsg
                ?.replace("<br><br>", "\n", ignoreCase = true)
                ?.replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
                ?.replace(Regex("<[^>]*>"), " ")
                ?.replace(Regex("\\s+"), " ")
                ?.trim()
                ?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
                ?: "User Name or Password you entered is incorrect"

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                loginSuccess = false,
                errorMessage = serverMessage,
                successMessage = null
            )
            return
        }

        if (user.userTypeId == 1 || user.userTypeId == 2) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                loginSuccess = false,
                errorMessage = "Admin user cannot login into this app",
                successMessage = null
            )
            return
        }

        // The legacy app redirects users with any non-zero expiry flag to password reset.
        if (user.pwdExpiryFlag != 0) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                loginSuccess = false,
                errorMessage = null,
                successMessage = null,
                user = user,
                passwordExpiredUser = user
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
        viewModelScope.launch {
            delay(700)
            val state = _uiState.value
            if (state.user == user && state.successMessage != null) {
                _uiState.value = state.copy(loginSuccess = true, successMessage = null)
            }
        }
    }

    private fun friendlyError(error: Throwable): String {
        val message = error.message.orEmpty()
        val lower = message.lowercase()
        return when {
            error is UnknownHostException || lower.contains("unable to resolve host") || lower.contains("internet connection") ->
                "Please check your internet connection"
            error is SocketTimeoutException || lower.contains("timed out") ->
                "The server is taking too long to respond. Please try again."
            error is SSLException || lower.contains("ssl") || lower.contains("certificate") || lower.contains("trust anchor") ->
                "Secure connection failed. Please check the server certificate."
            error is ConnectException || lower.contains("failed to connect") || lower.contains("connection refused") ->
                "Unable to connect to the server. Please try again later."
            lower.contains("invalid web service url") || lower.contains("invalid server url") ->
                "Invalid Web Service URL"
            lower.contains("http 401") || lower.contains("http 403") || lower.contains("invalid credentials") ->
                "User Name or Password you entered is incorrect"
            lower.contains("http 5") || lower.contains("internal server error") ->
                "Internal server error, please try again later!"
            lower.contains("configure the server url") || lower.contains("server url is not configured") ->
                "Please configure the server URL in Settings."
            else -> message.takeIf { it.isNotBlank() } ?: "Unable to connect to server. Please try again."
        }
    }

    fun clearLoginSuccess() {
        _uiState.value = _uiState.value.copy(loginSuccess = false, successMessage = null)
    }

    fun clearPasswordExpiredUser() {
        _uiState.value = _uiState.value.copy(passwordExpiredUser = null)
    }
}
