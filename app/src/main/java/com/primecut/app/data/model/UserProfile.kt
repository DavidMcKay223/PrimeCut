package com.primecut.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val userName: String,
    var age: Float = 25f,
    var sex: Sex = Sex.Male,
    var heightInches: Float = 70f,
    var weightPounds: Float = 170f,
    var activityLevel: String = "sedentary",
    var goalType: String = "maintain",
    var dietType: DietType = DietType.None,

    // Calculated Goals
    var calorieGoal: Float = 0f,
    var proteinGoal: Float = 0f,
    var carbsGoal: Float = 0f,
    var fatGoal: Float = 0f,
    var fiberGoal: Float = 0f
) {
    fun calculateGoals() {
        val bmr = calculateBMR()
        val tdee = bmr * getActivityFactor()
        val adjustedCalories = applyGoalAdjustment(tdee)

        calorieGoal = adjustedCalories

        val macroSplit = MacroSplit.fromDiet(dietType)

        proteinGoal = (calorieGoal * macroSplit.proteinRatio) / 4f
        fatGoal = (calorieGoal * macroSplit.fatRatio) / 9f
        carbsGoal = (calorieGoal * macroSplit.carbRatio) / 4f
        fiberGoal = (calorieGoal / 1000f) * macroSplit.fiberPer1000Calories
    }

    private fun calculateBMR(): Float {
        val weightKg = weightPounds * 0.453592f
        val heightCm = heightInches * 2.54f

        return if (sex == Sex.Male)
            10f * weightKg + 6.25f * heightCm - 5f * age + 5f
        else
            10f * weightKg + 6.25f * heightCm - 5f * age - 161f
    }

    private fun getActivityFactor(): Float = when (activityLevel.lowercase()) {
        "sedentary" -> 1.2f
        "lightly active" -> 1.375f
        "moderately active" -> 1.55f
        "very active" -> 1.725f
        "super active" -> 1.9f
        else -> 1.2f
    }

    private fun applyGoalAdjustment(tdee: Float): Float = when (goalType.lowercase()) {
        "maintain" -> tdee
        "lose0.5" -> tdee - 250f
        "lose1" -> tdee - 500f
        "lose2" -> tdee - 1000f
        "gain0.5" -> tdee + 250f
        "gain1" -> tdee + 500f
        else -> tdee
    }
}
