package ai.heart.classickbeats.ui.logging

import ai.heart.classickbeats.NavHomeDirections
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentLogWeightBinding
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.showLoadingBar
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint


@ExperimentalPagingApi
@AndroidEntryPoint
class LogWeightFragment : Fragment(R.layout.fragment_log_weight) {

    private var binding: FragmentLogWeightBinding? = null

    private val loggingViewModel: LoggingViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLogWeightBinding.bind(view)

        navController = findNavController()

        binding?.dateLayout?.addOnEditTextAttachedListener(dateEditTextAttachListener)

        binding?.saveBtn?.setSafeOnClickListener {
            saveWeightLog()
        }

        binding?.backArrow?.setSafeOnClickListener {
            navController.navigateUp()
        }

        loggingViewModel.selectedLogDate.observe(viewLifecycleOwner, EventObserver {
            binding?.dateLayout?.editText?.setText(it.toString())
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
                notes = note
            )
        }
    }

    override fun onDestroyView() {
        binding?.dateLayout?.removeOnEditTextAttachedListener(dateEditTextAttachListener)
        super.onDestroyView()
    }
}