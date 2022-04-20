package ai.heart.classickbeats.ui.ppg.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanQuestionBinding
import ai.heart.classickbeats.ui.ppg.viewmodel.MonitorViewModel
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
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


    @SuppressLint("SetTextI18n")
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


//        bottomSheetBehavior = BottomSheetBehavior.from(binding.nestedBottomSheet)
//        bottomSheetBehavior!!.halfExpandedRatio = 0.7f //changing the half expanding ratio
//
//        bottomSheetBehavior!!.setBottomSheetCallback(object : BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//
//                /** when (newState) {
//                BottomSheetBehavior.STATE_COLLAPSED -> {
//                //todo
//                Toast.makeText(context, "collapsed", Toast.LENGTH_SHORT).show()
//                }
//
//                BottomSheetBehavior.STATE_DRAGGING -> {
//                //todo
//                Toast.makeText(context, "dragging", Toast.LENGTH_SHORT).show()
//                }
//
//                BottomSheetBehavior.STATE_EXPANDED -> {
//                //todo
//                Toast.makeText(context, "expanded", Toast.LENGTH_SHORT).show()
//                }
//
//                BottomSheetBehavior.STATE_HALF_EXPANDED -> {
//                //todo
//                Toast.makeText(context, "half expanded", Toast.LENGTH_SHORT)
//                .show()
//                }
//
//                BottomSheetBehavior.STATE_HIDDEN -> {
//                //todo
//                Toast.makeText(context, "hidden", Toast.LENGTH_SHORT).show()
//                }
//
//                BottomSheetBehavior.STATE_SETTLING -> {
//                //todo
//                Toast.makeText(context, "settling", Toast.LENGTH_SHORT).show()
//                }
//
//                }**/
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                val upperState = 0.72f
//                val lowerState = 0.36f
//                if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_SETTLING) {
//                    if (slideOffset > lowerState && slideOffset < upperState) {
//                        bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
//                    } else if (slideOffset >= upperState) {
//                        bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
//                    } else if (slideOffset <= lowerState) {
//                        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
//                    }
//                }
//            }
//        })

        setTextWithEmoticon()

        binding.sleepSlider.addOnChangeListener { slider, value, fromUser ->
            binding.sleepTimeText.text = "${(value/60).toInt()} : ${(value % 60).toInt()}"
            binding.sleepSlider.labelBehavior = ((value/30).toInt())
            //todo get the sleep time "value"
        }

        binding.apply {
            arrayOf(
                chipHappy,
                chipGood,
                chipSick,
                chipExcited,
                chipEnergetic,
                chipCalm,
                chipSad,
                chipTired,
                chipNormal,
                chipStressed
            ).forEach {
                it.setOnClickListener { v ->
                    if (it.isChecked) {
                        //todo add to the list
                    }
                    btnEnabled()
                }
            }
        }

        binding.apply {
            arrayOf(
                chipChilling,
                chipWorking,
                chipSleeping,
                chipEating,
                chipWorkout,
                chipOther
            ).forEach {
                it.setOnClickListener { v ->
                    //todo scantype to required
                    btnEnabled()
                }
            }
        }

        binding.continueBtn.setOnClickListener {
            navToMyHealthFragment()
            /*if (btnEnabled()) {
                //todo on continue btn click
            }*/
        }


    }

    @SuppressLint("SetTextI18n")
    private fun setTextWithEmoticon() {
        binding.chipHappy.text = setEmoji(0x1F606) + " Happy"
        binding.chipGood.text = setEmoji(0x1F44C) + " Good"
        binding.chipSick.text = setEmoji(0x1F912) + " Sick"
        binding.chipExcited.text = setEmoji(0x1F929) + " Excited"
        binding.chipEnergetic.text = setEmoji(0x1F483) + " Energetic"
        binding.chipCalm.text = setEmoji(0x1F9D8) + " Calm"
        binding.chipSad.text = setEmoji(0x1F641) + " Sad"
        binding.chipTired.text = setEmoji(0x1F62E) + " Tired"
        binding.chipNormal.text = setEmoji(0x1F91E) + " Normal"
        binding.chipStressed.text = setEmoji(0x1F624) + " Stressed"

        binding.chipChilling.text = setEmoji(0x1F468) + " Chilling"
        binding.chipWorking.text = setEmoji(0x1F4AA) + " Working"
        binding.chipSleeping.text = setEmoji(0x1F610) + " Sleeping"
        binding.chipEating.text = setEmoji(0x1F35A) + " Eating"
        binding.chipWorkout.text = setEmoji(0x1F3CB) + " Exercising"
        binding.chipOther.text = setEmoji(0x1F536) + " Other"
    }

    private fun setEmoji(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    @SuppressLint("ResourceAsColor")
    private fun btnEnabled(): Boolean{
//        if (mood && sleep && scan){
//            binding.continueBtn.setBackgroundColor(R.color.rosy_pink)
//            binding.continueBtnImg.setBackgroundColor(R.color.pale_red)
//
//        }
        return false
    }

    private fun navToMyHealthFragment(){
            val action = ScanQuestionFragmentDirections.actionScanQuestionFragmentToMyHealthFragment()
        findNavController().navigate(action)
    }

}

//    private fun resetAllChips() {
//        binding.apply {
//            chipEating.setChipStrokeColorResource(ScanState.Eating.getColor())
//            chipSleeping.setChipStrokeColorResource(ScanState.Sleeping.getColor())
//            chipChilling.setChipStrokeColorResource(ScanState.Chilling.getColor())
//            chipWorkout.setChipStrokeColorResource(ScanState.Workout.getColor())
//            chipWorking.setChipStrokeColorResource(ScanState.Working.getColor())
//            chipOther.setChipStrokeColorResource(ScanState.Others.getColor())
//
//            chipEating.setTextColor(requireContext().getColor(ScanState.Eating.getColor()))
//            chipSleeping.setTextColor(requireContext().getColor(ScanState.Sleeping.getColor()))
//            chipChilling.setTextColor(requireContext().getColor(ScanState.Chilling.getColor()))
//            chipWorkout.setTextColor(requireContext().getColor(ScanState.Workout.getColor()))
//            chipWorking.setTextColor(requireContext().getColor(ScanState.Working.getColor()))
//            chipOther.setTextColor(requireContext().getColor(ScanState.Others.getColor()))
//
//            arrayOf(
//                chipEating,
//                chipSleeping,
//                chipChilling,
//                chipWorkout,
//                chipWorking,
//                chipOther
//            ).forEach {
//                it.setChipBackgroundColorResource(R.color.white)
//            }
//        }
//    }
//
//    private fun navigateToScanResultFragment() {
//        hideLoadingBar()
//        val action = ScanQuestionFragmentDirections.actionScanQuestionFragmentToScanResultFragment(
//            showingHistory = false,
//            scanId = monitorViewModel.ppgId
//        )
//        findNavController().navigate(action)
//    }

