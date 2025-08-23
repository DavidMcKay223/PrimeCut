package com.primecut.app.ui.component

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.primecut.app.data.database.AppDatabase
import com.primecut.app.data.model.FoodItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FoodItemViewModel(app: Application) : AndroidViewModel(app) {
    private val _foodItems = MutableLiveData<List<FoodItem>>()
    val foodItems: LiveData<List<FoodItem>> = _foodItems

    private val db = AppDatabase.getInstance(app)

    init {
        refreshFoodItems()
    }

    fun refreshFoodItems() {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                db.foodItemDao().getAll()
            }
            _foodItems.postValue(items)
        }
    }

    fun syncFoodItemsFromAssets(newItems: List<FoodItem>, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            newItems.forEach { db.foodItemDao().insert(it) }
            refreshFoodItems() // update LiveData
            onComplete?.let {
                withContext(Dispatchers.Main) {
                    it()
                }
            }
        }
    }
}
