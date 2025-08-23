package com.primecut.app.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.primecut.app.databinding.FragmentGalleryBinding
import com.primecut.app.ui.component.FoodItemAdapter
import com.primecut.app.ui.component.FoodItemViewModel
import androidx.recyclerview.widget.LinearLayoutManager

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    private lateinit var viewModel: FoodItemViewModel
    private lateinit var adapter: FoodItemAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup RecyclerView
        adapter = FoodItemAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Setup ViewModel
        viewModel = ViewModelProvider(this).get(FoodItemViewModel::class.java)
        viewModel.foodItems.observe(viewLifecycleOwner) { items ->
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
