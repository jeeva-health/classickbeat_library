package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentProfileSettingsBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.play.core.review.ReviewManagerFactory
import timber.log.Timber


class ProfileSettingsFragment : Fragment(R.layout.fragment_profile_settings) {

    private val binding by viewBinding(FragmentProfileSettingsBinding::bind)

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.backArrow.setSafeOnClickListener {
            navController.navigateUp()
        }

        binding.inviteFriend.setSafeOnClickListener {
            navigateToReferralFragment()
        }

        binding.jeevaWork.setSafeOnClickListener {
            showHowJeevaWorksDialog()
        }

        binding.feedback.setSafeOnClickListener {
            showFeedbackDialog()
        }

        binding.signOut.setSafeOnClickListener {
            showSignOutConfirmDialog()
        }
    }

    private fun showFeedbackDialog() {
        val manager = ReviewManagerFactory.create(requireContext())
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener { _ ->
                }
                flow.addOnFailureListener {
                    Timber.e(it)
                }
            } else {
                Timber.e(task.exception)
            }
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

    private fun showHowJeevaWorksDialog() {
        val action =
            ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToHowJeevaWorksFragment()
        navController.navigate(action)
    }
}