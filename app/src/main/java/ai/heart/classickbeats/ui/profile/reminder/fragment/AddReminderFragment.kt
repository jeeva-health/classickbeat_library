package ai.heart.classickbeats.ui.profile.reminder.fragment

import ai.heart.classickbeats.NavHomeDirections
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentAddReminderBinding
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.ui.profile.reminder.ReminderViewModel
import ai.heart.classickbeats.ui.widgets.DateTimePickerViewModel
import ai.heart.classickbeats.utils.hideKeyboard
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.toName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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

            nameLayout.editText?.setOnClickListener {
                hideKeyboard(it)
                openReminderTypeDialog()
            }

            chipCustom.setOnClickListener {
                reminderViewModel.setIsDaily(false)
            }

            chipDaily.setOnClickListener {
                reminderViewModel.setIsDaily(true)
            }

            reminderViewModel.isDaily.observe(viewLifecycleOwner, EventObserver { isDaily ->
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
            })

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
                            if (frequencyDayGroup.checkedChipIds.contains(chipMonday.id)) {
                                chipMonday.setTextColor(requireContext().getColor(R.color.white))
                                chipMonday.setChipBackgroundColorResource(R.color.moderate_green_2)
                            } else {
                                chipMonday.setTextColor(requireContext().getColor(R.color.moderate_green_2))
                                chipMonday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipTuesday -> {
                            if (frequencyDayGroup.checkedChipIds.contains(chipTuesday.id)) {
                                chipTuesday.setTextColor(requireContext().getColor(R.color.white))
                                chipTuesday.setChipBackgroundColorResource(R.color.bright_red_2)
                            } else {
                                chipTuesday.setTextColor(requireContext().getColor(R.color.bright_red_2))
                                chipTuesday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipWednesday -> {
                            if (frequencyDayGroup.checkedChipIds.contains(chipWednesday.id)) {
                                chipWednesday.setTextColor(requireContext().getColor(R.color.white))
                                chipWednesday.setChipBackgroundColorResource(R.color.soft_blue)
                            } else {
                                chipWednesday.setTextColor(requireContext().getColor(R.color.soft_blue))
                                chipWednesday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipThursday -> {
                            if (frequencyDayGroup.checkedChipIds.contains(chipThursday.id)) {
                                chipThursday.setTextColor(requireContext().getColor(R.color.white))
                                chipThursday.setChipBackgroundColorResource(R.color.very_soft_red_2)
                            } else {
                                chipThursday.setTextColor(requireContext().getColor(R.color.very_soft_red_2))
                                chipThursday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipFriday -> {
                            if (frequencyDayGroup.checkedChipIds.contains(chipFriday.id)) {
                                chipFriday.setTextColor(requireContext().getColor(R.color.white))
                                chipFriday.setChipBackgroundColorResource(R.color.dark_red)
                            } else {
                                chipFriday.setTextColor(requireContext().getColor(R.color.dark_red))
                                chipFriday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipSaturday -> {
                            if (frequencyDayGroup.checkedChipIds.contains(chipSaturday.id)) {
                                chipSaturday.setTextColor(requireContext().getColor(R.color.white))
                                chipSaturday.setChipBackgroundColorResource(R.color.moderate_violet)
                            } else {
                                chipSaturday.setTextColor(requireContext().getColor(R.color.moderate_violet))
                                chipSaturday.setChipBackgroundColorResource(R.color.white)
                            }
                        }
                        chipSunday -> {
                            if (frequencyDayGroup.checkedChipIds.contains(chipSunday.id)) {
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
                saveReminder()
            }
        }

        reminderViewModel.reminderType.observe(viewLifecycleOwner, EventObserver {
            val reminderName = it.toName(requireContext())
            binding?.nameLayout?.editText?.setText(reminderName)
        })
    }

    private fun FragmentAddReminderBinding.saveReminder() {
        val time: Time? = dateTimePickerViewModel.selectedLogTime.value?.peekContent()
        val selectedDayList = if (reminderViewModel.isDaily.value?.peekContent() == true) {
            Reminder.DayOfWeek.values().toList()
        } else {
            frequencyDayGroup.checkedChipIds.map {
                mapChipIdToDayOfWeek(it)
            }
        }
        reminderViewModel.addReminder(time!!, selectedDayList)
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

    private fun openReminderTypeDialog() {
        val action =
            AddReminderFragmentDirections.actionAddReminderFragmentToReminderTypeBottomSheetFragment()
        navController.navigate(action)
    }

    private fun openTimePickerDialog() {
        val action = NavHomeDirections.actionGlobalTimePickerFragment()
        navController.navigate(action)
    }

    private fun navigateBack() {
        navController.navigateUp()
    }

    override fun onDestroyView() {
        binding?.timeLayout?.removeOnEditTextAttachedListener(timerEditTextAttachListener)
        binding = null
        super.onDestroyView()
    }
}
