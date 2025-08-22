package com.primecut.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.primecut.app.data.dao.FoodItemDao
import com.primecut.app.data.model.FoodItem

@Database(entities = [FoodItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
}
