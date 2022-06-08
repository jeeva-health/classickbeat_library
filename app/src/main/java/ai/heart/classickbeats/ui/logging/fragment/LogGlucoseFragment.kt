package ai.heart.classickbeats.ui.logging.fragment

import ai.heart.classickbeats.NavHomeDirections
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentLogGlucoseBinding
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.common.DateTimePickerViewModel
import ai.heart.classickbeats.ui.logging.LoggingViewModel
import ai.heart.classickbeats.utils.*
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@AndroidEntryPoint
class LogGlucoseFragment : Fragment(R.layout.fragment_log_glucose) {

    private var binding: FragmentLogGlucoseBinding? = null

    private val loggingViewModel: LoggingViewModel by activityViewModels()

    private val dateTimePickerViewModel: DateTimePickerViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLightStatusBar()

        binding = FragmentLogGlucoseBinding.bind(view)

        navController = findNavController()

        binding?.glucoseLayout?.requestFocus()

        val currentDate = getCurrentDate()

        val currentTime = getCurrentTime()

        dateTimePickerViewModel.setLogDate(currentDate)

        dateTimePickerViewModel.setLogTime(currentTime)

        binding?.dateLayout?.editText?.setText(currentDate.toString())

        binding?.timeLayout?.editText?.setText(currentTime.toDisplayString())

        binding?.timeLayout?.addOnEditTextAttachedListener(timerEditTextAttachListener)

        binding?.dateLayout?.addOnEditTextAttachedListener(dateEditTextAttachListener)

        binding?.saveBtn?.setSafeOnClickListener {
            saveGlucoseLevelLog()
        }

        binding?.backArrow?.setSafeOnClickListener {
            navController.navigateUp()
        }

        dateTimePickerViewModel.selectedLogDate.observe(viewLifecycleOwner, EventObserver {
            binding?.dateLayout?.editText?.setText(it.toString())
        })

        dateTimePickerViewModel.selectedLogTime.observe(viewLifecycleOwner, EventObserver {
            binding?.timeLayout?.editText?.setText(it.toDisplayString())
        })

        loggingViewModel.navigateToLoggingHome.observe(viewLifecycleOwner, EventObserver {
            hideLoadingBar()
            navController.navigateUp()
        })

        loggingViewModel.showLoading.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                showLoadingBar()
            } else {
                hideLoadingBar()
            }
        })
    }

    private val timerEditTextAttachListener = TextInputLayout.OnEditTextAttachedListener {
        it.editText?.setOnClickListener {
            openTimePickerDialog()
        }
    }

    private val dateEditTextAttachListener = TextInputLayout.OnEditTextAttachedListener {
        it.editText?.setOnClickListener {
            openDatePickerDialog()
        }
    }

    private fun openDatePickerDialog() {
        val action = NavHomeDirections.actionGlobalDatePickerFragment()
        navController.navigate(action)
    }

    private fun openTimePickerDialog() {
        val action = NavHomeDirections.actionGlobalTimePickerFragment()
        navController.navigate(action)
    }

    private fun saveGlucoseLevelLog() {
        binding?.apply {
//            val glucoseLevel = glucoseLayout.editText?.text?.toString()?.toInt() ?: -1
//            val tag = when (tagChipGroup.checkedChipId) {
//                R.id.chip_fasting -> 1
//                R.id.chip_before_meal -> 2
//                R.id.chip_after_meal -> 3
//                else -> 4
//            }
//            val note = notesLayout.editText?.text?.toString()
//            loggingViewModel.uploadGlucoseLevelEntry(
//                glucoseLevel = glucoseLevel,
//                tag = tag,
//                notes = note,
//                time = dateTimePickerViewModel.selectedLogTime.value?.peekContent(),
//                date = dateTimePickerViewModel.selectedLogDate.value?.peekContent()
//            )
        }
    }

    override fun onDestroyView() {
        binding?.timeLayout?.removeOnEditTextAttachedListener(timerEditTextAttachListener)
        binding?.dateLayout?.removeOnEditTextAttachedListener(dateEditTextAttachListener)
        super.onDestroyView()
    }
}
