package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentInviteFriendBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InviteFriendFragment : Fragment(R.layout.fragment_invite_friend) {

    private val binding by viewBinding(FragmentInviteFriendBinding::bind)

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val (refereeReward, referrerReward) = profileViewModel.getReferralGemReward()

        binding.referralMessage.text = getString(R.string.earn_gems_message_1, refereeReward)

        binding.referralMessage2.text =
            getString(R.string.earn_gems_message_2, refereeReward, referrerReward)

        binding.cross.setSafeOnClickListener {
            navController.navigateUp()
        }

        binding.inviteFriendButton.setSafeOnClickListener {
            val inviteMessage = getString(
                R.string.invite_message,
                "https://play.google.com/store/apps/details?id=ai.heart.classickbeats"
            )
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, inviteMessage)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }
}