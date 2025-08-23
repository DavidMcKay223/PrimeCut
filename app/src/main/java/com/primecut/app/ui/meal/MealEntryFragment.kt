package com.primecut.app.ui.meal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.primecut.app.data.model.FoodItem
import com.primecut.app.data.model.MealEntry
import com.primecut.app.databinding.FragmentMealEntryBinding
import com.primecut.app.ui.component.FoodItemViewModel
import com.primecut.app.ui.component.MealEntryAdapter
import com.primecut.app.ui.component.MealEntryViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class MealEntryFragment : Fragment() {

    private var _binding: FragmentMealEntryBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealEntryViewModel: MealEntryViewModel
    private lateinit var foodItemViewModel: FoodItemViewModel
    private lateinit var adapter: MealEntryAdapter

    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mealEntryViewModel = ViewModelProvider(requireActivity()).get(MealEntryViewModel::class.java)
        foodItemViewModel = ViewModelProvider(requireActivity()).get(FoodItemViewModel::class.java)

        setupUI()
        setupListeners()
        observeViewModels()
    }

    private fun setupUI() {
        val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
        val mealTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mealTypes)
        binding.mealTypeSpinner.setAdapter(mealTypeAdapter)

        binding.dateInput.setText(selectedDate)

        adapter = MealEntryAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.dateInput.setOnClickListener {
            showDatePicker()
        }

        binding.foodItemInput.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            setPortionFromFoodItem(selectedName)
        }

        binding.foodItemInput.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val typedName = binding.foodItemInput.text.toString()
                if (typedName.isNotBlank()) {
                    setPortionFromFoodItem(typedName)
                }
            }
        }

        binding.addMealButton.setOnClickListener {
            addMealEntry()
        }
    }

    private fun setPortionFromFoodItem(name: String) {
        val item = foodItemViewModel.foodItems.value?.find { it.recipeName == name }
        item?.let {
            binding.portionInput.setText(it.servings.toString())
        } ?: run {
            binding.portionInput.setText("1")
        }
    }

    private fun observeViewModels() {
        mealEntryViewModel.mealEntries.observe(viewLifecycleOwner) { entries ->
            updateMealEntriesList(entries)
        }

        foodItemViewModel.foodItems.observe(viewLifecycleOwner) { items ->
            val names = items.map { it.recipeName }
            val foodItemAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            binding.foodItemInput.setAdapter(foodItemAdapter)
        }
    }

    private fun updateMealEntriesList(entries: List<MealEntry>) {
        val filtered = entries.filter { it.date == selectedDate }
        binding.noDataText.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        adapter.updateItems(filtered)
    }

    private fun showDatePicker() {
        val initialLocalDate = LocalDate.parse(selectedDate)
        val initialSelectionMillis = initialLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setSelection(initialSelectionMillis)
            .build()

        picker.show(parentFragmentManager, "date_picker")

        picker.addOnPositiveButtonClickListener { utcMillis ->
            val newLocalDate = Instant.ofEpochMilli(utcMillis).atZone(ZoneId.of("UTC")).toLocalDate()

            selectedDate = newLocalDate.toString()
            binding.dateInput.setText(selectedDate)
            mealEntryViewModel.refreshMealEntries()
        }
    }

    private fun addMealEntry() {
        val mealType = binding.mealTypeSpinner.text.toString()
        val mealName = binding.foodItemInput.text.toString()
        val portion = binding.portionInput.text.toString().toFloatOrNull() ?: 1f

        if (mealType.isNullOrBlank() || mealName.isBlank()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val foodItem: FoodItem? = foodItemViewModel.foodItems.value?.find { it.recipeName == mealName }
        if (foodItem != null) {
            val entry = MealEntry(
                date = selectedDate,
                day = SimpleDateFormat("EEEE", Locale.US).format(SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(selectedDate)!!),
                mealType = mealType,
                mealName = foodItem.recipeName,
                groupName = foodItem.groupName,
                portionEaten = portion,
                measurementServings = foodItem.measurementServings,
                measurementType = foodItem.measurementType,
                calories = foodItem.caloriesPerServing * portion,
                protein = foodItem.protein * portion,
                carbs = foodItem.carbs * portion,
                fats = foodItem.fats * portion,
                fiber = foodItem.fiber * portion
            )
            mealEntryViewModel.addMealEntry(entry)
            binding.foodItemInput.text?.clear()
            binding.portionInput.text?.clear()
        } else {
            Toast.makeText(requireContext(), "Food item not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
