package com.aquila.pocxpertalerts.viewmodel.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.local.SettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

// ============================================================
// SETTINGS UI STATE
// ============================================================

data class SettingsUiState(

    val webServiceUrl: String =
        "https://www.xpertalerts.com/ams/webservice/",

    val isTesting: Boolean = false,

    val isUrlValid: Boolean? = null,

    val errorMessage: String? = null,

    val testSuccess: Boolean = false,

    val saveSuccess: Boolean = false
)


// ============================================================
// SETTINGS VIEW MODEL
// ============================================================

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val settingsDataStore =
        SettingsDataStore(
            application.applicationContext
        )

    private val _uiState =
        MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> =
        _uiState.asStateFlow()


    // ========================================================
    // INITIALIZE
    // ========================================================

    init {
        loadSavedUrl()
    }


    // ========================================================
    // LOAD SAVED URL
    // ========================================================

    private fun loadSavedUrl() {

        viewModelScope.launch {

            settingsDataStore.webServiceUrl.collect { savedUrl ->

                if (!savedUrl.isNullOrBlank()) {

                    _uiState.value =
                        _uiState.value.copy(
                            webServiceUrl = savedUrl
                        )
                }
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
                isUrlValid = null,
                errorMessage = null,
                testSuccess = false,
                saveSuccess = false
            )
    }


    // ========================================================
    // TEST CONNECTION
    // ========================================================

    fun startTest() {

        val url =
            _uiState.value.webServiceUrl.trim()


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
        // URL FORMAT
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


        // ----------------------------------------------------
        // TEST SERVER
        // ----------------------------------------------------

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isTesting = true,
                    isUrlValid = null,
                    errorMessage = null,
                    testSuccess = false
                )


            val result =
                withContext(Dispatchers.IO) {

                    try {

                        val connection =
                            URL(url).openConnection()
                                    as HttpURLConnection

                        connection.requestMethod = "GET"

                        connection.connectTimeout = 10000

                        connection.readTimeout = 10000

                        connection.instanceFollowRedirects = true

                        connection.connect()

                        val responseCode =
                            connection.responseCode

                        connection.disconnect()

                        responseCode in 200..499

                    } catch (e: Exception) {

                        false
                    }
                }


            // ------------------------------------------------
            // RESULT
            // ------------------------------------------------

            if (result) {

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
                        testSuccess = false,
                        errorMessage =
                            "Unable to connect to server"
                    )
            }
        }
    }


    // ========================================================
    // SAVE URL
    // ========================================================

    fun saveUrl() {

        var url =
            _uiState.value.webServiceUrl.trim()


        // ----------------------------------------------------
        // EMPTY URL
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
        // URL FORMAT
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


        // ----------------------------------------------------
        // ENSURE TRAILING SLASH
        // ----------------------------------------------------

        if (!url.endsWith("/")) {
            url += "/"
        }


        // ----------------------------------------------------
        // SAVE
        // ----------------------------------------------------

        viewModelScope.launch {

            try {

                settingsDataStore
                    .saveWebServiceUrl(url)


                _uiState.value =
                    _uiState.value.copy(
                        webServiceUrl = url,
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