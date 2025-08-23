package com.primecut.app.data.dao

import androidx.room.*
import com.primecut.app.data.model.MealEntry

@Dao
interface MealEntryDao {

    @Query("SELECT * FROM meal_entries ORDER BY date DESC")
    suspend fun getAll(): List<MealEntry>

    @Query("SELECT * FROM meal_entries WHERE date = :date ORDER BY date DESC")
    suspend fun getByDate(date: String): List<MealEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(entry: MealEntry)

    @Update
    suspend fun update(entry: MealEntry)

    @Query("DELETE FROM meal_entries WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM meal_entries WHERE date >= :since")
    suspend fun getSince(since: String): List<MealEntry>

    @Query("SELECT * FROM meal_entries WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    suspend fun getByDateRange(start: String, end: String): List<MealEntry>
}
