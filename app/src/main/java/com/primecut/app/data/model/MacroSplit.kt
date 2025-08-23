package com.primecut.app.data.model

data class MacroSplit(
    val proteinRatio: Float,
    val carbRatio: Float,
    val fatRatio: Float,
    val fiberPer1000Calories: Float
) {
    companion object {
        fun fromDiet(dietType: DietType): MacroSplit = when (dietType) {
            DietType.Balanced -> MacroSplit(0.30f, 0.40f, 0.30f, 14f)
            DietType.Keto -> MacroSplit(0.20f, 0.05f, 0.75f, 14f)
            DietType.Paleo -> MacroSplit(0.30f, 0.30f, 0.40f, 14f)
            DietType.Whole30 -> MacroSplit(0.35f, 0.30f, 0.35f, 14f)
            DietType.Vegan -> MacroSplit(0.25f, 0.50f, 0.25f, 20f)
            DietType.Vegetarian -> MacroSplit(0.25f, 0.50f, 0.25f, 18f)
            DietType.Mediterranean -> MacroSplit(0.20f, 0.50f, 0.30f, 16f)
            DietType.HighCarb -> MacroSplit(0.20f, 0.60f, 0.20f, 14f)
            DietType.LowCarb -> MacroSplit(0.30f, 0.20f, 0.50f, 14f)
            DietType.BodybuilderBulk -> MacroSplit(0.35f, 0.45f, 0.20f, 14f)
            DietType.Cutting -> MacroSplit(0.40f, 0.30f, 0.30f, 14f)
            DietType.Recomp -> MacroSplit(0.35f, 0.35f, 0.30f, 14f)
            DietType.IntermittentFasting -> MacroSplit(0.30f, 0.40f, 0.30f, 14f)
            DietType.None -> MacroSplit(0.25f, 0.50f, 0.25f, 14f)
        }
    }
}
