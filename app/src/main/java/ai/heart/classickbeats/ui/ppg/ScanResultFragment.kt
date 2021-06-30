package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanResultBinding
import ai.heart.classickbeats.graph.LineGraph
import ai.heart.classickbeats.model.BioAge
import ai.heart.classickbeats.model.displayString
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat.getColor
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
            axisLeft.setDrawLabels(false)
            axisLeft.setDrawAxisLine(false)
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawLabels(false)
            axisRight.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            xAxis.setDrawLabels(false)
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
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
                    getColor(
                        requireContext(),
                        R.color.bright_blue
                    ), PorterDuff.Mode.SRC_IN
                )
            }

            lifestyle.text = lifeStyleText
            lifestyleInfo.text = lifeStyleInfoText
            for (i in 1 until activeStarCount) {
                lifeStyleList[i].setColorFilter(
                    getColor(
                        requireContext(),
                        R.color.bright_red_2
                    ), PorterDuff.Mode.SRC_IN
                )
            }

            LineGraph.drawLineGraph(waveformChart, monitorViewModel.leveledSignal!!.toList())

            sdnn.text = "${scanResult.sdnn.toInt()} ms"
            pnn.text = "${scanResult.pnn50.toInt()} %"
            mssd.text = "${scanResult.rmssd.toInt()} ms"

            var stressDrawableInt = 0
            var stressSpannableString: SpannableString? = null

            when (scanResult.stress.stressResult) {
                1 -> {
                    stressDrawableInt = R.drawable.graph_less_stress
                    stressSpannableString = SpannableString(getString(R.string.less_stress_msg))
                    setStressMessageSpan(stressSpannableString, 9, 22, Color.GREEN)
                    stressTag.setBackgroundColor(getColor(requireContext(), R.color.moderate_green))
                    stressTag.text = getString(R.string.low_stress)
                }
                2 -> {
                    stressDrawableInt = R.drawable.graph_medium_stress
                    stressSpannableString = SpannableString(getString(R.string.normal_stress))
                    setStressMessageSpan(stressSpannableString, 9, 17, Color.YELLOW)
                    stressTag.setBackgroundColor(getColor(requireContext(), R.color.yellow))
                    stressTag.text = getString(R.string.normal_stress)
                }
                3 -> {
                    stressDrawableInt = R.drawable.graph_high_stress
                    stressSpannableString = SpannableString(getString(R.string.high_stress))
                    setStressMessageSpan(stressSpannableString, 9, 22, Color.RED)
                    stressTag.setBackgroundColor(getColor(requireContext(), R.color.red))
                    stressTag.text = getString(R.string.high_stress)
                }
                else -> {
                    stressTag.visibility = View.GONE
                    stressGraphCard.visibility = View.GONE
                    insufficientStressData.visibility = View.VISIBLE
                }
            }

            stressGraph.setImageResource(stressDrawableInt)
            stressMessage.setText(stressSpannableString)

            saveBtn.setSafeOnClickListener {
                navigateToHistoryFragment()
            }
        }
    }

    private fun setStressMessageSpan(
        stressSpannableString: SpannableString,
        startPos: Int,
        endPos: Int,
        colorInt: Int
    ) {
        stressSpannableString.setSpan(
            RelativeSizeSpan(1.2f),
            startPos,
            endPos,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        stressSpannableString.setSpan(
            ForegroundColorSpan(colorInt),
            startPos,
            endPos,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        stressSpannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            startPos,
            endPos,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun navigateToHistoryFragment() {
        val action =
            ScanResultFragmentDirections.actionScanResultFragmentToHistoryHomeFragment()
        navController.navigate(action)
    }
}