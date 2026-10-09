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
    val webServiceUrl: String =
        SettingsDataStore.DEFAULT_URL,
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

    fun testUrl() {
        val urlText = _uiState.value.webServiceUrl.trim()

        if (!isValidUrl(urlText)) {
            showResult("Please enter a valid HTTP or HTTPS URL", true)
            return
        }

        _uiState.value = _uiState.value.copy(
            isTesting = true,
            message = "Testing server connection...",
            isError = false
        )

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                testServer(urlText)
            }

            _uiState.value = _uiState.value.copy(
                isTesting = false,
                message = result.first,
                isError = !result.second
            )
        }
    }

    fun saveUrl() {
        val urlText = _uiState.value.webServiceUrl.trim()

        if (!isValidUrl(urlText)) {
            showResult("Please enter a valid HTTP or HTTPS URL", true)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                message = null,
                isError = false
            )

            try {
                settingsDataStore.saveWebServiceUrl(urlText)

                _uiState.value = _uiState.value.copy(
                    webServiceUrl = urlText,
                    isSaving = false,
                    message = "Server URL saved successfully",
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
                    !parsed.host.isNullOrBlank()
        } catch (_: Exception) {
            false
        }
    }

    private fun testServer(urlText: String): Pair<String, Boolean> {
        var connection: HttpURLConnection? = null

        return try {
            connection = URL(urlText)
                .openConnection() as HttpURLConnection


            // Bypass SSL validation so the test works even when the server
            // has an expired or self-signed certificate.
            if (connection is HttpsURLConnection) {
                (connection as HttpsURLConnection).apply {
                    sslSocketFactory = UnsafeSslHelper.sslSocketFactory
                    hostnameVerifier = javax.net.ssl.HostnameVerifier { _, _ -> true }
                }
            }


            connection.requestMethod = "GET"
            connection.connectTimeout = 3_000
            connection.readTimeout = 3_000
            connection.useCaches = false

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                "Server connection successful (HTTP 200)" to true
            } else {
                "Server test failed. HTTP status: $responseCode" to false
            }
        } catch (e: Exception) {
            val isSslError = generateSequence<Throwable>(e) { it.cause }
                .any { it is SSLHandshakeException }

            val message = when {
                isSslError ->
                    "SSL certificate validation failed."

                e is java.net.SocketTimeoutException ->
                    "Connection timed out."

                else ->
                    "Server connection failed: ${e.localizedMessage ?: "Network error"}"
            }

            message to false
        } finally {
            connection?.disconnect()
        }
    }

    private fun showResult(message: String, isError: Boolean) {
        _uiState.value = _uiState.value.copy(
            message = message,
            isError = isError,
            isTesting = false
        )
    }

}



