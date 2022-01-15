package ai.heart.classickbeats.ui.profile

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentUpgradeToProBinding
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UpgradeToProFragment : Fragment(R.layout.fragment_upgrade_to_pro) {

    private val binding by viewBinding(FragmentUpgradeToProBinding::bind)

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        profileViewModel.getUser()

        val (yearlyCharges, yearlyChargesPerMonth, monthlyCharges) = profileViewModel.getProPricing()

        binding.apply {
            yearlyPrice.text = getString(R.string.rupee_amount, yearlyCharges.toString())
            yearlyPerMonthPrice.text =
                getString(R.string.rupee_amount, yearlyChargesPerMonth.toString())
            monthlyPrice.text = getString(R.string.rupee_amount, monthlyCharges.toString())
        }
    }
}