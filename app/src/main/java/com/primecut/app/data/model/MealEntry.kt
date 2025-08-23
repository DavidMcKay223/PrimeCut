package com.primecut.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_entries")
data class MealEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val date: String,
    val day: String,
    val mealType: String,
    val mealName: String,
    val groupName: String? = null,

    val portionEaten: Float = 1f,
    val measurementServings: Float? = null,
    val measurementType: String? = null,

    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fats: Float = 0f,
    val fiber: Float = 0f
)