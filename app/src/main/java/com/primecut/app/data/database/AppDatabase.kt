package com.primecut.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.primecut.app.data.dao.FoodItemDao
import com.primecut.app.data.model.FoodItem

@Database(
    entities = [FoodItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "primecut.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
