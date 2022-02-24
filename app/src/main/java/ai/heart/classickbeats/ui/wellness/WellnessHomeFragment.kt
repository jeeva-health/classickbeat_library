package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentWellnessHomeBinding
import ai.heart.classickbeats.model.WellnessType
import ai.heart.classickbeats.utils.setLightStatusBar
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
class WellnessHomeFragment : Fragment(R.layout.fragment_wellness_home) {

    private val binding by viewBinding(FragmentWellnessHomeBinding::bind)

    private val wellnessViewModel: WellnessViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLightStatusBar()

        navController = findNavController()

        binding.sleepCard.setSafeOnClickListener {
            navigateToWellnessCategoryFragment(WellnessType.SLEEP)
        }

        binding.angerCard.setSafeOnClickListener {
            navigateToWellnessCategoryFragment(WellnessType.ANGER)
        }

        binding.bpCard.setSafeOnClickListener {
            navigateToWellnessCategoryFragment(WellnessType.BLOOD_PRESSURE)
        }

        binding.stressCard.setSafeOnClickListener {
            navigateToWellnessCategoryFragment(WellnessType.STRESS)
        }

        binding.immunityCard.setSafeOnClickListener {
            navigateToWellnessCategoryFragment(WellnessType.IMMUNITY)
        }

        wellnessViewModel.getMeditationList()
    }

    private fun navigateToWellnessCategoryFragment(wellnessType: WellnessType = WellnessType.SLEEP) {
        val action =
            WellnessHomeFragmentDirections.actionWellnessHomeFragmentToWellnessCategoryFragment(
                wellnessType = wellnessType
            )
        navController.navigate(action)
    }
}