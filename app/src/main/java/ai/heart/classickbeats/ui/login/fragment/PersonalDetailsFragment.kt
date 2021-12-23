package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentPersonalDetailsBinding
import ai.heart.classickbeats.model.User
import ai.heart.classickbeats.shared.formattedinput.MaskedEditText
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.*
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController


class PersonalDetailsFragment : Fragment(R.layout.fragment_personal_details) {

    private val binding by viewBinding(FragmentPersonalDetailsBinding::bind)

    private val logInViewModel by activityViewModels<LoginViewModel>()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setLightStatusBar()

        binding.genderLayout.editText?.setOnClickListener {
            hideKeyboard(it)
            openGenderSelectionDialog()
        }

        binding.continueBtn.setSafeOnClickListener {
            val name = binding.nameLayout.editText?.text?.toString() ?: ""
            val gender = logInViewModel.selectedGender
            val weight = binding.weightLayout.editText?.text?.toString()?.toDoubleOrNull()
            val isHeightValid =
                (binding.heightLayout.editText as MaskedEditText?)?.isValid() ?: false
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

            if (!isHeightValid) {
                binding.heightLayout.error = "Enter valid height"
                isError = true
            }

            if (!isDobValid) {
                binding.dobLayout.error = "Enter valid dob"
                isError = true
            }

            if (isError) {
                showLongToast("Invalid details")
            } else {
                val height =
                    (binding.heightLayout.editText as MaskedEditText?)?.getParsedText()
                        ?.toDoubleOrNull()
                val dob = (binding.dobLayout.editText as MaskedEditText?)?.getParsedText()

                val user = User(
                    fullName = name,
                    gender = gender!!,
                    weight = weight!!,
                    height = height!!,
                    dob = dob!!
                )
                logInViewModel.registerUser(user)
            }
        }

        logInViewModel.apiResponse.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                LoginViewModel.RequestType.LOGIN -> TODO()
                LoginViewModel.RequestType.REGISTER -> {
                    showShortToast("Successfully Registered")
                    navigateToNavHome()
                }
            }
        })

        logInViewModel.showLoading.observe(viewLifecycleOwner, {
            if (it) {
                showLoadingBar()
            } else {
                hideLoadingBar()
            }
        })
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
}
