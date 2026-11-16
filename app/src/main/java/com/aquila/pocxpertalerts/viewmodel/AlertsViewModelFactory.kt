package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aquila.pocxpertalerts.data.remote.ApiService
import com.aquila.pocxpertalerts.data.repository.AlertRepository

class AlertsViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            val repository = AlertRepository(apiService)
            return AlertsViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
