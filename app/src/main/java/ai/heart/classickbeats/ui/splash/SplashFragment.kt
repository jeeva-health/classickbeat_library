package ai.heart.classickbeats.ui.splash

import ai.heart.classickbeats.R
import ai.heart.classickbeats.shared.result.EventObserver
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var navController: NavController

    private val launchViewModel: LaunchViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        launchViewModel.launchDestination.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                LaunchDestination.ONBOARDING -> navigateToOnBoardingFragment()
                LaunchDestination.SIGNUP -> navigateToLoginFragment()
                LaunchDestination.HOME_SCREEN -> navigateToScanFragment()
            }
        })
    }

    private fun navigateToScanFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToNavHome()
        navController.navigate(action)
    }

    private fun navigateToLoginFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToNavLogin()
        navController.navigate(action)
    }

    private fun navigateToOnBoardingFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToOnBoardingFragment()
        navController.navigate(action)
    }
}