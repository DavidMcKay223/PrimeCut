package com.primecut.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.primecut.app.data.database.AppDatabase
import com.primecut.app.data.model.DietType
import com.primecut.app.data.model.Sex
import com.primecut.app.data.model.UserProfile
import com.primecut.app.data.repository.UserProfileRepository
import com.primecut.app.databinding.FragmentUserProfileBinding
import com.primecut.app.ui.profile.UserProfileViewModel

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application
        val repo = UserProfileRepository(AppDatabase.getInstance(app).userProfileDao())
        viewModel = ViewModelProvider(this, UserProfileViewModelFactory(app, repo))
            .get(UserProfileViewModel::class.java)

        setupUI()
        observeViewModel()
        loadProfile()
    }

    private fun setupUI() {
        val sexes = listOf("Male", "Female")
        val activityLevels = listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active", "Super Active")
        val goals = listOf("Maintain", "Lose0.5", "Lose1", "Lose2", "Gain0.5", "Gain1")
        val diets = DietType.values().map { it.name }

        binding.sexSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sexes))
        binding.activitySpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, activityLevels))
        binding.goalSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, goals))
        binding.dietSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, diets))

        binding.saveButton.setOnClickListener {
            saveProfile()
        }

        binding.recalcButton.setOnClickListener {
            viewModel.recalcGoals(binding.userNameInput.text.toString()) {
                Toast.makeText(requireContext(), "Goals recalculated!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            binding.userNameInput.setText(profile.userName)
            binding.ageInput.setText(profile.age.toString())
            binding.heightInput.setText(profile.heightInches.toString())
            binding.weightInput.setText(profile.weightPounds.toString())
            binding.sexSpinner.setText(profile.sex.name, false)
            binding.activitySpinner.setText(profile.activityLevel, false)
            binding.goalSpinner.setText(profile.goalType, false)
            binding.dietSpinner.setText(profile.dietType.name, false)

            binding.goalsTextView.text = """
                    Calories: ${profile.calorieGoal} cal
                    Protein: ${profile.proteinGoal} g
                    Carbs: ${profile.carbsGoal} g
                    Fat: ${profile.fatGoal} g
                    Fiber: ${profile.fiberGoal} g
                """.trimIndent()
        }
    }

    private fun loadProfile() {
        val defaultUser = "DefaultUser"
        viewModel.loadProfile(defaultUser)
    }

    private fun saveProfile() {
        val profile = UserProfile(
            userName = binding.userNameInput.text.toString(),
            age = binding.ageInput.text.toString().toFloatOrNull() ?: 25f,
            heightInches = binding.heightInput.text.toString().toFloatOrNull() ?: 70f,
            weightPounds = binding.weightInput.text.toString().toFloatOrNull() ?: 180f,
            sex = Sex.valueOf(binding.sexSpinner.text.toString()),
            activityLevel = binding.activitySpinner.text.toString(),
            goalType = binding.goalSpinner.text.toString(),
            dietType = DietType.valueOf(binding.dietSpinner.text.toString())
        )

        viewModel.saveProfile(profile) {
            Toast.makeText(requireContext(), "Profile saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
