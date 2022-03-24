package ai.heart.classickbeats.ui.ppg.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanQuestionBinding
import ai.heart.classickbeats.model.ScanState
import ai.heart.classickbeats.ui.ppg.viewmodel.MonitorViewModel
import ai.heart.classickbeats.utils.getColor
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@AndroidEntryPoint
class ScanQuestionFragment : Fragment(R.layout.fragment_scan_question) {

    private val binding by viewBinding(FragmentScanQuestionBinding::bind)
    private val monitorViewModel: MonitorViewModel by activityViewModels()
    private var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** setLightStatusBar()

        resetAllChips()

        binding.apply {

        arrayOf(
        chipEating,
        chipSleeping,
        chipChilling,
        chipWorkout,
        chipWorking,
        chipOther
        ).forEach {
        it.setSafeOnClickListener {
        resetAllChips()

        val scanState = when (beforeScanChipGroup.checkedChipId) {
        R.id.chip_eating -> ScanState.Eating
        R.id.chip_sleeping -> ScanState.Sleeping
        R.id.chip_chilling -> ScanState.Chilling
                        R.id.chip_workout -> ScanState.Workout
                        R.id.chip_working -> ScanState.Working
                        else -> ScanState.Others
                    }

                    when (scanState) {
                        ScanState.Eating -> {
                            chipEating.setTextColor(requireContext().getColor(R.color.white))
                            chipEating.setChipBackgroundColorResource(ScanState.Eating.getColor())
                        }
                        ScanState.Sleeping -> {
                            chipSleeping.setTextColor(requireContext().getColor(R.color.white))
                            chipSleeping.setChipBackgroundColorResource(ScanState.Sleeping.getColor())
                        }
                        ScanState.Chilling -> {
                            chipChilling.setTextColor(requireContext().getColor(R.color.white))
                            chipChilling.setChipBackgroundColorResource(ScanState.Chilling.getColor())
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
        val scanState = when (beforeScanChipGroup.checkedChipId) {
        R.id.chip_eating -> ScanState.Eating
        R.id.chip_sleeping -> ScanState.Sleeping
        R.id.chip_chilling -> ScanState.Chilling
        R.id.chip_workout -> ScanState.Workout
        R.id.chip_working -> ScanState.Working
        else -> ScanState.Others
        }
        monitorViewModel.uploadScanSurvey(
        sleepRating,
        moodRating,
        scanState.getText(requireContext())
        )
        navigateToScanResultFragment()
        }
        }**/


        bottomSheetBehavior = BottomSheetBehavior.from(binding.nestedBottomSheet)
        bottomSheetBehavior!!.halfExpandedRatio = 0.7f //changing the half expanding ratio

        bottomSheetBehavior!!.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        //todo
                        Toast.makeText(context, "collapsed", Toast.LENGTH_SHORT).show()
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        //todo
                        Toast.makeText(context, "dragging", Toast.LENGTH_SHORT).show()
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //todo
                        Toast.makeText(context, "expanded", Toast.LENGTH_SHORT).show()
                    }

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        //todo
                        Toast.makeText(context, "half expanded", Toast.LENGTH_SHORT)
                            .show()
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        //todo
                        Toast.makeText(context, "hidden", Toast.LENGTH_SHORT).show()
                    }

                    BottomSheetBehavior.STATE_SETTLING -> {
                        //todo
                        Toast.makeText(context, "settling", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val upperState = 0.72f
                val lowerState = 0.36f
                if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_SETTLING) {
                    if (slideOffset > lowerState && slideOffset < upperState) {
                        bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
                    } else if (slideOffset >= upperState) {
                        bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else if (slideOffset <= lowerState) {
                        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }
        })
    }

    private fun resetAllChips() {
        binding.apply {
            chipEating.setChipStrokeColorResource(ScanState.Eating.getColor())
            chipSleeping.setChipStrokeColorResource(ScanState.Sleeping.getColor())
            chipChilling.setChipStrokeColorResource(ScanState.Chilling.getColor())
            chipWorkout.setChipStrokeColorResource(ScanState.Workout.getColor())
            chipWorking.setChipStrokeColorResource(ScanState.Working.getColor())
            chipOther.setChipStrokeColorResource(ScanState.Others.getColor())

            chipEating.setTextColor(requireContext().getColor(ScanState.Eating.getColor()))
            chipSleeping.setTextColor(requireContext().getColor(ScanState.Sleeping.getColor()))
            chipChilling.setTextColor(requireContext().getColor(ScanState.Chilling.getColor()))
            chipWorkout.setTextColor(requireContext().getColor(ScanState.Workout.getColor()))
            chipWorking.setTextColor(requireContext().getColor(ScanState.Working.getColor()))
            chipOther.setTextColor(requireContext().getColor(ScanState.Others.getColor()))

            arrayOf(
                chipEating,
                chipSleeping,
                chipChilling,
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
