package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentProfileHomeBinding
import ai.heart.classickbeats.model.WeightUnits
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.shared.util.computeAge
import ai.heart.classickbeats.shared.util.toDate
import ai.heart.classickbeats.utils.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class ProfileHomeFragment : Fragment(R.layout.fragment_profile_home) {

    private val binding by viewBinding(FragmentProfileHomeBinding::bind)

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getUser()

        navController = findNavController()

        binding.profilePic.clipToOutline = true

        profileViewModel.userData.observe(viewLifecycleOwner, EventObserver {
            val userName = it.fullName
            val weight = it.weight.roundToInt()
            val weightUnit = when (it.weightUnit) {
                WeightUnits.KGS -> "kg"
                WeightUnits.LBS -> "lbs"
            }
            val age = it.dob.toDate()!!.computeAge()
            val heightInches = (it.height % 12).toInt()
            val heightFeet = (it.height / 12).toInt()
            binding.name.text = userName
            binding.details.text =
                "${age} yrs, ${weight} $weightUnit, ${heightFeet}ft ${heightInches}in"
        })

        profileViewModel.showLoading.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                showLoadingBar()
            } else {
                hideLoadingBar()
            }
        })

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

            ProfileHomeFragmentDirections.actionProfileHomeFragmentToFeedbackDialogFragment()
        navController.navigate(action)
    }

    private fun openUpgradeFragment() {
        if (profileViewModel.userData.value?.peekContent()?.isUpgradedPro == true) {
            showLongToast("Already a PRO User")
        } else {
            val action =
                ProfileHomeFragmentDirections.actionProfileHomeFragmentToUpgradeToProFragment()
            navController.navigate(action)
        }
    }

    private fun showSignOutConfirmDialog() {
        val action =
            ProfileHomeFragmentDirections.actionProfileHomeFragmentToSignOutDialogFragment()
        navController.navigate(action)
    }

    private fun navigateToReferralFragment() {
        val action =
            ProfileHomeFragmentDirections.actionProfileHomeFragmentToInviteFriendFragment()
        navController.navigate(action)
    }

    private fun openHowJeevaWorksPage() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.intealth.app/"))
        startActivity(intent)
    }

    private fun navigateToReminderListFragment() {
        val action =
            ProfileHomeFragmentDirections.actionProfileHomeFragmentToReminderListFragment()
        navController.navigate(action)
    }
}
