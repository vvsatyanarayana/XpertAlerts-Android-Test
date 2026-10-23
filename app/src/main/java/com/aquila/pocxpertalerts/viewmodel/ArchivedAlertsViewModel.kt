
package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.model.ArchivedAlertDto
import com.aquila.pocxpertalerts.data.repository.ArchivedAlertsCache
import com.aquila.pocxpertalerts.data.repository.ArchivedAlertsRepository
import com.aquila.pocxpertalerts.data.repository.ArchivedSearchCriteria
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArchivedAlertsViewModel(
    private val repository: ArchivedAlertsRepository,
    private val cache: ArchivedAlertsCache
) : ViewModel() {

    private val _alerts =
        MutableStateFlow<List<ArchivedAlertDto>>(emptyList())
    val alerts: StateFlow<List<ArchivedAlertDto>> = _alerts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var lastUserId = ""
    private var lastSessionId = ""
    private var lastCriteria = ArchivedSearchCriteria()
    private var loadJob: Job? = null

    fun load(
        userId: String,
        sessionId: String,
        criteria: ArchivedSearchCriteria = lastCriteria
    ) {
        lastUserId = userId
        lastSessionId = sessionId
        lastCriteria = criteria

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            // Display saved results immediately.
            val cached = cache.load(userId, criteria)
            if (cached.isNotEmpty()) {
                _alerts.value = cached
            }

            _isLoading.value = true
            _errorMessage.value = null

            try {
                repository.getArchivedAlerts(
                    userId = userId,
                    sessionId = sessionId,
                    criteria = criteria
                ).fold(
                    onSuccess = { results ->
                        _alerts.value = results
                        cache.save(userId, criteria, results)
                    },
                    onFailure = { error ->
                        _errorMessage.value =
                            error.message ?: "Unable to load archived alerts."
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        if (lastUserId.isNotBlank() && lastSessionId.isNotBlank()) {
            load(lastUserId, lastSessionId, lastCriteria)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
