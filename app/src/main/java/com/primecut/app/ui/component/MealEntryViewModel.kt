package com.primecut.app.ui.component

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.primecut.app.data.database.AppDatabase
import com.primecut.app.data.model.MealEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MealEntryViewModel(app: Application) : AndroidViewModel(app) {

    val mealEntries: LiveData<List<MealEntry>> = liveData {
        val items = withContext(Dispatchers.IO) {
            AppDatabase.getInstance(app).mealEntryDao().getAll()
        }
        emit(items)
    }
}
