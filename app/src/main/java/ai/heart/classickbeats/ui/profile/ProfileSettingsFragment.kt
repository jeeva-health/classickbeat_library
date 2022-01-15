package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentProfileSettingsBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.showLongToast
import ai.heart.classickbeats.utils.viewBinding
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileSettingsFragment : Fragment(R.layout.fragment_profile_settings) {

    private val binding by viewBinding(FragmentProfileSettingsBinding::bind)

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        profileViewModel.getUser()

        binding.backArrow.setSafeOnClickListener {
            navController.navigateUp()
        }

        binding.reminder.setSafeOnClickListener {
            navigateToReminderListFragment()
        }

        binding.inviteFriend.setSafeOnClickListener {
            navigateToReferralFragment()
        }

        binding.upgrade.setSafeOnClickListener {
            openUpgradeFragment()
        }

        binding.jeevaWork.setSafeOnClickListener {
            openHowJeevaWorksPage()
        }

        binding.feedback.setSafeOnClickListener {
            showFeedbackDialog()
        }

        binding.signOut.setSafeOnClickListener {
            showSignOutConfirmDialog()
        }
    }

    private fun showFeedbackDialog() {
        val action =
            ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToFeedbackDialogFragment()
        navController.navigate(action)
    }

    private fun openUpgradeFragment() {
        if (profileViewModel.userData.value?.peekContent()?.isUpgradedPro == true) {
            showLongToast("Already a PRO User")
        } else {
            val action =
                ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToUpgradeToProFragment()
            navController.navigate(action)
        }
    }

    private fun showSignOutConfirmDialog() {
        val action =
            ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToSignOutDialogFragment()
        navController.navigate(action)
    }

    private fun navigateToReferralFragment() {
        val action =
            ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToInviteFriendFragment()
        navController.navigate(action)
    }

    private fun openHowJeevaWorksPage() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.intealth.app/"))
        startActivity(intent)
    }

    private fun navigateToReminderListFragment() {
        val action =
            ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToReminderListFragment()
        navController.navigate(action)
    }
}