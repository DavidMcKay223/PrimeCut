package com.primecut.app.ui.meal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.primecut.app.databinding.FragmentMealEntryBinding
import com.primecut.app.ui.component.MealEntryAdapter
import com.primecut.app.ui.component.MealEntryViewModel

class MealEntryFragment : Fragment() {

    private var _binding: FragmentMealEntryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MealEntryViewModel
    private lateinit var adapter: MealEntryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealEntryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup RecyclerView
        adapter = MealEntryAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Setup ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(MealEntryViewModel::class.java)
        viewModel.mealEntries.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                binding.noDataText.visibility = View.VISIBLE
            } else {
                binding.noDataText.visibility = View.GONE
                adapter.updateItems(items)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
