package com.primecut.app.ui.component

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.primecut.app.R
import com.primecut.app.data.model.FoodItem
import com.primecut.app.databinding.FoodItemCardBinding

class FoodItemAdapter(private var items: List<FoodItem>) : RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(val binding: FoodItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = FoodItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = items[position]

        holder.binding.apply {
            // Set image
            Glide.with(foodImage.context)
                .load(food.pictureLink ?: R.drawable.ic_picture_placeholder)
                .into(foodImage)

            // Set title & info
            recipeTitle.text = food.recipeName
            servingsInfo.text = "${food.servings} servings (${food.measurementServings} x ${food.measurementType}) ${food.caloriesPerServing} cal"

            // Clear previous macro bars
            macroContainer.removeAllViews()

            // Prepare macro bars
            val macros = listOf(
                Macro("Protein", food.protein, android.R.color.holo_green_dark),
                Macro("Carbs", food.carbs, android.R.color.holo_orange_dark),
                Macro("Fats", food.fats, android.R.color.holo_red_dark),
                Macro("Fiber", food.fiber, android.R.color.holo_purple)
            )

            val max = macros.maxOf { it.value.coerceAtLeast(1f) }
            val containerHeightPx = 200 // height of bars in pixels

            macros.forEach { macro ->
                val barHeight = (containerHeightPx * (macro.value / max)).toInt().coerceAtLeast(10)
                val spacerHeight = containerHeightPx - barHeight

                val barLayout = LinearLayout(holder.itemView.context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    gravity = Gravity.BOTTOM
                }

                val bar = View(holder.itemView.context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        barHeight
                    )
                    setBackgroundColor(ContextCompat.getColor(holder.itemView.context, macro.colorRes))
                }

                val spacer = View(holder.itemView.context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        spacerHeight
                    )
                }

                val label = TextView(holder.itemView.context).apply {
                    text = "${macro.label}\n${macro.value.toInt()}g"
                    textSize = 12f
                    gravity = Gravity.CENTER
                }

                barLayout.addView(spacer)
                barLayout.addView(bar)
                barLayout.addView(label)

                macroContainer.addView(barLayout)
            }
        }
    }

    // Update adapter items (filtering or refresh)
    fun updateItems(newItems: List<FoodItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    private data class Macro(val label: String, val value: Float, val colorRes: Int)
}
