package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentCalculatingBinding
import ai.heart.classickbeats.utils.EventObserver
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CalculatingFragment : Fragment(R.layout.fragment_calculating) {

    private val binding by viewBinding(FragmentCalculatingBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        monitorViewModel.outputComputed.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                when (monitorViewModel.testType) {
                    TestType.HEART_RATE -> navigateToHeartResultFragment()
                    //TestType.OXYGEN_SATURATION -> navigateToOxygenResultFragment()
                    TestType.OXYGEN_SATURATION -> navigateToHeartResultFragment()
                }
            }
        })
    }

    private fun navigateToHeartResultFragment() {
        val action =
            CalculatingFragmentDirections.actionCalculatingFragmentToHeartResultFragment()
        navController.navigate(action)
    }

    private fun navigateToOxygenResultFragment() {
        val action =
            CalculatingFragmentDirections.actionCalculatingFragmentToOxygenResultFragment()
        navController.navigate(action)
    }
}