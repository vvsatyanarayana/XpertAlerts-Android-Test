package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.model.ArchivedAlertDto
import com.aquila.pocxpertalerts.data.repository.ArchivedAlertsRepository
import com.aquila.pocxpertalerts.data.repository.ArchivedSearchCriteria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArchivedAlertsViewModel(
    private val repository: ArchivedAlertsRepository
) : ViewModel() {
    private val _alerts = MutableStateFlow<List<ArchivedAlertDto>>(emptyList())
    val alerts: StateFlow<List<ArchivedAlertDto>> = _alerts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var lastUserId = ""
    private var lastSessionId = ""
    private var lastCriteria = ArchivedSearchCriteria()

    fun load(userId: String, sessionId: String, criteria: ArchivedSearchCriteria = lastCriteria) {
        lastUserId = userId
        lastSessionId = sessionId
        lastCriteria = criteria
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getArchivedAlerts(userId, sessionId, criteria).fold(
                onSuccess = { _alerts.value = it },
                onFailure = { _errorMessage.value = it.message ?: "Unable to load archived alerts." }
            )
            _isLoading.value = false
        }
    }

    fun refresh() = load(lastUserId, lastSessionId, lastCriteria)
    fun clearError() { _errorMessage.value = null }
}
