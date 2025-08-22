package com.primecut.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.primecut.app.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


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

        // Learning Here Delete Later
        val homeContainer = LinearLayout(requireContext())
        homeContainer.orientation = LinearLayout.VERTICAL
        homeContainer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        homeContainer.setPadding(16, 16, 16, 16)

        val nameInputLayout = TextInputLayout(requireContext())
        nameInputLayout.hint = "Name"

        val nameInput = TextInputEditText(requireContext())
        nameInputLayout.addView(nameInput)

        val ageInputLayout = TextInputLayout(requireContext())
        ageInputLayout.hint = "Age"

        val ageInput = TextInputEditText(requireContext())
        ageInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        ageInputLayout.addView(ageInput)

        homeContainer.addView(nameInputLayout) // TextInputLayout containing TextInputEditText
        homeContainer.addView(ageInputLayout) // TextInputLayout containing TextInputEditText for age

        (root as ViewGroup).addView(homeContainer)
        // End Learn

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}