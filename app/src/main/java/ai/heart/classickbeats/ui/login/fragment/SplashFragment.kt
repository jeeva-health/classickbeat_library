package ai.heart.classickbeats.ui.login.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.postOnMainLooper
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var navController: NavController

    private val logInViewModel by activityViewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        lifecycleScope.launchWhenResumed {
            delay(2000)
            postOnMainLooper {
                navigateToNextScreen()
            }
        }
    }

    private fun navigateToNextScreen() {
        if (logInViewModel.isUserLoggedIn()) {
            navigateToSelectionFragment()
        } else {
            navigateToLoginFragment()
        }
    }

    private fun navigateToSelectionFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToSelectionFragment()
        navController.navigate(action)
    }

    private fun navigateToLoginFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToNavLogin()
        navController.navigate(action)
    }
}