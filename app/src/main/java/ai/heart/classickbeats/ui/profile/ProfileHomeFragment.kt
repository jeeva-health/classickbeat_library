package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentProfileHomeBinding
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.shared.util.computeAge
import ai.heart.classickbeats.shared.util.toDate
import ai.heart.classickbeats.ui.login.LoginViewModel
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.showLoadingBar
import ai.heart.classickbeats.utils.viewBinding
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import kotlin.math.roundToInt


class ProfileHomeFragment : Fragment(R.layout.fragment_profile_home) {

    private val binding by viewBinding(FragmentProfileHomeBinding::bind)

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private val loginViewModel: LoginViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getUser()

        navController = findNavController()

        binding.profilePic.clipToOutline = true

        binding.logout.setSafeOnClickListener {
            loginViewModel.logoutUser()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }

        profileViewModel.userData.observe(viewLifecycleOwner, EventObserver {
            val userName = it.fullName
            val weight = it.weight.roundToInt()
            val weightUnit = if (it.isWeightKgs) "kg" else "lbs"
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
    }
}