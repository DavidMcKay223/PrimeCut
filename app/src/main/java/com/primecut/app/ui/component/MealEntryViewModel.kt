package com.primecut.app.ui.component

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.primecut.app.data.database.AppDatabase
import com.primecut.app.data.model.MealEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MealEntryViewModel(app: Application) : AndroidViewModel(app) {

    private val _mealEntries = MutableLiveData<List<MealEntry>>()
    val mealEntries: LiveData<List<MealEntry>> = _mealEntries

    private val db = AppDatabase.getInstance(app)

    init {
        refreshMealEntries()
    }

    fun refreshMealEntries(date: String? = null) {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                date?.let {
                    db.mealEntryDao().getByDate(it)
                } ?: db.mealEntryDao().getAll()
            }
            _mealEntries.postValue(items)
        }
    }

    fun addMealEntry(entry: MealEntry, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            db.mealEntryDao().add(entry)
            refreshMealEntries(entry.date)
            onComplete?.let {
                withContext(Dispatchers.Main) { it() }
            }
        }
    }
}
