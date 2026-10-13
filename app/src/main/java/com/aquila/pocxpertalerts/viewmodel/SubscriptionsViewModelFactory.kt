package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aquila.pocxpertalerts.data.repository.SubscriptionRepository
import com.aquila.pocxpertalerts.domain.usecase.GetSubscriptionsUseCase

class SubscriptionsViewModelFactory(
    private val repository: SubscriptionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscriptionsViewModel::class.java)) {
            return SubscriptionsViewModel(
                GetSubscriptionsUseCase(repository)
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}