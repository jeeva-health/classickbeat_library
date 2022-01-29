package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.NavHomeDirections
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentLogWeightBinding
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.common.DateTimePickerViewModel
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
class LogWeightFragment : Fragment(R.layout.fragment_log_weight) {

    private var binding: FragmentLogWeightBinding? = null

    private val loggingViewModel: LoggingViewModel by activityViewModels()

    private val dateTimePickerViewModel: DateTimePickerViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLogWeightBinding.bind(view)

        setLightStatusBar()

        navController = findNavController()

        binding?.weightLayout?.requestFocus()

        val currentDate = getCurrentDate()

        val currentTime = getCurrentTime()

        dateTimePickerViewModel.setLogDate(currentDate)

        dateTimePickerViewModel.setLogTime(currentTime)

        binding?.dateLayout?.editText?.setText(currentDate.toString())

        binding?.dateLayout?.addOnEditTextAttachedListener(dateEditTextAttachListener)

        binding?.saveBtn?.setSafeOnClickListener {
            saveWeightLog()
        }

        binding?.backArrow?.setSafeOnClickListener {
            navController.navigateUp()
        }

        dateTimePickerViewModel.selectedLogDate.observe(viewLifecycleOwner, EventObserver {
            binding?.dateLayout?.editText?.setText(it.toString())
        })

        loggingViewModel.navigateToLoggingHome.observe(viewLifecycleOwner, EventObserver {
            hideLoadingBar()
            navController.navigateUp()
        })
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

    private fun saveWeightLog() {
        binding?.apply {
            val weight = weightLayout.editText?.text?.toString()?.toFloat() ?: -1.0f
            val note = notesLayout.editText?.text?.toString()
            loggingViewModel.uploadWeightEntry(
                weight = weight,
                notes = note,
                time = dateTimePickerViewModel.selectedLogTime.value?.peekContent(),
                date = dateTimePickerViewModel.selectedLogDate.value?.peekContent()
            )
        }
    }

    override fun onDestroyView() {
        binding?.dateLayout?.removeOnEditTextAttachedListener(dateEditTextAttachListener)
        binding = null
        super.onDestroyView()
    }
}
