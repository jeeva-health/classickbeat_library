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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReminderFragment : Fragment() {

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
                navigateBack()
            }
        })

        binding?.timeLayout?.addOnEditTextAttachedListener(timerEditTextAttachListener)

        binding?.apply {
            arrayOf(
                chipMonday,
                chipTuesday,
                chipWednesday,
                chipThursday,
                chipFriday,
                chipSaturday,
                chipSunday
            ).forEach {
                it.setOnClickListener { view ->
                    when (view) {
                        chipMonday -> {
                            if (frequencyChipGroup.checkedChipIds.contains(chipMonday.id)) {
                                chipMonday.setTextColor(requireContext().getColor(R.color.white))
                                chipMonday.setChipBackgroundColorResource(R.color.moderate_green_2)
                            } else {
                                chipMonday.setTextColor(requireContext().getColor(R.color.moderate_green_2))
                                chipMonday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipTuesday -> {
                            if (frequencyChipGroup.checkedChipIds.contains(chipTuesday.id)) {
                                chipTuesday.setTextColor(requireContext().getColor(R.color.white))
                                chipTuesday.setChipBackgroundColorResource(R.color.bright_red_2)
                            } else {
                                chipTuesday.setTextColor(requireContext().getColor(R.color.bright_red_2))
                                chipTuesday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipWednesday -> {
                            if (frequencyChipGroup.checkedChipIds.contains(chipWednesday.id)) {
                                chipWednesday.setTextColor(requireContext().getColor(R.color.white))
                                chipWednesday.setChipBackgroundColorResource(R.color.soft_blue)
                            } else {
                                chipWednesday.setTextColor(requireContext().getColor(R.color.soft_blue))
                                chipWednesday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipThursday -> {
                            if (frequencyChipGroup.checkedChipIds.contains(chipThursday.id)) {
                                chipThursday.setTextColor(requireContext().getColor(R.color.white))
                                chipThursday.setChipBackgroundColorResource(R.color.very_soft_red_2)
                            } else {
                                chipThursday.setTextColor(requireContext().getColor(R.color.very_soft_red_2))
                                chipThursday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipFriday -> {
                            if (frequencyChipGroup.checkedChipIds.contains(chipFriday.id)) {
                                chipFriday.setTextColor(requireContext().getColor(R.color.white))
                                chipFriday.setChipBackgroundColorResource(R.color.dark_red)
                            } else {
                                chipFriday.setTextColor(requireContext().getColor(R.color.dark_red))
                                chipFriday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipSaturday -> {
                            if (frequencyChipGroup.checkedChipIds.contains(chipSaturday.id)) {
                                chipSaturday.setTextColor(requireContext().getColor(R.color.white))
                                chipSaturday.setChipBackgroundColorResource(R.color.moderate_violet)
                            } else {
                                chipSaturday.setTextColor(requireContext().getColor(R.color.moderate_violet))
                                chipSaturday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipSunday -> {
                            if (frequencyChipGroup.checkedChipIds.contains(chipSunday.id)) {
                                chipSunday.setTextColor(requireContext().getColor(R.color.white))
                                chipSunday.setChipBackgroundColorResource(R.color.vivid_yellow_2)
                            } else {
                                chipSunday.setTextColor(requireContext().getColor(R.color.vivid_yellow_2))
                                chipSunday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        else -> throw Exception("Unknown viewId: ${it.id}")
                    }
                }
            }


            cross.setSafeOnClickListener {
                navigateBack()
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

    private fun navigateBack() {

    }

    override fun onDestroyView() {
        binding?.timeLayout?.removeOnEditTextAttachedListener(timerEditTextAttachListener)
        binding = null
        super.onDestroyView()
    }
}
