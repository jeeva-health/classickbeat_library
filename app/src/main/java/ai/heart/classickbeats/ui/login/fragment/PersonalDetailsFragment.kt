package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentPersonalDetailsBinding
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.shared.formattedinput.MaskedEditText
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.common.DateTimePickerViewModel
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.*
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

class PersonalDetailsFragment : Fragment(R.layout.fragment_personal_details) {

    private val binding by viewBinding(FragmentPersonalDetailsBinding::bind)

    private val logInViewModel by activityViewModels<LoginViewModel>()

    private val dateTimePickerViewModel: DateTimePickerViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setLightStatusBar()

        logInViewModel.currentUser?.let {
            binding.nameLayout.editText?.setText(it.fullName)
        }

        binding.genderLayout.editText?.setOnClickListener {
            hideKeyboard(it)
            openGenderSelectionDialog()
        }

        binding.dobLayout.setEndIconOnClickListener {
            openDatePickerDialog()
        }

        binding.continueBtn.setSafeOnClickListener {
            val name = binding.nameLayout.editText?.text?.toString() ?: ""
            val gender = logInViewModel.selectedGender.value
            val weight = binding.weightLayout.editText?.text?.toString()?.toDoubleOrNull()
            val isHeightFeetValid = binding.heightFeetLayout.editText?.text?.isNotBlank() ?: false
            val isHeightInchesValid =
                binding.heightInchesLayout.editText?.text?.isNotBlank() ?: false
            val isDobValid = (binding.dobLayout.editText as MaskedEditText?)?.isValid() ?: false

            var isError = false
            if (name.isBlank()) {
                binding.nameLayout.error = "Enter valid name"
                isError = true
            }

            if (gender == null) {
                binding.genderLayout.error = "Gender not selected"
                isError = true
            }

            if (weight == null) {
                binding.weightLayout.error = "Enter valid weight"
                isError = true
            }

            if (!isHeightFeetValid) {
                binding.heightFeetLayout.error = "Enter valid height"
                isError = true
            }

            if (!isHeightInchesValid) {
                binding.heightInchesLayout.error = "Enter valid height"
                isError = true
            }

            if (!isDobValid) {
                binding.dobLayout.error = "Enter valid dob"
                isError = true
            }

            if (isError) {
                showSnackbar("Invalid details", false)
            } else {
                val heightFeet = binding.heightFeetLayout.editText?.text?.toString()?.toInt() ?: 0
                val heightInches =
                    binding.heightInchesLayout.editText?.text?.toString()?.toInt() ?: 0
                val height = heightFeet * 12 + heightInches
                val dob = (binding.dobLayout.editText as MaskedEditText?)?.getParsedText()

                val user = User(
                    fullName = name,
                    gender = gender!!,
                    weight = weight!!,
                    height = height.toDouble(),
                    dob = dob!!
                )
                logInViewModel.registerUser(user)
            }
        }

        logInViewModel.selectedGender.observe(viewLifecycleOwner, Observer {
            binding.genderLayout.editText?.setText(it.displayStr)
        })

        logInViewModel.apiResponse.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                LoginViewModel.RequestType.LOGIN -> TODO()
                LoginViewModel.RequestType.REGISTER -> {
                    showSnackbar("Successfully Registered")
                    navigateToNavHome()
                }
            }
        })

        dateTimePickerViewModel.selectedLogDate.observe(viewLifecycleOwner, EventObserver {
            binding.dobLayout.editText?.setText(it.toString())
        })

        logInViewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoadingBar()
            } else {
                hideLoadingBar()
            }
        }
    }

    private fun navigateToNavHome() {
        val action = PersonalDetailsFragmentDirections.actionPersonalDetailsFragmentToNavHome()
        navController.navigate(action)
    }

    private fun openGenderSelectionDialog() {
        val action =
            PersonalDetailsFragmentDirections.actionPersonalDetailsFragmentToGenderSelectionBottomSheetFragment()
        navController.navigate(action)
    }

    private fun openDatePickerDialog() {
        val action = PersonalDetailsFragmentDirections.actionPersonalDetailsFragmentToDatePickerFragment2()
        navController.navigate(action)
    }
}
