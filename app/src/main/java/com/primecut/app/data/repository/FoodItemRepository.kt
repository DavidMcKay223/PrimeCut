package com.primecut.app.data.repository

import com.primecut.app.data.dao.FoodItemDao
import com.primecut.app.data.model.FoodItem

class FoodItemRepository(private val dao: FoodItemDao) {

    suspend fun getFoodItemByName(name: String): FoodItem? =
        dao.getFoodItemByName(name)

    suspend fun getAll(): List<FoodItem> =
        dao.getAll()
}
