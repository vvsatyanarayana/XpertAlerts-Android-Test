
package com.aquila.pocxpertalerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aquila.pocxpertalerts.data.repository.ArchivedAlertsCache
import com.aquila.pocxpertalerts.data.repository.ArchivedAlertsRepository

class ArchivedAlertsViewModelFactory(
    private val repository: ArchivedAlertsRepository,
    private val cache: ArchivedAlertsCache
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArchivedAlertsViewModel::class.java)) {
            return ArchivedAlertsViewModel(
                repository = repository,
                cache = cache
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
