package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.model.SubscriptionDto
import com.aquila.pocxpertalerts.data.repository.SubscriptionAlertDefinition
import com.aquila.pocxpertalerts.data.repository.SubscriptionApplication
import com.aquila.pocxpertalerts.data.repository.SubscriptionRepository
import com.aquila.pocxpertalerts.data.repository.SubscriptionSaveRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubscriptionsUiState(
    val subscriptions: List<SubscriptionDto> = emptyList(),
    val applications: List<SubscriptionApplication> = emptyList(),
    val alertDefinitions: List<SubscriptionAlertDefinition> = emptyList(),
    val selectedSubscription: SubscriptionDto? = null,

    val isLoading: Boolean = false,
    val isLoadingApplications: Boolean = false,
    val isLoadingDefinitions: Boolean = false,
    val isSaving: Boolean = false,

    val errorMessage: String? = null,
    val saveMessage: String? = null,
    val duplicateMessage: String? = null,

    val selectedApplicationId: String = "0",
    val selectedSearchName: String = "",
    val selectedSearchActive: Int = 2,
    val hasLoadedOnce: Boolean = false
)

class SubscriptionsViewModel(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionsUiState())

    val uiState: StateFlow<SubscriptionsUiState> =
        _uiState.asStateFlow()

    // ---------------------------------------------------------
    // LOAD SUBSCRIPTIONS
    // ---------------------------------------------------------

    fun loadSubscriptions(
        userId: String,
        sessionId: String,
        applicationId: String = "0",
        searchName: String = "",
        searchActive: Int = 2
    ) {
        if (userId.isBlank() || sessionId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "User or session information is missing."
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            selectedApplicationId = applicationId,
            selectedSearchName = searchName,
            selectedSearchActive = searchActive,
            hasLoadedOnce = true,
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            repository.getSubscriptions(
                userId = userId,
                sessionId = sessionId,
                applicationId = applicationId,
                searchName = searchName,
                searchActive = searchActive
            ).fold(
                onSuccess = { subscriptions ->
                    _uiState.value = _uiState.value.copy(
                        subscriptions = subscriptions,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                            ?: "Unable to load subscriptions."
                    )
                }
            )
        }
    }

    fun loadInitialSubscriptions(
        userId: String,
        sessionId: String
    ) {
        if (_uiState.value.hasLoadedOnce) return

        loadSubscriptions(
            userId = userId,
            sessionId = sessionId,
            applicationId = "0",
            searchName = "",
            searchActive = 2
        )
    }

    fun reloadCurrentSubscriptions(
        userId: String,
        sessionId: String
    ) {
        val state = _uiState.value

        loadSubscriptions(
            userId = userId,
            sessionId = sessionId,
            applicationId = state.selectedApplicationId,
            searchName = state.selectedSearchName,
            searchActive = state.selectedSearchActive
        )
    }

    // ---------------------------------------------------------
    // LOAD APPLICATIONS
    // ---------------------------------------------------------

    fun loadApplications(
        userId: String,
        userTypeId: String,
        sessionId: String
    ) {
        if (
            userId.isBlank() ||
            userTypeId.isBlank() ||
            sessionId.isBlank()
        ) {
            updateError("User or session information is missing.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingApplications = true,
                errorMessage = null
            )

            repository.getApplications(
                userId = userId,
                userTypeId = userTypeId,
                sessionId = sessionId
            ).fold(
                onSuccess = { applications ->
                    _uiState.value = _uiState.value.copy(
                        applications = applications,
                        isLoadingApplications = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingApplications = false,
                        errorMessage = error.message
                            ?: "Unable to load applications."
                    )
                }
            )
        }
    }

    // ---------------------------------------------------------
    // LOAD ALERT DEFINITIONS
    // ---------------------------------------------------------

    fun loadAlertDefinitions(
        applicationId: String,
        alertFlag: String,
        userId: String,
        sessionId: String
    ) {
        if (applicationId.isBlank() || applicationId == "0") {
            _uiState.value = _uiState.value.copy(
                alertDefinitions = emptyList(),
                isLoadingDefinitions = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingDefinitions = true,
                errorMessage = null
            )

            repository.getAlertDefinitions(
                applicationId = applicationId,
                alertFlag = alertFlag,
                userId = userId,
                sessionId = sessionId
            ).fold(
                onSuccess = { definitions ->
                    _uiState.value = _uiState.value.copy(
                        alertDefinitions = definitions,
                        isLoadingDefinitions = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingDefinitions = false,
                        errorMessage = error.message
                            ?: "Unable to load alert definitions."
                    )
                }
            )
        }
    }

    // ---------------------------------------------------------
    // LOAD SUBSCRIPTION DETAILS
    // ---------------------------------------------------------

    fun loadSubscriptionDetails(
        userSubId: String,
        sessionId: String
    ) {
        val id = userSubId.toLongOrNull()

        if (id == null || id <= 0L || sessionId.isBlank()) {
            updateError("Invalid subscription ID or session.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                selectedSubscription = null,
                errorMessage = null
            )

            repository.getSubscriptionDetails(
                userSubId = id,
                sessionId = sessionId
            ).fold(
                onSuccess = { subscription ->
                    _uiState.value = _uiState.value.copy(
                        selectedSubscription = subscription,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                            ?: "Unable to load subscription details."
                    )
                }
            )
        }
    }

    // ---------------------------------------------------------
    // CHECK DUPLICATE, THEN SAVE
    // ---------------------------------------------------------

    fun saveSubscription(request: SubscriptionSaveRequest) {
        val currentState = _uiState.value

        // Prevent repeated taps from starting concurrent save requests.
        if (currentState.isSaving) return

        if (request.userId.isBlank() || request.sessionId.isBlank()) {
            updateError("User or session information is missing.")
            return
        }

        if (
            request.applicationId.isBlank() ||
            request.alertDefId.isBlank() ||
            request.alertName.isBlank()
        ) {
            updateError(
                "Please select an application and alert definition " +
                        "and enter an alert name."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null,
                saveMessage = null,
                duplicateMessage = null
            )

            repository.checkDuplicate(
                userId = request.userId,
                applicationId = request.applicationId,
                alertDefinitionId = request.alertDefId,
                alertFlag = request.alertFlag,
                // "0" for create; existing ID for edit.
                userSubId = request.userSubId,
                sessionId = request.sessionId
            ).fold(
                onSuccess = { isDuplicate ->
                    if (isDuplicate) {
                        // Do not call saveSubscription when duplicate.
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            duplicateMessage = "Record already exists."
                        )
                    } else {
                        saveAfterDuplicateCheck(request)
                    }
                },
                onFailure = { error ->
                    // Fail closed: never save if duplicate validation fails.
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = error.message
                            ?: "Unable to check for duplicate subscriptions."
                    )
                }
            )
        }
    }

    private suspend fun saveAfterDuplicateCheck(
        request: SubscriptionSaveRequest
    ) {
        repository.saveSubscription(request).fold(
            onSuccess = {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveMessage = "Subscription saved successfully.",
                    errorMessage = null,
                    duplicateMessage = null
                )
            },
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = error.message
                        ?: "Unable to save subscription."
                )
            }
        )
    }

    // ---------------------------------------------------------
    // CLEAR MESSAGES
    // ---------------------------------------------------------

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            saveMessage = null,
            duplicateMessage = null
        )
    }

    private fun updateError(message: String) {
        _uiState.value = _uiState.value.copy(
            errorMessage = message
        )
    }
}