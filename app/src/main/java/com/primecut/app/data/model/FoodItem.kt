package com.primecut.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey val recipeName: String,
    val brandType: String,
    val groupName: String? = null,

    val servings: Float,
    val caloriesPerServing: Float,

    val measurementServings: Float,
    val measurementType: String,

    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val fiber: Float,

    val isBreakfast: Boolean = false,
    val isLunch: Boolean = false,
    val isDinner: Boolean = false,
    val isSnack: Boolean = false,

    val link: String? = null,
    val pictureLink: String? = null
) {
    val totalCalories: Float
        get() = servings * caloriesPerServing

    val isHighProtein: Boolean
        get() = protein >= 20f

    val isLowCarb: Boolean
        get() = carbs < 10f

    val isKeto: Boolean
        get() = fats > (carbs * 3f)

    val isBulkMeal: Boolean
        get() = totalCalories > 600f

    val isHighCarb: Boolean
        get() = carbs >= 25f && fiber < 3f

    val isLowFiber: Boolean
        get() = fiber < 2f

    val isHighFat: Boolean
        get() = fats >= 20f

    val isBalancedMeal: Boolean
        get() = protein >= 15f && carbs <= 60f && fats <= 20f

    val isLowProtein: Boolean
        get() = protein < 5f
}
