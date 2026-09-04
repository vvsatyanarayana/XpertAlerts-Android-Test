package com.aquila.pocxpertalerts.viewmodel.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val webServiceUrl: String = "",
    val isTesting: Boolean = false,
    val isUrlValid: Boolean? = null,
    val errorMessage: String? = null
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState()
    )

    val uiState: StateFlow<SettingsUiState> =
        _uiState.asStateFlow()

    fun updateWebServiceUrl(url: String) {

        _uiState.value = _uiState.value.copy(
            webServiceUrl = url,
            isUrlValid = null,
            errorMessage = null
        )
    }

    fun testUrl() {

        val url = _uiState.value.webServiceUrl.trim()

        if (url.isBlank()) {

            _uiState.value = _uiState.value.copy(
                isUrlValid = false,
                errorMessage = "Please enter web service URL"
            )

            return
        }

        _uiState.value = _uiState.value.copy(
            isTesting = true,
            isUrlValid = null,
            errorMessage = null
        )

        // Real URL testing will be added
        // when we create the Repository/API layer.
    }
}