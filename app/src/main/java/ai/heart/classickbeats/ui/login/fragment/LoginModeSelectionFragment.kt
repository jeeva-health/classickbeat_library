package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentLoginModeSelectionBinding
import ai.heart.classickbeats.utils.StringUtils
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

class LoginModeSelectionFragment : Fragment(R.layout.fragment_login_mode_selection) {

    private val binding by viewBinding(FragmentLoginModeSelectionBinding::bind)

    private lateinit var navController: NavController

    private lateinit var phoneModeButton: Button

    private lateinit var googleModeButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        phoneModeButton = binding.phoneSignIn
        googleModeButton = binding.googleSignIn

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireActivity(), R.color.very_dark_blue)

        StringUtils.setTextViewHTML(
            binding.signUp,
            "Need to create an account? <a href=\"\">SIGN UP</a>"
        ) {
            navigateToSignUpModelSelectionFragment()
        }

        phoneModeButton.setSafeOnClickListener {
            navigateToPhoneSignUpFragment()
        }

        googleModeButton.setSafeOnClickListener {

        }
    }

    private fun navigateToPhoneSignUpFragment() {
        val action =
            LoginModeSelectionFragmentDirections.actionLoginModeSelectionFragmentToPhoneSignUpFragment()
        navController.navigate(action)
    }

    private fun navigateToSignUpModelSelectionFragment() {
        val action =
            LoginModeSelectionFragmentDirections.actionLoginModeSelectionFragmentToSignUpModeSelectionFragment()
        navController.navigate(action)
    }
}