package com.primecut.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.primecut.app.data.dao.FoodItemDao
import com.primecut.app.data.model.FoodItem
import com.primecut.app.data.dao.MealEntryDao
import com.primecut.app.data.dao.UserProfileDao
import com.primecut.app.data.model.MealEntry
import com.primecut.app.data.model.UserProfile

@Database(
    entities = [FoodItem::class, MealEntry::class, UserProfile::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun mealEntryDao(): MealEntryDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "primecut.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
