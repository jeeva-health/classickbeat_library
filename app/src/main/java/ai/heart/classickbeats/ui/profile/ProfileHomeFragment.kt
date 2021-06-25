package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentProfileHomeBinding
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController


class ProfileHomeFragment : Fragment(R.layout.fragment_profile_home) {

    private val binding by viewBinding(FragmentProfileHomeBinding::bind)

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.profilePic.clipToOutline = true
    }
}