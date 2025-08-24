package com.primecut.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.datepicker.MaterialDatePicker
import com.primecut.app.data.database.AppDatabase
import com.primecut.app.data.model.WeightLog
import com.primecut.app.data.repository.WeightLogRepository
import com.primecut.app.databinding.FragmentWeightLogBinding
import java.time.Instant
import java.time.ZoneId

class WeightLogFragment : Fragment() {

    private var _binding: FragmentWeightLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WeightLogViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeightLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = AppDatabase.getInstance(requireContext()).weightLogDao()
        val repository = WeightLogRepository(dao)
        val factory = WeightLogViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WeightLogViewModel::class.java]

        viewModel.logs.observe(viewLifecycleOwner) { logs ->
            updateChartWithLogs(logs)
        }

        viewModel.loadUserLogs("DefaultUser", showProjection = true)

        binding.dateInput.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            picker.show(parentFragmentManager, "date_picker")
            picker.addOnPositiveButtonClickListener { selection ->
                val date = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                binding.dateInput.setText(date.toString())
            }
        }

        binding.addButton.setOnClickListener {
            val date = binding.dateInput.text.toString()
            val weight = binding.weightInput.text.toString().toFloatOrNull()
            if (date.isBlank() || weight == null) {
                Toast.makeText(requireContext(), "Enter valid date and weight", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addOrUpdateLog("DefaultUser", date, weight)
                binding.dateInput.text?.clear()
                binding.weightInput.text?.clear()
                Toast.makeText(requireContext(), "Weight added!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateChartWithLogs(logs: List<WeightLog>) {
        val chart = binding.weightChartView

        chart.setBackgroundColor(resources.getColor(com.primecut.app.R.color.midnightBlue, null))
        chart.axisLeft.textColor = resources.getColor(com.primecut.app.R.color.offWhite, null)
        chart.axisRight.isEnabled = false
        chart.xAxis.textColor = resources.getColor(com.primecut.app.R.color.offWhite, null)
        chart.xAxis.gridColor = resources.getColor(com.primecut.app.R.color.slateGray, null)
        chart.axisLeft.gridColor = resources.getColor(com.primecut.app.R.color.slateGray, null)
        chart.description.isEnabled = false
        chart.legend.textColor = resources.getColor(com.primecut.app.R.color.offWhite, null)

        if (logs.isEmpty()) {
            chart.clear()
            chart.invalidate()
            return
        }

        val sortedLogs = logs.sortedBy { it.date }

        val entries = sortedLogs.mapIndexed { index, log ->
            Entry(index.toFloat(), log.weightLbs.toFloat())
        }

        val dataSet = LineDataSet(entries, "Weight")
        dataSet.color = resources.getColor(com.primecut.app.R.color.vividBlue, null)
        dataSet.setCircleColor(resources.getColor(com.primecut.app.R.color.offWhite, null))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawValues(false)

        chart.data = LineData(dataSet)
        chart.invalidate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
