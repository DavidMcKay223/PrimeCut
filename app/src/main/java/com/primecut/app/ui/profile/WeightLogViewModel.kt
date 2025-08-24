package com.primecut.app.ui.profile

import androidx.lifecycle.*
import com.primecut.app.data.model.WeightLog
import com.primecut.app.data.repository.WeightLogRepository
import kotlinx.coroutines.launch

class WeightLogViewModel(private val repository: WeightLogRepository) : ViewModel() {

    private val _logs = MutableLiveData<List<WeightLog>>()
    val logs: LiveData<List<WeightLog>> get() = _logs

    fun loadUserLogs(userId: String, showProjection: Boolean = false) {
        viewModelScope.launch {
            val userLogs = repository.getUserLogs(userId, showProjection)
            _logs.postValue(userLogs)
        }
    }

    fun addOrUpdateLog(userId: String, date: String, weightLbs: Float) {
        viewModelScope.launch {
            repository.addOrUpdateLog(userId, date, weightLbs)
            // Refresh logs after adding/updating
            loadUserLogs(userId)
        }
    }
}
