package ai.heart.classickbeats.ui.splash

import ai.heart.classickbeats.R
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.ppg.fragment.ScanFragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val launchViewModel: LaunchViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchViewModel.launchDestination.observe(viewLifecycleOwner, EventObserver {
            navigateToScanFragment()
        })
    }

    private fun navigateToScanFragment() {
        requireActivity().supportFragmentManager.commit {
            replace<ScanFragment>(R.id.nav_host_fragment)
            setReorderingAllowed(true)
        }
    }
}
