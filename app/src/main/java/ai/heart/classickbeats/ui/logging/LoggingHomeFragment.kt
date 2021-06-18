package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentLoggingHomeBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoggingHomeFragment : Fragment(R.layout.fragment_logging_home) {

    private val binding by viewBinding(FragmentLoggingHomeBinding::bind)

    private val loggingViewModel: LoggingViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.bpCard.setSafeOnClickListener {
            navigateToLogBpFragment()
        }

        binding.bpGlucoseCard.setSafeOnClickListener {
            navigateToLogGlucoseFragment()
        }

        binding.waterCard.setSafeOnClickListener {
            navigateToLogWaterIntakeFragment()
        }

        binding.weightCard.setSafeOnClickListener {
            navigateToLogWeightFragment()
        }

        binding.medicationCard.setSafeOnClickListener {
            navigateToLogMedicationFragment()
        }
    }

    private fun navigateToLogBpFragment() {
        val action = LoggingHomeFragmentDirections.actionLoggingHomeFragmentToLogBpFragment()
        navController.navigate(action)
    }

    private fun navigateToLogGlucoseFragment() {
        val action = LoggingHomeFragmentDirections.actionLoggingHomeFragmentToLogGlucoseFragment()
        navController.navigate(action)
    }

    private fun navigateToLogWaterIntakeFragment() {
        val action =
            LoggingHomeFragmentDirections.actionLoggingHomeFragmentToLogWaterIntakeFragment()
        navController.navigate(action)
    }

    private fun navigateToLogWeightFragment() {
        val action = LoggingHomeFragmentDirections.actionLoggingHomeFragmentToLogWeightFragment()
        navController.navigate(action)
    }

    private fun navigateToLogMedicationFragment() {
        val action =
            LoggingHomeFragmentDirections.actionLoggingHomeFragmentToLogMedicationFragment()
        navController.navigate(action)
    }
}