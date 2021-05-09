package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentPersonalDetailsBinding
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

        binding.continueBtn.setSafeOnClickListener {
            val name = binding.nameLayout.editText?.text?.toString()
            if (name.isNullOrBlank()) {
                showLongToast("Enter valid name")
            } else {
                logInViewModel.registerUser(name)
            }
        }

        logInViewModel.apiResponse.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                LoginViewModel.RequestType.LOGIN -> TODO()
                LoginViewModel.RequestType.REGISTER -> {
                    showShortToast("Successfully Registered")
                    navigateToSelectionFragment()
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

    private fun navigateToSelectionFragment() {
        val action = PersonalDetailsFragmentDirections.actionPersonalDetailsFragmentToNavHome()
        navController.navigate(action)
    }
}