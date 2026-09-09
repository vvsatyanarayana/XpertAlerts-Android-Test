package com.aquila.pocxpertalerts.viewmodel.login

import android.app.Application
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.model.User
import com.aquila.pocxpertalerts.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val userId: String = "",
    val password: String = "",

    val isLoading: Boolean = false,

    val user: User? = null,

    // Field-specific validation errors
    val userIdError: String? = null,
    val passwordError: String? = null,

    // Server/API error
    val errorMessage: String? = null,

    // Login success
    val loginSuccess: Boolean = false
)

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        LoginRepository(application.applicationContext)

    private val _uiState =
        MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()


    // ========================================================
    // USER ID
    // ========================================================

    fun updateUserId(value: String) {

        _uiState.value = _uiState.value.copy(
            userId = value,
            userIdError = null,
            errorMessage = null
        )
    }


    // ========================================================
    // PASSWORD
    // ========================================================

    fun updatePassword(value: String) {

        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null,
            errorMessage = null
        )
    }


    // ========================================================
    // LOGIN
    // ========================================================

    fun login() {

        val currentState = _uiState.value

        val username =
            currentState.userId.trim()

        val password =
            currentState.password


        // ----------------------------------------------------
        // VALIDATE USER ID
        // ----------------------------------------------------

        if (username.isBlank()) {

            _uiState.value = currentState.copy(
                userIdError = "Please enter user ID",
                passwordError = null,
                errorMessage = null
            )

            return
        }


        // ----------------------------------------------------
        // VALIDATE PASSWORD
        // ----------------------------------------------------

        if (password.isBlank()) {

            _uiState.value = currentState.copy(
                userIdError = null,
                passwordError = "Please enter password",
                errorMessage = null
            )

            return
        }


        // ----------------------------------------------------
        // DEVICE ID
        // ----------------------------------------------------

        val deviceId = Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            Settings.Secure.ANDROID_ID
        )


        // ----------------------------------------------------
        // API CALL
        // ----------------------------------------------------

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                userIdError = null,
                passwordError = null,
                errorMessage = null,
                loginSuccess = false
            )


            val result = repository.login(
                username = username,
                password = password,

            )


            result.fold(

                // =================================================
                // SUCCESS
                // =================================================

                onSuccess = { user ->

                    if (user == null) {

                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                errorMessage =
                                    "Invalid user ID or password"
                            )

                    } else if (user.id <= 0) {

                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                errorMessage =
                                    user.errorMsg
                                        ?: "Invalid user ID or password"
                            )

                    } else {

                        _uiState.value =
                            _uiState.value.copy(
                                isLoading = false,
                                user = user,
                                loginSuccess = true,
                                errorMessage = null
                            )
                    }
                },


                // =================================================
                // FAILURE
                // =================================================

                onFailure = { exception ->

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage =
                                exception.message
                                    ?: "Unable to connect to server"
                        )
                }
            )
        }
    }


    // ========================================================
    // CLEAR LOGIN SUCCESS
    // ========================================================

    fun clearLoginSuccess() {

        _uiState.value =
            _uiState.value.copy(
                loginSuccess = false
            )
    }


    // ========================================================
    // CLEAR ERROR
    // ========================================================

    fun clearError() {

        _uiState.value =
            _uiState.value.copy(
                errorMessage = null
            )
    }
}