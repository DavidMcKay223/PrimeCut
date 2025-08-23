package com.primecut.app.ui.component

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.primecut.app.R
import com.primecut.app.data.model.MealEntry
import com.primecut.app.databinding.MealEntryCardBinding

class MealEntryAdapter(private var items: List<MealEntry>) :
    RecyclerView.Adapter<MealEntryAdapter.MealViewHolder>() {

    inner class MealViewHolder(val binding: MealEntryCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = MealEntryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = items[position]

        holder.binding.apply {
            mealTitle.text = meal.mealName
            mealInfo.text =
                "${meal.portionEaten} x ${meal.measurementType ?: "portion"} — ${meal.calories} cal"

            macroContainer.removeAllViews()

            val macros = listOf(
                Macro("Protein", meal.protein, android.R.color.holo_green_dark),
                Macro("Carbs", meal.carbs, android.R.color.holo_orange_dark),
                Macro("Fats", meal.fats, android.R.color.holo_red_dark),
                Macro("Fiber", meal.fiber, android.R.color.holo_purple)
            )

            val max = macros.maxOf { it.value.coerceAtLeast(1f) }
            val containerHeightPx = 200

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

    fun updateItems(newItems: List<MealEntry>) {
        items = newItems
        notifyDataSetChanged()
    }

    private data class Macro(val label: String, val value: Float, val colorRes: Int)
}
