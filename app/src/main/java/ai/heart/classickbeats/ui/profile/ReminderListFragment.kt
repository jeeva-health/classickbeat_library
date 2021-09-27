package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentReminderListBinding
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReminderListFragment : Fragment(R.layout.fragment_reminder_list) {

    private val binding by viewBinding(FragmentReminderListBinding::bind)

    private val reminderViewModel: ReminderViewModel by activityViewModels()

    private lateinit var navController: NavController

    private lateinit var reminderAdapter: ReminderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        reminderAdapter = ReminderAdapter(reminderItemClickListener, reminderToggleClickListener)

        binding.reminderRv.adapter = reminderAdapter

        binding.addReminder.setSafeOnClickListener {
            openAddReminderDialog()
        }
    }

    override fun onResume() {
        super.onResume()

        val reminders = reminderViewModel.getAllReminders()

        reminderAdapter.submitList(reminders)
    }

    private fun openAddReminderDialog() {
        val action =
            ReminderListFragmentDirections.actionReminderListFragmentToAddReminderFragment()
        navController.navigate(action)
    }

    private val reminderItemClickListener = fun(item: Reminder) {

    }

    private val reminderToggleClickListener = fun(item: Reminder, isChecked: Boolean) {

    }
}