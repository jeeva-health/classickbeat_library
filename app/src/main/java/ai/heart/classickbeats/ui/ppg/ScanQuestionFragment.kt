package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanQuestionBinding
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.showLoadingBar
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController


class ScanQuestionFragment : Fragment(R.layout.fragment_scan_question) {

    private val binding by viewBinding(FragmentScanQuestionBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private var isWaiting = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            saveBtn.setSafeOnClickListener {
                val sleepRating = sleepSlider.value.toInt()
                val moodRating = moodSlider.value.toInt()
                val healthRating = healthSlider.value.toInt()
                val scanState = when (beforeScanChipGroup.checkedChipId) {
                    R.id.chip_eating -> "Eating"
                    R.id.chip_napping -> "Napping"
                    R.id.chip_chilling -> "Chilling"
                    R.id.chip_woke_up -> "Woke up"
                    R.id.chip_workout -> "Workout"
                    else -> "Other"
                }
                monitorViewModel.uploadScanSurvey(sleepRating, moodRating, healthRating, scanState)
                if (monitorViewModel.outputComputed.value?.peekContent() == true) {
                    isWaiting = false
                    navigateToScanResultFragment()
                } else {
                    isWaiting = true
                    showLoadingBar()
                }
            }
        }

        monitorViewModel.outputComputed.observe(viewLifecycleOwner, EventObserver {
            if (it && isWaiting) {
                navigateToScanResultFragment()
            }
        })
    }

    private fun navigateToScanResultFragment() {
        hideLoadingBar()
        val action = ScanQuestionFragmentDirections.actionScanQuestionFragmentToScanResultFragment()
        findNavController().navigate(action)
    }
}