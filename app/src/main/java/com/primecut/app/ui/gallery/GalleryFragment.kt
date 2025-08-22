package com.primecut.app.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.primecut.app.databinding.FragmentGalleryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.textview.MaterialTextView
import com.primecut.app.data.database.AppDatabase
import androidx.lifecycle.lifecycleScope
import com.primecut.app.data.model.FoodItem
import android.widget.LinearLayout

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // This whole block is a coroutine launched by lifecycleScope.launch
        lifecycleScope.launch {
            // Correctly using withContext inside the coroutine
            val foodItems: List<FoodItem> = withContext(Dispatchers.IO) {
                // This code runs on the I/O thread
                AppDatabase.getInstance(requireContext())
                    .foodItemDao()
                    .getAll()
            }

            // Get a reference to the container view
            val galleryContainer = binding.galleryContainer as? LinearLayout ?: return@launch

            if (foodItems.isEmpty()) {
                val noDataTextView = MaterialTextView(requireContext()).apply {
                    text = "No food items found."
                    setPadding(32, 16, 32, 16)
                }
                galleryContainer.addView(noDataTextView)
            } else {
                foodItems.forEach { food ->
                    val tv = MaterialTextView(
                        requireContext(),
                        null
                    ).apply {
                        text = "${food.recipeName} - ${food.caloriesPerServing} cal"
                        setPadding(32, 16, 32, 16)
                        setTextColor(resources.getColor(com.primecut.app.R.color.offWhite, null))
                    }
                    galleryContainer.addView(tv)
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
