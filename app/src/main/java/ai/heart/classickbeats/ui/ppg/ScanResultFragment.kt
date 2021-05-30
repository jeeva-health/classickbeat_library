package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanResultBinding
import ai.heart.classickbeats.model.BioAge
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ScanResultFragment : Fragment(R.layout.fragment_scan_result) {

    private val binding by viewBinding(FragmentScanResultBinding::bind)

    private lateinit var navController: NavController

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val scanResult = monitorViewModel.scanResult ?: throw Exception("Scan result null")
        val bioAge = BioAge.values()[scanResult.ageBin].displayStr

        binding.apply {
            heartRate.text = scanResult.hrv.toInt().toString()
            ageRange.text = bioAge

        }
    }
}
