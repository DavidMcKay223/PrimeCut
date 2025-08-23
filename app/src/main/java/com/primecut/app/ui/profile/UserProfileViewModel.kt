package com.primecut.app.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.primecut.app.data.model.UserProfile
import com.primecut.app.data.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileViewModel(
    app: Application,
    private val repository: UserProfileRepository
) : AndroidViewModel(app) {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    fun loadProfile(userName: String) {
        viewModelScope.launch {
            val profile = withContext(Dispatchers.IO) { repository.getUserProfile(userName) }
            _userProfile.postValue(profile)
        }
    }

    fun saveProfile(profile: UserProfile, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveUserProfile(profile)
            withContext(Dispatchers.Main) { onComplete?.invoke() }
        }
    }

    fun recalcGoals(userName: String, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserGoals(userName)
            val updated = repository.getUserProfile(userName)
            withContext(Dispatchers.Main) {
                _userProfile.value = updated
                onComplete?.invoke()
            }
        }
    }
}
