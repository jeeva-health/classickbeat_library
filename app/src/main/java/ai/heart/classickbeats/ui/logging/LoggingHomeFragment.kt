package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentLoggingHomeBinding
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.entity.BpLogEntity
import ai.heart.classickbeats.model.entity.GlucoseLogEntity
import ai.heart.classickbeats.model.entity.WaterLogEntity
import ai.heart.classickbeats.model.entity.WeightLogEntity
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.utils.*
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@AndroidEntryPoint
class LoggingHomeFragment : Fragment(R.layout.fragment_logging_home) {

    private val binding by viewBinding(FragmentLoggingHomeBinding::bind)

    private val loggingViewModel: LoggingViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLightStatusBar()

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

        loggingViewModel.reloadScreen.observe(viewLifecycleOwner, EventObserver {
            reloadCards()
        })

        loggingViewModel.getLoggingData()
    }

    private fun reloadCards() {
        binding.apply {
            loggingViewModel.loggingData?.forEach { logEntity ->
                when (logEntity.type) {
                    LogType.BloodPressure -> {
                        val bpLogEntity = logEntity as BpLogEntity
                        val diastolic = bpLogEntity.diastolic
                        val systolic = bpLogEntity.systolic
                        val bpString = "$systolic/$diastolic"
                        bpValue.text = bpString
                    }
                    LogType.GlucoseLevel -> {
                        val glucoseEntity = logEntity as GlucoseLogEntity
                        val glucoseLevel = glucoseEntity.glucoseLevel.toString()
                        glucoseValue.text = glucoseLevel
                    }
                    LogType.WaterIntake -> {
                        val waterEntity = logEntity as WaterLogEntity
                        val waterQuantity = waterEntity.quantity.toString()
                        waterValue.text = waterQuantity
                    }
                    LogType.Weight -> {
                        val weightEntity = logEntity as WeightLogEntity
                        val weight = weightEntity.weight.toString()
                        weightValue.text = weight
                    }
                    LogType.Medicine -> TODO()
                    LogType.PPG -> TODO()
                }
            }
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
}
