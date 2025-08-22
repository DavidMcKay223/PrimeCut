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
import com.google.android.material.card.MaterialCardView
import android.view.Gravity
import androidx.core.content.ContextCompat
import android.widget.ImageView
import com.bumptech.glide.Glide

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
        val galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
                    val card = MaterialCardView(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(16, 16, 16, 16)
                        }

                        addView(LinearLayout(context).apply {
                            orientation = LinearLayout.VERTICAL
                            setPadding(32, 16, 32, 16)

                            val imageView = ImageView(context).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    400
                                )
                                scaleType = ImageView.ScaleType.FIT_CENTER
                                adjustViewBounds = true
                            }

                            // Load URL or fallback using Glide
                            Glide.with(this)
                                .load(food.pictureLink ?: com.primecut.app.R.drawable.ic_picture_placeholder)
                                .into(imageView)

                            addView(imageView)

                            // Title: vivid blue + bold
                            addView(MaterialTextView(context, null).apply {
                                text = food.recipeName
                                textSize = 20f
                                setTypeface(typeface, android.graphics.Typeface.BOLD)
                                setTextColor(ContextCompat.getColor(context, com.primecut.app.R.color.vividBlue))
                            })

                            // Servings + Measurement + Calories
                            addView(MaterialTextView(context, null).apply {
                                text = "${food.servings} servings (${food.measurementServings} x ${food.measurementType}) ${food.caloriesPerServing} cal"
                            })

                            // Macro bar chart
                            addView(LinearLayout(context).apply {
                                orientation = LinearLayout.HORIZONTAL
                                weightSum = 4f
                                setPadding(0, 16, 0, 0)
                                minimumHeight = 200 // chart height baseline

                                val max = listOf(food.protein, food.carbs, food.fats, food.fiber).maxOrNull() ?: 1f

                                fun addBar(value: Float, label: String, colorRes: Int) {
                                    addView(LinearLayout(context).apply {
                                        orientation = LinearLayout.VERTICAL
                                        gravity = Gravity.BOTTOM
                                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)

                                        addView(View(context).apply {
                                            layoutParams = LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                (200 * (value / max)).toInt().coerceAtLeast(10) // proportional height
                                            ).apply { gravity = Gravity.BOTTOM }
                                            setBackgroundColor(ContextCompat.getColor(context, colorRes))
                                        })

                                        addView(MaterialTextView(context, null).apply {
                                            text = "$label\n${value.toInt()}g"
                                            textSize = 12f
                                            gravity = Gravity.CENTER
                                        })
                                    })
                                }

                                addBar(food.protein, "Protein", android.R.color.holo_green_dark)
                                addBar(food.carbs, "Carbs", android.R.color.holo_orange_dark)
                                addBar(food.fats, "Fats", android.R.color.holo_red_dark)
                                addBar(food.fiber, "Fiber", android.R.color.holo_purple)
                            })
                        })
                    }

                    galleryContainer.addView(card)
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
