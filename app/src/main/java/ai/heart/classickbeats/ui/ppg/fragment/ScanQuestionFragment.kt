package ai.heart.classickbeats.ui.ppg.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanQuestionBinding
import ai.heart.classickbeats.domain.getColor
import ai.heart.classickbeats.domain.getText
import ai.heart.classickbeats.model.ScanState
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.ppg.viewmodel.MonitorViewModel
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.setLightStatusBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@AndroidEntryPoint
class ScanQuestionFragment : Fragment(R.layout.fragment_scan_question) {

    private val binding by viewBinding(FragmentScanQuestionBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLightStatusBar()

        resetAllChips()

        binding.apply {

            arrayOf(
                chipEating,
                chipNapping,
                chipChilling,
                chipWokeUp,
                chipWorkout,
                chipWorking,
                chipOther
            ).forEach {
                it.setSafeOnClickListener {
                    resetAllChips()

                    val scanState = when (beforeScanChipGroup.checkedChipId) {
                        R.id.chip_eating -> ScanState.Eating
                        R.id.chip_napping -> ScanState.Napping
                        R.id.chip_chilling -> ScanState.Chilling
                        R.id.chip_woke_up -> ScanState.JustWokeUp
                        R.id.chip_workout -> ScanState.Workout
                        R.id.chip_working -> ScanState.Working
                        else -> ScanState.Others
                    }

                    when (scanState) {
                        ScanState.Eating -> {
                            chipEating.setTextColor(requireContext().getColor(R.color.white))
                            chipEating.setChipBackgroundColorResource(ScanState.Eating.getColor())
                        }
                        ScanState.Napping -> {
                            chipNapping.setTextColor(requireContext().getColor(R.color.white))
                            chipNapping.setChipBackgroundColorResource(ScanState.Napping.getColor())
                        }
                        ScanState.Chilling -> {
                            chipChilling.setTextColor(requireContext().getColor(R.color.white))
                            chipChilling.setChipBackgroundColorResource(ScanState.Chilling.getColor())
                        }
                        ScanState.JustWokeUp -> {
                            chipWokeUp.setTextColor(requireContext().getColor(R.color.white))
                            chipWokeUp.setChipBackgroundColorResource(ScanState.JustWokeUp.getColor())
                        }
                        ScanState.Workout -> {
                            chipWorkout.setTextColor(requireContext().getColor(R.color.white))
                            chipWorkout.setChipBackgroundColorResource(ScanState.Workout.getColor())
                        }
                        ScanState.Working -> {
                            chipWorking.setTextColor(requireContext().getColor(R.color.white))
                            chipWorking.setChipBackgroundColorResource(ScanState.Working.getColor())
                        }
                        ScanState.Others -> {
                            chipOther.setTextColor(requireContext().getColor(R.color.white))
                            chipOther.setChipBackgroundColorResource(ScanState.Others.getColor())
                        }
                    }
                }
            }

            saveBtn.setSafeOnClickListener {
                val sleepRating = sleepSlider.value.toInt()
                val moodRating = moodSlider.value.toInt()
                val healthRating = healthSlider.value.toInt()
                val scanState = when (beforeScanChipGroup.checkedChipId) {
                    R.id.chip_eating -> ScanState.Eating
                    R.id.chip_napping -> ScanState.Napping
                    R.id.chip_chilling -> ScanState.Chilling
                    R.id.chip_woke_up -> ScanState.JustWokeUp
                    R.id.chip_workout -> ScanState.Workout
                    R.id.chip_working -> ScanState.Working
                    else -> ScanState.Others
                }
                monitorViewModel.uploadScanSurvey(
                    sleepRating,
                    moodRating,
                    healthRating,
                    scanState.getText(requireContext())
                )
                navigateToScanResultFragment()
            }
        }
    }

    private fun resetAllChips() {
        binding.apply {
            chipEating.setChipStrokeColorResource(ScanState.Eating.getColor())
            chipNapping.setChipStrokeColorResource(ScanState.Napping.getColor())
            chipChilling.setChipStrokeColorResource(ScanState.Chilling.getColor())
            chipWokeUp.setChipStrokeColorResource(ScanState.JustWokeUp.getColor())
            chipWorkout.setChipStrokeColorResource(ScanState.Workout.getColor())
            chipWorking.setChipStrokeColorResource(ScanState.Working.getColor())
            chipOther.setChipStrokeColorResource(ScanState.Others.getColor())

            chipEating.setTextColor(requireContext().getColor(ScanState.Eating.getColor()))
            chipNapping.setTextColor(requireContext().getColor(ScanState.Napping.getColor()))
            chipChilling.setTextColor(requireContext().getColor(ScanState.Chilling.getColor()))
            chipWokeUp.setTextColor(requireContext().getColor(ScanState.JustWokeUp.getColor()))
            chipWorkout.setTextColor(requireContext().getColor(ScanState.Workout.getColor()))
            chipWorking.setTextColor(requireContext().getColor(ScanState.Working.getColor()))
            chipOther.setTextColor(requireContext().getColor(ScanState.Others.getColor()))

            arrayOf(
                chipEating,
                chipNapping,
                chipChilling,
                chipWokeUp,
                chipWorkout,
                chipWorking,
                chipOther
            ).forEach {
                it.setChipBackgroundColorResource(R.color.white)
            }
        }
    }

    private fun navigateToScanResultFragment() {
        hideLoadingBar()
        val action = ScanQuestionFragmentDirections.actionScanQuestionFragmentToScanResultFragment(
            showingHistory = false,
            scanId = monitorViewModel.ppgId
        )
        findNavController().navigate(action)
    }
}
