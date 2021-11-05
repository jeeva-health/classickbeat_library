package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentSignUpModeSelectionBinding
import ai.heart.classickbeats.utils.setDarkStatusBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController


class SignUpModeSelectionFragment : Fragment(R.layout.fragment_sign_up_mode_selection) {

    private val binding by viewBinding(FragmentSignUpModeSelectionBinding::bind)

    private lateinit var navController: NavController

    private lateinit var googleModeButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        googleModeButton = binding.googleSignIn

        setDarkStatusBar()

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
}
