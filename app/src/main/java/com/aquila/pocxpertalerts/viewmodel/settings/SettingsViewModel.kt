package com.aquila.pocxpertalerts.viewmodel.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
data class SettingsUiState(
    val webServiceUrl: String =
        "https://www.xpertalerts.com/ams/webservice/",

    val testUrl: String =
        "https://www.xpertalerts.com/ams/webservice/",

    val isTesting: Boolean = false,

    val isUrlValid: Boolean? = null,

    val errorMessage: String? = null,

    val testSuccess: Boolean = false,

    val saveSuccess: Boolean = false
)

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val settingsDataStore =
        SettingsDataStore(application.applicationContext)

    private val _uiState =
        MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> =
        _uiState.asStateFlow()

    init {
        loadSavedUrl()
    }

    // --------------------------------------------------------
    // LOAD SAVED URL
    // --------------------------------------------------------

    private fun loadSavedUrl() {

        viewModelScope.launch {

            settingsDataStore.webServiceUrl.collect { savedUrl ->

                if (!savedUrl.isNullOrBlank()) {

                    _uiState.value =
                        _uiState.value.copy(
                            webServiceUrl = savedUrl,
                            testUrl = savedUrl
                        )
                }
            }
        }
    }

    // --------------------------------------------------------
    // UPDATE URL
    // --------------------------------------------------------

    fun updateWebServiceUrl(value: String) {

        _uiState.value =
            _uiState.value.copy(
                webServiceUrl = value,
                testUrl = value,
                isUrlValid = null,
                errorMessage = null,
                testSuccess = false,
                saveSuccess = false
            )
    }

    // --------------------------------------------------------
    // TEST CONNECTION
    // --------------------------------------------------------

    fun startTest() {

        var url = _uiState.value.webServiceUrl.trim()

        if (url.isBlank()) {

            _uiState.value = _uiState.value.copy(
                isUrlValid = false,
                errorMessage = "Please enter a Web Service URL",
                testSuccess = false
            )

            return
        }

        if (
            !url.startsWith("http://") &&
            !url.startsWith("https://")
        ) {
            url = "https://$url"
        }

        // Remove trailing slash
        url = url.trimEnd('/')

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isTesting = true,
                isUrlValid = null,
                errorMessage = null,
                testSuccess = false
            )

            try {

                val result = withContext(Dispatchers.IO) {

                    val testEndpoint =
                        "$url/authenticateUserForDevices"

                    val connection =
                        java.net.URL(testEndpoint)
                            .openConnection()
                                as java.net.HttpURLConnection

                    connection.requestMethod = "GET"

                    connection.connectTimeout = 15000
                    connection.readTimeout = 15000

                    connection.setRequestProperty(
                        "Accept",
                        "application/json"
                    )

                    try {

                        connection.connect()

                        connection.responseCode

                    } finally {

                        connection.disconnect()
                    }
                }

                // ------------------------------------------------
                // RESPONSE
                // ------------------------------------------------

                if (result in 200..499) {

                    _uiState.value =
                        _uiState.value.copy(
                            isTesting = false,
                            isUrlValid = true,
                            testSuccess = true,
                            errorMessage = null
                        )

                } else {

                    _uiState.value =
                        _uiState.value.copy(
                            isTesting = false,
                            isUrlValid = false,
                            testSuccess = false,
                            errorMessage =
                                "Server returned HTTP $result"
                        )
                }

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isTesting = false,
                        isUrlValid = false,
                        testSuccess = false,
                        errorMessage =
                            "Connection failed: " +
                                    "${e.javaClass.simpleName}: " +
                                    "${e.message ?: "No additional information"}"
                    )
            }
        }
    }
    // --------------------------------------------------------
    // SAVE URL
    // --------------------------------------------------------

    fun saveUrl() {

        var url =
            _uiState.value.webServiceUrl.trim()

        if (
            !url.startsWith("http://") &&
            !url.startsWith("https://")
        ) {
            url = "https://$url"
        }

        if (url.isBlank()) {

            _uiState.value =
                _uiState.value.copy(
                    errorMessage =
                        "Please enter a Web Service URL",
                    saveSuccess = false
                )

            return
        }

        viewModelScope.launch {

            try {

                settingsDataStore.saveWebServiceUrl(url)

                _uiState.value =
                    _uiState.value.copy(
                        webServiceUrl = url,
                        testUrl = url,
                        errorMessage = null,
                        saveSuccess = true
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isTesting = false,
                        isUrlValid = false,
                        errorMessage =
                            "Connection failed: ${e.javaClass.simpleName}: ${e.message}",
                        testSuccess = false
                    )
            }
        }
    }

    // --------------------------------------------------------
    // CLEAR ERROR
    // --------------------------------------------------------

    fun clearError() {

        _uiState.value =
            _uiState.value.copy(
                errorMessage = null
            )
    }
}