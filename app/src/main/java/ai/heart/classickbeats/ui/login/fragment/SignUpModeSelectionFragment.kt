package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentSignUpModeSelectionBinding
import ai.heart.classickbeats.utils.StringUtils
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController


class SignUpModeSelectionFragment : Fragment(R.layout.fragment_sign_up_mode_selection) {

    private val binding by viewBinding(FragmentSignUpModeSelectionBinding::bind)

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireActivity(), R.color.very_dark_blue)

        StringUtils.setTextViewHTML(
            binding.signIn,
            "Already have an account? <a href=\"\">SIGN IN</a>"
        ) {
            navigateToLoginModeSelectionFragment()
        }
    }

    private fun navigateToLoginModeSelectionFragment() {
        val action =
            SignUpModeSelectionFragmentDirections.actionSignUpModeSelectionFragmentToLoginModeSelectionFragment()
        navController.navigate(action)
    }
}