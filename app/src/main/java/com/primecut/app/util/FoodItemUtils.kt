package com.primecut.app.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.primecut.app.data.model.FoodItem

fun loadFoodItemsFromAssets(context: Context, fileName: String = "food_items.json"): List<FoodItem> {
    val jsonString = context.assets.open(fileName)
        .bufferedReader()
        .use { it.readText() }
    val listType = object : TypeToken<List<FoodItem>>() {}.type
    return Gson().fromJson(jsonString, listType)
}
