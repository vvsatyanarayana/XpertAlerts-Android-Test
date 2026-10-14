package com.aquila.pocxpertalerts.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.model.AlertDto
import com.aquila.pocxpertalerts.data.repository.AlertRepository
import com.aquila.pocxpertalerts.data.repository.ApiDropdownOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val repository: AlertRepository
) : ViewModel() {

    private val _alerts = MutableStateFlow<List<AlertDto>>(emptyList())
    val alerts: StateFlow<List<AlertDto>> = _alerts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _applications = MutableStateFlow<List<ApiDropdownOption>>(emptyList())
    val applications: StateFlow<List<ApiDropdownOption>> = _applications.asStateFlow()

    private val _alertDefinitions = MutableStateFlow<List<ApiDropdownOption>>(emptyList())
    val alertDefinitions: StateFlow<List<ApiDropdownOption>> = _alertDefinitions.asStateFlow()

    private val _isLoadingApplications = MutableStateFlow(false)
    val isLoadingApplications: StateFlow<Boolean> = _isLoadingApplications.asStateFlow()

    private val _isLoadingDefinitions = MutableStateFlow(false)
    val isLoadingDefinitions: StateFlow<Boolean> = _isLoadingDefinitions.asStateFlow()

    private val _dropdownErrorMessage = MutableStateFlow<String?>(null)
    val dropdownErrorMessage: StateFlow<String?> = _dropdownErrorMessage.asStateFlow()

    fun loadApplications(userId: String, userTypeId: String, sessionId: String) {
        if (userId.isBlank() || sessionId.isBlank()) {
            _dropdownErrorMessage.value = "Unable to load applications: user or session information is missing."
            return
        }
        viewModelScope.launch {
            _isLoadingApplications.value = true
            _dropdownErrorMessage.value = null
            try {
                repository.getApplications(userId, userTypeId, sessionId).fold(
                    onSuccess = { options ->
                        _applications.value = options
                        Log.d("ApplicationAPI", "Loaded ${options.size} application options")
                    },
                    onFailure = { error ->
                        _applications.value = emptyList()
                        _dropdownErrorMessage.value = error.message ?: "Unable to load applications."
                        Log.e("ApplicationAPI", "Application list failed", error)
                    }
                )
            } finally {
                _isLoadingApplications.value = false
            }
        }
    }

    fun loadAlertDefinitions(applicationId: String, sessionId: String) {
        _alertDefinitions.value = emptyList()
        _dropdownErrorMessage.value = null
        if (applicationId.isBlank() || applicationId == "0") return
        if (sessionId.isBlank()) {
            _dropdownErrorMessage.value = "Unable to load alert definitions: session information is missing."
            return
        }
        viewModelScope.launch {
            _isLoadingDefinitions.value = true
            try {
                repository.getAlertDefinitions(applicationId, sessionId).fold(
                    onSuccess = { options ->
                        _alertDefinitions.value = options
                        Log.d("AlertDefinitionAPI", "Loaded ${options.size} alert definitions for application $applicationId")
                    },
                    onFailure = { error ->
                        _alertDefinitions.value = emptyList()
                        _dropdownErrorMessage.value = error.message ?: "Unable to load alert definitions."
                        Log.e("AlertDefinitionAPI", "Alert definition list failed", error)
                    }
                )
            } finally {
                _isLoadingDefinitions.value = false
            }
        }
    }

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
            Log.d("AlertsAPI", "Calling getAlertsForDevices")
            Log.d("AlertCompare", "Search dates: $startDate to $endDate")
            try {
                val result = repository.getAlerts(
                    userId = userId,
                    startDate = startDate,
                    endDate = endDate,
                    searchByKeyword = keyword,
                    viewByFlag = viewByFlag,
                    applicationId = applicationId,
                    alertDefinitionId = alertDefinitionId,
                    sessionId = sessionId
                )
                result.fold(
                    onSuccess = { response ->
                        _alerts.value = response
                        Log.d("AlertCompare", "Total alerts received: ${response.size}")
                        response.take(10).forEachIndexed { index, alert ->
                            Log.d("AlertCompare", """
                                Alert #${index + 1}
                                id=${alert.id}
                                alertId=${alert.alertId}
                                subject=${alert.subject}
                                message=${alert.message}
                                alertSendDate=${alert.alertSendDate}
                                groupBy=${alert.groupBy}
                            """.trimIndent())
                        }
                        onSuccess(response)
                    },
                    onFailure = { error ->
                        _errorMessage.value =
                            error.message ?: "Unable to retrieve alerts."

                        Log.e(
                            "AlertsAPI",
                            "getAlertsForDevices failed",
                            error
                        )
                    }
                )
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Unexpected API error"
                Log.e("AlertsAPI", "Unexpected error", error)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _errorMessage.value = null }
    fun clearDropdownError() { _dropdownErrorMessage.value = null }
    fun clearAlerts() { _alerts.value = emptyList() }
}
