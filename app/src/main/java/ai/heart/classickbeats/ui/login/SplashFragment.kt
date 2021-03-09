package ai.heart.classickbeats.ui.login

import ai.heart.classickbeats.R
import ai.heart.classickbeats.utils.postOnMainLooper
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        lifecycleScope.launchWhenResumed {
            delay(2000)
            postOnMainLooper {
                navigateToLoginFragment()
            }
        }
    }

    private fun navigateToLoginFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        navController.navigate(action)
    }
}