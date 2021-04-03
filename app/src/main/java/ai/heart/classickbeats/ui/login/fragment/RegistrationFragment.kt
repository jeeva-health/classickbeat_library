package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentRegistrationBinding
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.*
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private val binding by viewBinding(FragmentRegistrationBinding::bind)

    private val logInViewModel by activityViewModels<LoginViewModel>()

    private lateinit var nameField: TextInputLayout

    private lateinit var registerButton: AppCompatButton

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        nameField = binding.nameLayout

        registerButton = binding.registerBtn

        registerButton.setSafeOnClickListener {
            val name = nameField.editText?.text?.toString()
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
        val action = RegistrationFragmentDirections.actionRegistrationFragmentToSelectionFragment()
        navController.navigate(action)
    }
}