package ai.heart.classickbeats.ui.profile.reminder

import ai.heart.classickbeats.NavHomeDirections
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentUpdateReminderBinding
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.widgets.DateTimePickerViewModel
import ai.heart.classickbeats.utils.hideKeyboard
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.toName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateReminderFragment : BottomSheetDialogFragment() {

    private var binding: FragmentUpdateReminderBinding? = null

    private lateinit var navController: NavController

    private val dateTimePickerViewModel: DateTimePickerViewModel by activityViewModels()

    private val reminderViewModel: ReminderViewModel by activityViewModels()

    private val args: UpdateReminderFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateReminderBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reminderViewModel.getReminder(args.reminderId)

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

            reminderViewModel.isDaily.observe(viewLifecycleOwner) { isDaily ->
                if (isDaily) {
                    frequencyDayGroup.isVisible = false
                    chipDaily.setTextColor(requireContext().getColor(R.color.white))
                    chipDaily.setChipBackgroundColorResource(R.color.very_dark_grey_3)
                    chipCustom.setTextColor(requireContext().getColor(R.color.very_dark_grey_3))
                    chipCustom.setChipBackgroundColorResource(R.color.white)
                } else {
                    frequencyDayGroup.isVisible = true
                    chipCustom.setTextColor(requireContext().getColor(R.color.white))
                    chipCustom.setChipBackgroundColorResource(R.color.very_dark_grey_3)
                    chipDaily.setTextColor(requireContext().getColor(R.color.very_dark_grey_3))
                    chipDaily.setChipBackgroundColorResource(R.color.white)
                }
            }

            cross.setSafeOnClickListener {
                dismiss()
            }

            saveBtn.setSafeOnClickListener {
                val time: Time? = dateTimePickerViewModel.selectedLogTime.value?.peekContent()
                val selectedDayList = if (reminderViewModel.isDaily.value == true) {
                    Reminder.DayOfWeek.values().toList()
                } else {
                    frequencyDayGroup.checkedChipIds.map {
                        mapChipIdToDayOfWeek(it)
                    }
                }
                reminderViewModel.updateReminder(
                    time,
                    selectedDayList
                )
            }

            deleteBtn.setSafeOnClickListener {
                reminderViewModel.deleteReminder()
            }
        }

        reminderViewModel.selectedReminder.observe(viewLifecycleOwner) {
            binding?.apply {
                val name = it.type.toName(requireContext())
                val frequency = it.frequency
                if (frequency.size == 7) {
                    reminderViewModel.setIsDaily(true)
                } else {
                    reminderViewModel.setIsDaily(false)
                    frequency.forEach { dayOfWeek ->
                        when (dayOfWeek) {
                            Reminder.DayOfWeek.Monday -> makeChipSelected(chipMonday)
                            Reminder.DayOfWeek.Tuesday -> makeChipSelected(chipTuesday)
                            Reminder.DayOfWeek.Wednesday -> makeChipSelected(chipWednesday)
                            Reminder.DayOfWeek.Thursday -> makeChipSelected(chipThursday)
                            Reminder.DayOfWeek.Friday -> makeChipSelected(chipFriday)
                            Reminder.DayOfWeek.Saturday -> makeChipSelected(chipSaturday)
                            Reminder.DayOfWeek.Sunday -> makeChipSelected(chipSunday)
                        }
                    }
                }
                val time = it.time.toDisplayString()
                nameLayout.editText?.setText(name)
                timeLayout.editText?.setText(time)
            }
        }
    }

    private fun makeChipSelected(view: View) {
        binding?.apply {
            when (view) {
                chipMonday -> {
                    chipMonday.setTextColor(requireContext().getColor(R.color.white))
                    chipMonday.setChipBackgroundColorResource(R.color.moderate_green_2)
                }
                chipTuesday -> {
                    chipTuesday.setTextColor(requireContext().getColor(R.color.white))
                    chipTuesday.setChipBackgroundColorResource(R.color.bright_red_2)
                }
                chipWednesday -> {
                    chipWednesday.setTextColor(requireContext().getColor(R.color.white))
                    chipWednesday.setChipBackgroundColorResource(R.color.soft_blue)
                }
                chipThursday -> {
                    chipThursday.setTextColor(requireContext().getColor(R.color.white))
                    chipThursday.setChipBackgroundColorResource(R.color.very_soft_red_2)
                }
                chipFriday -> {
                    chipFriday.setTextColor(requireContext().getColor(R.color.white))
                    chipFriday.setChipBackgroundColorResource(R.color.dark_red)
                }
                chipSaturday -> {
                    chipSaturday.setTextColor(requireContext().getColor(R.color.white))
                    chipSaturday.setChipBackgroundColorResource(R.color.moderate_violet)
                }
                chipSunday -> {
                    chipSunday.setTextColor(requireContext().getColor(R.color.white))
                    chipSunday.setChipBackgroundColorResource(R.color.vivid_yellow_2)
                }
                else -> throw Exception("Unknown viewId: ${view.id}")
            }
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
        it.editText?.setOnClickListener { view ->
            hideKeyboard(view)
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
