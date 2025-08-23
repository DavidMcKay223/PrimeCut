package com.primecut.app.ui.component

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.primecut.app.data.database.AppDatabase
import com.primecut.app.data.model.FoodItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FoodItemViewModel(app: Application) : AndroidViewModel(app) {
    val foodItems: LiveData<List<FoodItem>> = liveData {
        val items = withContext(Dispatchers.IO) {
            AppDatabase.getInstance(app).foodItemDao().getAll()
        }
        emit(items)
    }
}
