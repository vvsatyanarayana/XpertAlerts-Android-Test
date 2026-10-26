
package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import com.aquila.pocxpertalerts.data.remote.UnsafeSslHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLHandshakeException

data class SettingsUiState(
    val webServiceUrl: String = SettingsDataStore.DEFAULT_URL,
    val isTesting: Boolean = false,
    val isSaving: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)

class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> =
        _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val savedUrl = settingsDataStore.webServiceUrl.first()

            _uiState.value = _uiState.value.copy(
                webServiceUrl = savedUrl
            )
        }
    }

    fun onUrlChanged(url: String) {
        _uiState.value = _uiState.value.copy(
            webServiceUrl = url,
            message = null,
            isError = false
        )
    }

    // Test the URL currently entered in Settings.
    // The URL is saved as the active configuration before testing.
    fun testUrl() {
        val urlText = _uiState.value.webServiceUrl.trim()

        if (!isValidUrl(urlText)) {
            showResult(
                "Please enter a valid HTTP or HTTPS URL",
                true
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isTesting = true,
                message = "Testing server connection...",
                isError = false
            )

            // Save the exact URL being tested.
            try {
                withContext(Dispatchers.IO) {
                    settingsDataStore.saveWebServiceUrl(urlText)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isTesting = false,
                    message = "Unable to save server URL",
                    isError = true
                )
                return@launch
            }

            val result = withContext(Dispatchers.IO) {
                testServer(urlText)
            }

            _uiState.value = _uiState.value.copy(
                isTesting = false,
                webServiceUrl = urlText,
                message = result.first,
                isError = !result.second
            )
        }
    }

    // Save the URL without testing it.
    // LoginRepository independently tests it on every login.
    fun saveUrl() {
        val urlText = _uiState.value.webServiceUrl.trim()

        if (!isValidUrl(urlText)) {
            showResult(
                "Please enter a valid HTTP or HTTPS URL",
                true
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                message = null,
                isError = false
            )

            try {
                withContext(Dispatchers.IO) {
                    settingsDataStore.saveWebServiceUrl(urlText)
                }

                _uiState.value = _uiState.value.copy(
                    webServiceUrl = urlText,
                    isSaving = false,
                    message = "Server URL saved successfully. It will be tested during login.",
                    isError = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = "Unable to save server URL",
                    isError = true
                )
            }
        }
    }

    private fun isValidUrl(value: String): Boolean {
        return try {
            val parsed = URL(value)

            (parsed.protocol == "http" ||
                    parsed.protocol == "https") &&
                    parsed.host.isNotBlank()

        } catch (e: Exception) {
            false
        }
    }

    // Match the old Java implementation:
    // GET the exact URL entered and accept HTTP 200 only.

    private suspend fun testServer(
        urlText: String
    ): Pair<String, Boolean> = withContext(Dispatchers.IO) {
        try {
            val apiService =
                com.aquila.pocxpertalerts.data.remote.RetrofitClient
                    .createApiService(urlText)

            val response = apiService.testServerConnection()
            val successful = response.isSuccessful

            response.errorBody()?.close()
            response.body()?.close()

            if (successful) {
                "Web service connection successful" to true
            } else {
                "Server connection failed" to false
            }
        } catch (e: Exception) {
            android.util.Log.e(
                "SettingsViewModel",
                "Server connection test failed for URL: $urlText",
                e
            )

            "Server connection failed" to false
        }
    }


    private fun showResult(
        message: String,
        isError: Boolean
    ) {
        _uiState.value = _uiState.value.copy(
            message = message,
            isError = isError,
            isTesting = false
        )
    }
}
