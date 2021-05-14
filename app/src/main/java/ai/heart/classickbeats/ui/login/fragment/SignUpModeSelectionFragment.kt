package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentSignUpModeSelectionBinding
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


class SignUpModeSelectionFragment : Fragment(R.layout.fragment_sign_up_mode_selection) {

    private val binding by viewBinding(FragmentSignUpModeSelectionBinding::bind)

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
            binding.signIn,
            "Already have an account? <a href=\"\">SIGN IN</a>"
        ) {
            navigateToLoginModeSelectionFragment()
        }

        phoneModeButton.setSafeOnClickListener {
            navigateToPhoneSignUpFragment()
        }

        googleModeButton.setSafeOnClickListener {
            navigateGoogleSignUpFragment()
        }
    }

    private fun navigateToPhoneSignUpFragment() {
        val action =
            SignUpModeSelectionFragmentDirections.actionSignUpModeSelectionFragmentToPhoneSignUpFragment()
        navController.navigate(action)
    }

    private fun navigateGoogleSignUpFragment() {
        val action =
            SignUpModeSelectionFragmentDirections.actionSignUpModeSelectionFragmentToGoogleSignUpFragment()
        navController.navigate(action)
    }

    private fun navigateToLoginModeSelectionFragment() {
        val action =
            SignUpModeSelectionFragmentDirections.actionSignUpModeSelectionFragmentToLoginModeSelectionFragment()
        navController.navigate(action)
    }
}
