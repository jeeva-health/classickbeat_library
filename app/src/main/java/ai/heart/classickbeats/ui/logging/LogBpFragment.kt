package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.NavHomeDirections
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentLogBpBinding
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LogBpFragment : Fragment(R.layout.fragment_log_bp) {

    private var binding: FragmentLogBpBinding? = null

    private val loggingViewModel: LoggingViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLogBpBinding.bind(view)

        navController = findNavController()

        binding?.timeLayout?.addOnEditTextAttachedListener(timerEditTextAttachListener)

        binding?.dateLayout?.addOnEditTextAttachedListener(dateEditTextAttachListener)

        binding?.saveBtn?.setSafeOnClickListener {
            saveBpLog()
        }

        loggingViewModel.selectedLogDate.observe(viewLifecycleOwner, EventObserver {
            binding?.dateLayout?.editText?.setText(it.toString())
        })

        loggingViewModel.selectedLogTime.observe(viewLifecycleOwner, EventObserver {
            binding?.timeLayout?.editText?.setText(it.toString())
        })
    }

    val timerEditTextAttachListener = TextInputLayout.OnEditTextAttachedListener {
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

    private fun saveBpLog() {
        binding?.apply {
            val systolicPressure = systolicLayout.editText?.text?.toString()?.toInt() ?: -1
            val diastolicPressure = diastolicLayout.editText?.text?.toString()?.toInt() ?: -1
            val note = notesLayout.editText?.text?.toString()
            loggingViewModel.uploadBloodPressureEntry(
                systolic = systolicPressure,
                diastolic = diastolicPressure,
                notes = note
            )
        }
    }

    override fun onDestroyView() {
        binding?.timeLayout?.removeOnEditTextAttachedListener(timerEditTextAttachListener)
        binding?.dateLayout?.removeOnEditTextAttachedListener(dateEditTextAttachListener)
        super.onDestroyView()
    }
}