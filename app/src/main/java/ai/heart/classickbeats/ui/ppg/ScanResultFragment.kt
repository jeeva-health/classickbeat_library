package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanResultBinding
import ai.heart.classickbeats.graph.LineGraph
import ai.heart.classickbeats.model.BioAge
import ai.heart.classickbeats.model.displayString
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ScanResultFragment : Fragment(R.layout.fragment_scan_result) {

    private val binding by viewBinding(FragmentScanResultBinding::bind)

    private lateinit var navController: NavController

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private lateinit var waveformChart: LineChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        waveformChart = binding.waveformChart.apply {
            setDrawGridBackground(false)
            description.isEnabled = false
            legend.isEnabled = false
            setNoDataText("")
            invalidate()
        }

        val scanResult = monitorViewModel.scanResult ?: throw Exception("Scan result null")
        val bioAgeIndex = scanResult.ageBin
        val bioAge = BioAge.values()[bioAgeIndex]
        val bioAgeInt = monitorViewModel.userAge ?: bioAge.startRange
        val bioAgeInfo = when {
            bioAgeInt < bioAge.startRange -> getString(R.string.bio_age_more)
            bioAgeInt > bioAge.endRange -> getString(R.string.bio_age_less)
            else -> getString(R.string.bio_age_same)
        }

        val activeStarCount = scanResult.activeStar
        val lifeStyleText =
            if (scanResult.isActive) getString(R.string.active) else getString(R.string.sedentary)
        val lifeStyleInfoText = if (scanResult.isActive)
            getString(R.string.active_lifestyle)
        else
            getString(R.string.sedentary_lifestyle)

        binding.apply {
            val ageClockList =
                listOf(ageClock1, ageClock2, ageClock3, ageClock4, ageClock5, ageClock6)
            val lifeStyleList =
                listOf(lifestyle1, lifestyle2, lifestyle3, lifestyle4, lifestyle5, lifestyle6)

            heartRate.text = scanResult.bpm.toInt().toString()

            ageRange.text = bioAge.displayString()
            ageInfo.text = bioAgeInfo
            for (i in 1 until (bioAgeIndex + 1)) {
                ageClockList[i].setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bright_blue
                    ), PorterDuff.Mode.SRC_IN
                )
            }

            lifestyle.text = lifeStyleText
            lifestyleInfo.text = lifeStyleInfoText
            for (i in 1 until activeStarCount) {
                lifeStyleList[i].setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bright_red_2
                    ), PorterDuff.Mode.SRC_IN
                )
            }

            LineGraph.drawLineGraph(waveformChart, monitorViewModel.leveledSignal!!.toList())

            sdnn.text = "${scanResult.sdnn.toInt()} ms"
            pnn.text = "${scanResult.pnn50.toInt()} %"
            mssd.text = "${scanResult.rmssd.toInt()} ms"

            saveBtn.setSafeOnClickListener {
                navigateToHistoryFragment()
            }
        }
    }

    private fun navigateToHistoryFragment() {
        val action =
            ScanResultFragmentDirections.actionScanResultFragmentToHistoryHomeFragment()
        navController.navigate(action)
    }
}