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

        val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
        val mealTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mealTypes)
        binding.mealTypeSpinner.setAdapter(mealTypeAdapter)

        binding.dateInput.setText(selectedDate)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mealEntryViewModel = ViewModelProvider(requireActivity()).get(MealEntryViewModel::class.java)
        foodItemViewModel = ViewModelProvider(requireActivity()).get(FoodItemViewModel::class.java)

        adapter = MealEntryAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        mealEntryViewModel.mealEntries.observe(viewLifecycleOwner) { entries ->
            val filtered = entries.filter { it.date == selectedDate }
            binding.noDataText.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
            adapter.updateItems(filtered)
        }

        foodItemViewModel.foodItems.observe(viewLifecycleOwner) { items ->
            val names = items.map { it.recipeName }
            binding.foodItemInput.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names))
        }

        binding.dateInput.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            picker.show(parentFragmentManager, "date_picker")

            picker.addOnPositiveButtonClickListener { utcMillis ->
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                selectedDate = sdf.format(java.util.Date(utcMillis))
                binding.dateInput.setText(selectedDate)
                mealEntryViewModel.refreshMealEntries()
            }
        }

        binding.foodItemInput.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val item = foodItemViewModel.foodItems.value?.find { it.recipeName == selectedName }
            if (item != null) binding.portionInput.setText("1")
        }

        binding.addMealButton.setOnClickListener {
            val mealType = binding.mealTypeSpinner.selectedItem.toString()
            val mealName = binding.foodItemInput.text.toString()
            val portion = binding.portionInput.text.toString().toFloatOrNull() ?: 1f

            if (mealType.isBlank() || mealName.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
