package com.aquila.pocxpertalerts.viewmodel.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

data class SettingsUiState(

    val webServiceUrl: String =
        "https://www.xpertalerts.com/ams/webservice/check",

    val testUrl: String =
        "https://www.xpertalerts.com/ams/webservice/check",

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
        SettingsDataStore(
            application.applicationContext
        )

    private val _uiState =
        MutableStateFlow(
            SettingsUiState()
        )

    val uiState: StateFlow<SettingsUiState> =
        _uiState.asStateFlow()

    init {
        loadSavedUrl()
    }

    // ========================================================
    // LOAD SAVED URL
    // ========================================================

    private fun loadSavedUrl() {

        viewModelScope.launch {

            val savedUrl =
                settingsDataStore
                    .webServiceUrl
                    .first()

            if (!savedUrl.isNullOrBlank()) {

                _uiState.value =
                    _uiState.value.copy(
                        webServiceUrl = savedUrl,
                        testUrl = savedUrl
                    )
            }
        }
    }

    // ========================================================
    // UPDATE URL
    // ========================================================

    fun updateWebServiceUrl(
        value: String
    ) {

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

    // ========================================================
    // TEST SERVER CONNECTION
    // ========================================================

    fun startTest() {

        val url =
            _uiState.value
                .webServiceUrl
                .trim()

        // ----------------------------------------------------
        // EMPTY URL
        // ----------------------------------------------------

        if (url.isBlank()) {

            _uiState.value =
                _uiState.value.copy(
                    isUrlValid = false,
                    errorMessage =
                        "Please enter a Web Service URL",
                    testSuccess = false
                )

            return
        }

        // ----------------------------------------------------
        // URL VALIDATION
        // ----------------------------------------------------

        if (
            !url.startsWith("http://") &&
            !url.startsWith("https://")
        ) {

            _uiState.value =
                _uiState.value.copy(
                    isUrlValid = false,
                    errorMessage =
                        "Please enter a valid URL",
                    testSuccess = false
                )

            return
        }

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isTesting = true,
                    isUrlValid = null,
                    errorMessage = null,
                    testSuccess = false
                )

            try {

                val responseCode =
                    withContext(Dispatchers.IO) {

                        val connection =
                            URL(url)
                                .openConnection()
                                    as HttpURLConnection

                        try {

                            connection.requestMethod = "GET"

                            connection.connectTimeout =
                                10000

                            connection.readTimeout =
                                10000

                            connection.instanceFollowRedirects =
                                true

                            connection.connect()

                            connection.responseCode

                        } finally {

                            connection.disconnect()
                        }
                    }

                // ------------------------------------------------
                // ORIGINAL APP EXPECTS HTTP 200
                // ------------------------------------------------

                if (responseCode == 200) {

                    _uiState.value =
                        _uiState.value.copy(
                            isTesting = false,
                            isUrlValid = true,
                            errorMessage = null,
                            testSuccess = true
                        )

                } else {

                    _uiState.value =
                        _uiState.value.copy(
                            isTesting = false,
                            isUrlValid = false,
                            errorMessage =
                                "Server returned HTTP $responseCode",
                            testSuccess = false
                        )
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _uiState.value =
                    _uiState.value.copy(
                        isTesting = false,
                        isUrlValid = false,
                        errorMessage =
                            "${e.javaClass.simpleName}: ${e.message}",
                        testSuccess = false
                    )
            }
        }
    }

    // ========================================================
    // SAVE URL
    // ========================================================

    fun saveUrl() {

        val url =
            _uiState.value
                .webServiceUrl
                .trim()

        // ----------------------------------------------------
        // EMPTY
        // ----------------------------------------------------

        if (url.isBlank()) {

            _uiState.value =
                _uiState.value.copy(
                    errorMessage =
                        "Please enter a Web Service URL",
                    saveSuccess = false
                )

            return
        }

        // ----------------------------------------------------
        // VALIDATE
        // ----------------------------------------------------

        if (
            !url.startsWith("http://") &&
            !url.startsWith("https://")
        ) {

            _uiState.value =
                _uiState.value.copy(
                    errorMessage =
                        "Please enter a valid URL",
                    saveSuccess = false
                )

            return
        }

        viewModelScope.launch {

            try {

                settingsDataStore
                    .saveWebServiceUrl(url)

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
                        saveSuccess = false,
                        errorMessage =
                            e.message
                                ?: "Unable to save URL"
                    )
            }
        }
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