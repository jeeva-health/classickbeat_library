package ai.heart.classickbeats.ui.profile.reminder.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentReminderListBinding
import ai.heart.classickbeats.model.Reminder
import ai.heart.classickbeats.ui.profile.reminder.ReminderAdapter
import ai.heart.classickbeats.ui.profile.reminder.ReminderViewModel
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ReminderListFragment : Fragment(R.layout.fragment_reminder_list) {

    private val binding by viewBinding(FragmentReminderListBinding::bind)

    private val reminderViewModel: ReminderViewModel by viewModels()

    private lateinit var navController: NavController

    private lateinit var reminderAdapter: ReminderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        reminderAdapter = ReminderAdapter(
            context = requireContext(),
            itemClickListener = reminderItemClickListener,
            toggleClickListener = reminderToggleClickListener
        )

        binding.reminderRv.adapter = reminderAdapter

        binding.addReminder.setSafeOnClickListener {
            openAddReminderFragment()
        }

        lifecycleScope.launchWhenResumed {
            reminderViewModel.getAllLocalReminders().collectLatest {
                reminderAdapter.submitList(it)
                reminderAdapter.notifyDataSetChanged()
            }
        }

        reminderViewModel.getAllReminders()
    }

    private fun openAddReminderFragment() {
        val action =
            ReminderListFragmentDirections.actionReminderListFragmentToAddReminderFragment()
        navController.navigate(action)
    }

    private fun openUpdateReminderFragment(reminderId: Long) {
        val action =
            ReminderListFragmentDirections.actionReminderListFragmentToUpdateReminderFragment(
                reminderId
            )
        navController.navigate(action)
    }

    private val reminderItemClickListener = fun(item: Reminder) {
        openUpdateReminderFragment(item._id)
    }

    private val reminderToggleClickListener = fun(item: Reminder, isChecked: Boolean) {

    }
}
