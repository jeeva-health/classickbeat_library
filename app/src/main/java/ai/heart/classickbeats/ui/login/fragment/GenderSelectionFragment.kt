package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentGenderSelectionBinding
import ai.heart.classickbeats.model.Gender
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenderSelectionFragment : Fragment(R.layout.fragment_gender_selection) {

    private val binding by viewBinding(FragmentGenderSelectionBinding::bind)

    private val logInViewModel by activityViewModels<LoginViewModel>()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.genderMaleCard.setSafeOnClickListener {
            logInViewModel.selectedGender.value = Gender.MALE
            navigateToPersonalInfoFragment()
        }

        binding.genderFemaleCard.setSafeOnClickListener {
            logInViewModel.selectedGender.value = Gender.FEMALE
            navigateToPersonalInfoFragment()
        }

        binding.genderOtherCard.setSafeOnClickListener {
            logInViewModel.selectedGender.value = Gender.OTHERS
            navigateToPersonalInfoFragment()
        }
    }

    private fun navigateToPersonalInfoFragment() {
        val action =
            GenderSelectionFragmentDirections.actionGenderSelectionFragmentToPersonalDetailsFragment()
        navController.navigate(action)
    }
}
