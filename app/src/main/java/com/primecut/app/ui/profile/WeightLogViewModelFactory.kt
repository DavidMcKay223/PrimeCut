package com.primecut.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.primecut.app.data.repository.WeightLogRepository

class WeightLogViewModelFactory(
    private val repository: WeightLogRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeightLogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeightLogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
