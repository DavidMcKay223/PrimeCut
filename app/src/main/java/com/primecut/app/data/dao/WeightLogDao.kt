package com.primecut.app.data.dao

import androidx.room.*
import com.primecut.app.data.model.WeightLog

@Dao
interface WeightLogDao {

    @Query("SELECT * FROM weight_logs WHERE userId = :userId ORDER BY date ASC")
    suspend fun getUserLogs(userId: String): List<WeightLog>

    @Query("SELECT * FROM weight_logs WHERE userId = :userId AND date >= :startDate ORDER BY date ASC")
    suspend fun getRecentLogs(userId: String, startDate: String): List<WeightLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WeightLog)

    @Update
    suspend fun update(log: WeightLog)

    @Transaction
    suspend fun addOrUpdate(userId: String, date: String, weightLbs: Float) {
        val existing = getUserLogs(userId).firstOrNull { it.date == date }
        if (existing == null) {
            insert(WeightLog(userId = userId, date = date, weightLbs = weightLbs))
        } else {
            update(existing.copy(weightLbs = weightLbs))
        }
    }
}
