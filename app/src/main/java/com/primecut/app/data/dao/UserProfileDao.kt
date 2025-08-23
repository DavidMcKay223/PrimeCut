package com.primecut.app.data.dao

import androidx.room.*
import com.primecut.app.data.model.UserProfile

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profiles WHERE userName = :userName LIMIT 1")
    suspend fun getUserProfile(userName: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)
}
