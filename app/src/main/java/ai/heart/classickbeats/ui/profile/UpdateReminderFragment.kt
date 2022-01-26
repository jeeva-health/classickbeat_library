package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.NavHomeDirections
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentAddReminderBinding
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.widgets.DateTimePickerViewModel
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.toName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateReminderFragment : BottomSheetDialogFragment() {

    private var binding: FragmentAddReminderBinding? = null

    private lateinit var navController: NavController

    private val dateTimePickerViewModel: DateTimePickerViewModel by activityViewModels()

    private val reminderViewModel: ReminderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddReminderBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        dateTimePickerViewModel.selectedLogTime.observe(viewLifecycleOwner, EventObserver {
            binding?.timeLayout?.editText?.setText(it.toDisplayString())
        })

        reminderViewModel.reminderSaved.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                dismiss()
            }
        })

        binding?.timeLayout?.addOnEditTextAttachedListener(timerEditTextAttachListener)

        binding?.apply {
            cross.setSafeOnClickListener {
                dismiss()
            }
            saveBtn.setSafeOnClickListener {
                val name: String = nameLayout.editText?.text?.toString() ?: "Reminder"
                val time: Time? = dateTimePickerViewModel.selectedLogTime.value?.peekContent()
                val selectedDayList = frequencyChipGroup.checkedChipIds.map {
                    mapChipIdToDayOfWeek(it)
                }
                if (reminderViewModel.selectedReminder != null) {
                    reminderViewModel.updateReminder(
                        reminderViewModel.selectedReminder!!,
                        name,
                        time,
                        selectedDayList
                    )
                } else {
                    reminderViewModel.addReminder(name, time!!, selectedDayList)
                }
            }
            deleteBtn.setSafeOnClickListener {
                reminderViewModel.deleteReminder(reminderViewModel.selectedReminder!!)
            }
        }

        reminderViewModel.selectedReminder?.apply {
            binding?.nameLayout?.editText?.setText(this.type.toName(requireContext()))
            if (this.isReminderSet) {
                binding?.timeLayout?.editText?.setText(this.time.toDisplayString())
                this.frequency.forEach {
                    when (it) {
                        Reminder.DayOfWeek.Monday -> binding?.chipMonday?.isChecked = true
                        Reminder.DayOfWeek.Tuesday -> binding?.chipTuesday?.isChecked = true
                        Reminder.DayOfWeek.Wednesday -> binding?.chipWednesday?.isChecked = true
                        Reminder.DayOfWeek.Thursday -> binding?.chipThursday?.isChecked = true
                        Reminder.DayOfWeek.Friday -> binding?.chipFriday?.isChecked = true
                        Reminder.DayOfWeek.Saturday -> binding?.chipSaturday?.isChecked = true
                        Reminder.DayOfWeek.Sunday -> binding?.chipSunday?.isChecked = true
                    }
                }
            }
            binding?.deleteBtn?.visibility = View.VISIBLE
        }
    }

    private fun mapChipIdToDayOfWeek(chipId: Int): Reminder.DayOfWeek =
        when (chipId) {
            R.id.chip_monday -> Reminder.DayOfWeek.Monday
            R.id.chip_tuesday -> Reminder.DayOfWeek.Tuesday
            R.id.chip_wednesday -> Reminder.DayOfWeek.Wednesday
            R.id.chip_thursday -> Reminder.DayOfWeek.Thursday
            R.id.chip_friday -> Reminder.DayOfWeek.Friday
            R.id.chip_saturday -> Reminder.DayOfWeek.Saturday
            else -> Reminder.DayOfWeek.Sunday
        }

    private val timerEditTextAttachListener = TextInputLayout.OnEditTextAttachedListener {
        it.editText?.setOnClickListener {
            openTimePickerDialog()
        }
    }

    private fun openTimePickerDialog() {
        val action = NavHomeDirections.actionGlobalTimePickerFragment()
        navController.navigate(action)
    }

    override fun onDestroyView() {
        binding?.timeLayout?.removeOnEditTextAttachedListener(timerEditTextAttachListener)
        binding = null
        super.onDestroyView()
    }
}
