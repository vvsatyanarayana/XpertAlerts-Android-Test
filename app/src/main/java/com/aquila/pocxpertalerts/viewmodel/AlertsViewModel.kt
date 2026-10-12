
package com.aquila.pocxpertalerts.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.data.repository.AlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val repository: AlertRepository
) : ViewModel() {

    // Holds the alerts returned by the API
    private val _alerts =
        MutableStateFlow<List<AlertDto>>(emptyList())

    val alerts: StateFlow<List<AlertDto>> =
        _alerts.asStateFlow()

    // Loading state
    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    // Error state
    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    /**
     * Fetch alerts from the backend.
     *
     * onSuccess is called only after the repository
     * returns a successful result.
     */
    fun searchAlerts(
        userId: String,
        startDate: String,
        endDate: String,
        keyword: String,
        viewByFlag: String,
        applicationId: String,
        alertDefinitionId: String,
        sessionId: String,
        onSuccess: (List<AlertDto>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            Log.d(
                "AlertsAPI",
                "Calling getAlertsForDevices"
            )

            try {
                val result = repository.getAlerts(
                    userId = userId,
                    startDate = startDate,
                    endDate = endDate,
                    keyword = keyword,
                    viewByFlag = viewByFlag,
                    applicationId = applicationId,
                    alertDefinitionId = alertDefinitionId,
                    sessionId = sessionId
                )

                result.fold(
                    onSuccess = { response ->

                        // Save the actual API results
                        _alerts.value = response

                        Log.d(
                            "AlertsAPI",
                            "Success: received ${response.size} alerts"
                        )

                        response.forEach { alert ->
                            Log.d(
                                "AlertsAPI",
                                "id=${alert.id}, subject=${alert.subject}"
                            )
                        }

                        // Notify the caller that the search succeeded
                        onSuccess(response)
                    },

                    onFailure = { error ->
                        _errorMessage.value =
                            error.message
                                ?: "Unable to retrieve alerts"

                        Log.e(
                            "AlertsAPI",
                            "Request failed: ${error.message}",
                            error
                        )
                    }
                )
            } catch (error: Exception) {
                _errorMessage.value =
                    error.message ?: "Unexpected API error"

                Log.e(
                    "AlertsAPI",
                    "Unexpected error",
                    error
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear an old error before starting a new search.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Clear the current results when needed.
     */
    fun clearAlerts() {
        _alerts.value = emptyList()
    }
}
