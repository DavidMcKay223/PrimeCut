package com.primecut.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.primecut.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Learning Here Delete Later
        val homeContainer = LinearLayout(requireContext())
        homeContainer.orientation = LinearLayout.VERTICAL
        homeContainer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        homeContainer.setPadding(16, 16, 16, 16)

        val nameLabel = TextView(requireContext())
        nameLabel.text = "Name"
        val nameInput = EditText(requireContext())

        val ageLabel = TextView(requireContext())
        ageLabel.text = "Age"
        val ageInput = EditText(requireContext())
        ageInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        homeContainer.addView(nameLabel)
        homeContainer.addView(nameInput)
        homeContainer.addView(ageLabel)
        homeContainer.addView(ageInput)

        (root as ViewGroup).addView(homeContainer)
        // End Learn

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}