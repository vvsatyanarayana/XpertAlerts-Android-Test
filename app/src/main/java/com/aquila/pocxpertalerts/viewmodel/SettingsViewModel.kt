package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.URL
import javax.net.ssl.SSLException

data class SettingsUiState(
    val webServiceUrl: String = SettingsDataStore.DEFAULT_URL,
    val isTesting: Boolean = false,
    val isSaving: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
    val showSaveAnywayConfirmation: Boolean = false,
    val saveCompleted: Boolean = false
)

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val savedUrl = settingsDataStore.webServiceUrl.first()
            _uiState.value = _uiState.value.copy(webServiceUrl = savedUrl)
        }
    }

    fun onUrlChanged(url: String) {
        _uiState.value = _uiState.value.copy(
            webServiceUrl = url,
            message = null,
            isError = false,
            showSaveAnywayConfirmation = false,
            saveCompleted = false
        )
    }

    /** Test only; never modifies the saved URL. */
    fun testUrl() {
        val url = _uiState.value.webServiceUrl.trim()
        val validationMessage = validateUrl(url)
        if (validationMessage != null) {
            showResult(validationMessage, true)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isTesting = true,
                message = null,
                isError = false
            )
            val result = withContext(Dispatchers.IO) { testServer(url) }
            _uiState.value = _uiState.value.copy(
                isTesting = false,
                message = result.first,
                isError = !result.second
            )
        }
    }

    /** Save follows the old app: test first; ask before saving an unreachable URL. */
    fun saveUrl() {
        val url = _uiState.value.webServiceUrl.trim()
        val validationMessage = validateUrl(url)
        if (validationMessage != null) {
            showResult(validationMessage, true)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                message = null,
                isError = false,
                showSaveAnywayConfirmation = false,
                saveCompleted = false
            )

            val testResult = withContext(Dispatchers.IO) { testServer(url) }
            if (testResult.second) {
                persistUrl(url)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = null,
                    isError = true,
                    showSaveAnywayConfirmation = true
                )
            }
        }
    }

    fun confirmSaveAnyway() {
        if (!_uiState.value.showSaveAnywayConfirmation) return
        val url = _uiState.value.webServiceUrl.trim()
        _uiState.value = _uiState.value.copy(showSaveAnywayConfirmation = false)
        viewModelScope.launch { persistUrl(url) }
    }

    fun dismissSaveAnywayConfirmation() {
        _uiState.value = _uiState.value.copy(
            showSaveAnywayConfirmation = false,
            message = "Configuration test failed. Try again...",
            isError = true
        )
    }

    fun consumeSaveCompleted() {
        _uiState.value = _uiState.value.copy(saveCompleted = false)
    }

    private suspend fun persistUrl(url: String) {
        _uiState.value = _uiState.value.copy(isSaving = true, message = null)
        try {
            withContext(Dispatchers.IO) {
                settingsDataStore.saveWebServiceUrl(url)
            }
            _uiState.value = _uiState.value.copy(
                webServiceUrl = url,
                isSaving = false,
                message = "Configuration saved successfully",
                isError = false,
                showSaveAnywayConfirmation = false,
                saveCompleted = true
            )
        } catch (_: Exception) {
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                message = "Unable to save server URL. Please try again.",
                isError = true,
                saveCompleted = false
            )
        }
    }

    private fun validateUrl(value: String): String? {
        if (value.isBlank()) return "Please enter web service URL"
        return try {
            val parsed = URL(value)
            if ((parsed.protocol.equals("http", true) || parsed.protocol.equals("https", true)) &&
                parsed.host.isNotBlank()
            ) null else "Invalid Web Service URL"
        } catch (_: Exception) {
            "Invalid Web Service URL"
        }
    }

    private suspend fun testServer(url: String): Pair<String, Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.createApiService(url).testServerConnection()
            val successful = response.code() == 200
            response.errorBody()?.close()
            response.body()?.close()
            if (successful) {
                "Configuration test successful" to true
            } else {
                "Configuration test failed. Try again..." to false
            }
        } catch (error: Exception) {
            val message = when (error) {
                is UnknownHostException -> "Please check your internet connection"
                is SocketTimeoutException -> "Configuration test failed. The server timed out. Try again..."
                is ConnectException -> "Configuration test failed. Unable to connect to server."
                is SSLException -> "Configuration test failed. Secure connection could not be established."
                else -> "Configuration test failed. Try again..."
            }
            message to false
        }
    }

    private fun showResult(message: String, isError: Boolean) {
        _uiState.value = _uiState.value.copy(
            message = message,
            isError = isError,
            isTesting = false,
            isSaving = false
        )
    }
}
