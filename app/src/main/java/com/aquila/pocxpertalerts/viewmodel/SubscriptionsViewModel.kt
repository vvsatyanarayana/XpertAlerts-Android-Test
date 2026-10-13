
package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquila.pocxpertalerts.data.model.SubscriptionDto
import com.aquila.pocxpertalerts.domain.usecase.GetSubscriptionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubscriptionsUiState(
    val subscriptions: List<SubscriptionDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SubscriptionsViewModel(
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionsUiState())
    val uiState: StateFlow<SubscriptionsUiState> = _uiState.asStateFlow()

    fun loadSubscriptions(
        userId: String,
        sessionId: String,
        applicationId: String = "0",
        searchName: String = "",
        searchActive: Int = 1
    ) {
        if (userId.isBlank() || sessionId.isBlank()) {
            _uiState.value = SubscriptionsUiState(
                errorMessage = "User or session information is missing."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            getSubscriptionsUseCase(
                userId = userId,
                sessionId = sessionId,
                applicationId = applicationId,
                searchName = searchName,
                searchActive = searchActive
            ).fold(
                onSuccess = { subscriptions ->
                    _uiState.value = SubscriptionsUiState(
                        subscriptions = subscriptions,
                        isLoading = false
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
