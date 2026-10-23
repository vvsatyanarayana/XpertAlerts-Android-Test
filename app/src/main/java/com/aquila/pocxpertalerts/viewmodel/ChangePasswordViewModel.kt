
package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.repository.ChangePasswordRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val isSuccess: Boolean = false
)

class ChangePasswordViewModel(
    private val repository: ChangePasswordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> =
        _uiState.asStateFlow()

    fun changePassword(
        username: String,
        userId: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (_uiState.value.isLoading) return

        val validationError = validatePasswords(
            oldPassword = oldPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )

        if (validationError != null) {
            _uiState.value = ChangePasswordUiState(
                message = validationError
            )
            return
        }

        if (username.isBlank() || userId.isBlank()) {
            _uiState.value = ChangePasswordUiState(
                message = "User session is missing. Please log in again."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState(isLoading = true)

            try {
                val result = repository.changePassword(
                    loginId = username,
                    oldPassword = oldPassword,
                    newPassword = newPassword,
                    userId = userId
                )

                _uiState.value = ChangePasswordUiState(
                    isLoading = false,
                    message = result.message,
                    isSuccess = result.success
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = ChangePasswordUiState(
                    isLoading = false,
                    message = exception.localizedMessage
                        ?: "Unable to change password. Please try again."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            message = null,
            isSuccess = false
        )
    }

    private fun validatePasswords(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): String? {
        if (oldPassword.isBlank()) {
            return "Please enter your current password."
        }

        if (newPassword.isBlank()) {
            return "Please enter your new password."
        }

        if (newPassword.length < 6) {
            return "Password must be at least 6 characters."
        }

        if (newPassword.length > 16) {
            return "Password should not exceed 16 characters."
        }

        if (newPassword.none { it.isDigit() }) {
            return "Password must contain at least one digit."
        }

        if (newPassword.none { !it.isLetterOrDigit() }) {
            return "Password must contain at least one special character."
        }

        if (newPassword == oldPassword) {
            return "New password must differ from your current password."
        }

        if (newPassword != confirmPassword) {
            return "New password and confirmation do not match."
        }

        return null
    }
}
