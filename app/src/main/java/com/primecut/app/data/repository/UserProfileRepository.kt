package com.primecut.app.data.repository

import com.primecut.app.data.dao.UserProfileDao
import com.primecut.app.data.model.UserProfile

class UserProfileRepository(private val dao: UserProfileDao) {

    suspend fun getUserProfile(userName: String): UserProfile {
        return dao.getUserProfile(userName) ?: UserProfile(userName = userName)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val existing = dao.getUserProfile(profile.userName)
        if (existing == null) {
            dao.insertProfile(profile)
        } else {
            dao.updateProfile(profile)
        }
    }

    suspend fun updateUserGoals(userName: String) {
        val profile = getUserProfile(userName)
        profile.calculateGoals()
        saveUserProfile(profile)
    }
}
