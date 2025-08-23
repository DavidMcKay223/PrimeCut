package com.primecut.app.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.primecut.app.data.database.AppDatabase
import com.primecut.app.data.model.FoodItem
import com.primecut.app.databinding.FragmentSettingBinding
import com.primecut.app.util.loadFoodItemsFromAssets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.app.Dialog
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.primecut.app.ui.component.FoodItemViewModel
import androidx.lifecycle.ViewModelProvider

class SettingFragment : DialogFragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FoodItemViewModel

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProvider(requireActivity()).get(FoodItemViewModel::class.java)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sync Settings")
            .setMessage("Update the database to latest files?")
            .setPositiveButton("Sync", null)
            .setNegativeButton("Close") { _, _ ->
                dismiss()
            }
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                lifecycleScope.launch {
                    val items = loadFoodItemsFromAssets(requireContext())
                    viewModel.syncFoodItemsFromAssets(items) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Sync Complete", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }
                }
            }
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
