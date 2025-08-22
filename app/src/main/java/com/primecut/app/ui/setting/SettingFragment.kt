package com.primecut.app.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.primecut.app.data.database.AppDatabase
import com.primecut.app.data.model.FoodItem
import com.primecut.app.databinding.FragmentSettingBinding
import com.primecut.app.util.loadFoodItemsFromAssets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import android.util.Log

class SettingFragment : DialogFragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Room database
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "primecut_db"
        ).build()

        // Button click: load JSON from assets & insert into DB
        binding.btnSyncData.setOnClickListener {
            lifecycleScope.launch {
                syncFoodItems()
            }
        }
    }

    private suspend fun syncFoodItems() {
        withContext(Dispatchers.IO) {
            val items: List<FoodItem> = loadFoodItemsFromAssets(requireContext())
            items.forEach { db.foodItemDao().insert(it) }
        }
        Toast.makeText(requireContext(), "Database synced!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
