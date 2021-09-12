package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentProfileHomeBinding
import ai.heart.classickbeats.model.WeightUnits
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.shared.util.computeAge
import ai.heart.classickbeats.shared.util.toDate
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.showLoadingBar
import ai.heart.classickbeats.utils.viewBinding
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

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getUser()

        navController = findNavController()

        binding.profilePic.clipToOutline = true

        binding.settings.setSafeOnClickListener {
            navigateToProfileSettingsFragment()
        }

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
    }

    private fun navigateToProfileSettingsFragment() {
        val action =
            ProfileHomeFragmentDirections.actionProfileHomeFragmentToProfileSettingsFragment()
        navController.navigate(action)
    }
}