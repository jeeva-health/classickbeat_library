package ai.heart.classickbeats.ui.ppg.fragment

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanResultBinding
import ai.heart.classickbeats.graph.LineGraph
import ai.heart.classickbeats.model.BioAge
import ai.heart.classickbeats.model.PPGData
import ai.heart.classickbeats.model.displayString
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.shared.util.toOrdinalFormattedDateString
import ai.heart.classickbeats.shared.util.toTimeString
import ai.heart.classickbeats.ui.ppg.viewmodel.ScanResultViewModel
import ai.heart.classickbeats.utils.getContextColor
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import com.github.mikephil.charting.charts.LineChart
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ScanResultFragment : Fragment(R.layout.fragment_scan_result) {

    private val binding by viewBinding(FragmentScanResultBinding::bind)

    private lateinit var navController: NavController

    private val scanResultViewModel: ScanResultViewModel by viewModels()

    private val args: ScanResultFragmentArgs by navArgs()

    private lateinit var waveformChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (_, scanId) = args
        scanResultViewModel.getScanDetail(scanId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val (isShowingHistory, _) = args

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
            requestLayout()
        }

        scanResultViewModel.scanDetails.observe(viewLifecycleOwner, EventObserver {
            showUi(it, isShowingHistory)
        })

        scanResultViewModel.scanDetails.value?.let { showUi(it.peekContent(), isShowingHistory) }
    }

    private fun showUi(scanResult: PPGData.ScanResult, isShowingHistory: Boolean) {
        val bioAgeIndex = scanResult.ageBin
        val bioAge = BioAge.values()[bioAgeIndex]
        val bioAgeInfo: SpannableString = when (scanResult.bioAgeResult) {
            1 -> setBoldSpan(SpannableString.valueOf(getString(R.string.bio_age_more)), 91, 102)
            -1 -> setBoldSpan(SpannableString.valueOf(getString(R.string.bio_age_less)), 92, 103)
            else -> setBoldSpan(SpannableString.valueOf(getString(R.string.bio_age_same)), 91, 102)
        }

        val activeStarCount = scanResult.activeStar
        val lifeStyleText =
            if (scanResult.isActive) getString(R.string.active) else getString(R.string.sedentary)
        val lifeStyleInfoText = if (scanResult.isActive)
            setBoldSpan(SpannableString.valueOf(getString(R.string.active_lifestyle)), 93, 104)
        else
            setBoldSpan(SpannableString.valueOf(getString(R.string.sedentary_lifestyle)), 93, 104)

        val dateStr = scanResult.timeStamp.toOrdinalFormattedDateString()
        val timeStr = scanResult.timeStamp.toTimeString()

        binding.apply {
            val ageClockList =
                listOf(ageClock1, ageClock2, ageClock3, ageClock4, ageClock5, ageClock6)
            val lifeStyleList =
                listOf(lifestyle1, lifestyle2, lifestyle3, lifestyle4, lifestyle5, lifestyle6)

            heartRate.text = scanResult.bpm.toInt().toString()

            date.text = dateStr
            time.text = timeStr

            ageRange.text = bioAge.displayString()
            ageInfo.text = bioAgeInfo
            for (i in 1 until (bioAgeIndex + 1)) {
                ageClockList[i].setColorFilter(
                    getContextColor(R.color.bright_blue), PorterDuff.Mode.SRC_IN
                )
            }

            lifestyle.text = lifeStyleText
            lifestyleInfo.text = lifeStyleInfoText
            for (i in 1 until activeStarCount) {
                lifeStyleList[i].setColorFilter(
                    getContextColor(R.color.bright_red_2), PorterDuff.Mode.SRC_IN
                )
            }

            LineGraph.draw(waveformChart, scanResult.filteredRMean)

            val sdnnVal = scanResult.sdnn.toInt()
            sdnn.text = "$sdnnVal ms"
            if (sdnnVal < 30 || sdnnVal > 96) {
                sdnn.backgroundTintList =
                    ColorStateList.valueOf(getContextColor(R.color.bright_red_3))
            }

            val pnnVal = scanResult.pnn50.toInt()
            pnn.text = "$pnnVal %"
            if (pnnVal < 5 || pnnVal > 40) {
                pnn.backgroundTintList =
                    ColorStateList.valueOf(getContextColor(R.color.bright_red_3))
            }

            val mssdVal = scanResult.rmssd.toInt()
            mssd.text = "$mssdVal ms"
            if (mssdVal < 30 || mssdVal > 96) {
                mssd.backgroundTintList =
                    ColorStateList.valueOf(getContextColor(R.color.bright_red_3))
            }

            val scanQuality = scanResult.quality
            quality.text = "Quality: ${scanQuality.toInt()} %"

            var stressDrawableInt = 0
            var stressSpannableString: SpannableString? = null

            when (scanResult.stress.stressResult) {
                1 -> {
                    stressDrawableInt = R.drawable.graph_less_stress
                    stressSpannableString = SpannableString(getString(R.string.less_stress_msg))
                    setStressMessageSpan(
                        stressSpannableString,
                        9,
                        22,
                        getContextColor(R.color.moderate_green_2)
                    )
                    stressTag.text = getString(R.string.low_stress)
                    stressTag.backgroundTintList =
                        ColorStateList.valueOf(getContextColor(R.color.moderate_green_2))
                }
                2 -> {
                    stressDrawableInt = R.drawable.graph_medium_stress
                    stressSpannableString = SpannableString(getString(R.string.normal_stress_msg))
                    setStressMessageSpan(
                        stressSpannableString,
                        9,
                        17,
                        getContextColor(R.color.vivid_yellow)
                    )
                    stressTag.text = getString(R.string.normal_stress)
                    stressTag.backgroundTintList =
                        ColorStateList.valueOf(getContextColor(R.color.vivid_yellow))
                }
                3 -> {
                    stressDrawableInt = R.drawable.graph_high_stress
                    stressSpannableString = SpannableString(getString(R.string.high_stress_msg))
                    setStressMessageSpan(
                        stressSpannableString,
                        9,
                        22,
                        getContextColor(R.color.bright_red_3)
                    )
                    stressTag.text = getString(R.string.high_stress)
                    stressTag.backgroundTintList =
                        ColorStateList.valueOf(getContextColor(R.color.bright_red_3))
                }
                else -> {
                    stressTag.visibility = View.GONE
                    stressGraphCard.visibility = View.GONE
                    stressInsufficientCard.visibility = View.VISIBLE
                    val completedScanCount = scanResult.stress.dataCount
                    val scanTargetCount = scanResult.stress.targetDataCount
                    progressText.text = "$completedScanCount/$scanTargetCount"
                    stressProgress.setProgress(completedScanCount, true)
                }
            }

            stressGraph.setImageResource(stressDrawableInt)
            stressMessage.text = stressSpannableString

            saveBtn.setSafeOnClickListener {
                navigateToHistoryFragment()
            }

            if (isShowingHistory) {
                stressInsufficientCard.visibility = View.GONE
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

    private fun setBoldSpan(message: SpannableString, startPos: Int, endPos: Int): SpannableString {
        message.setSpan(
            StyleSpan(Typeface.BOLD),
            startPos,
            endPos,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        return message
    }

    private fun navigateToHistoryFragment() {
        val action =
            ScanResultFragmentDirections.actionScanResultFragmentToTimelineFragment()
        navController.navigate(action)
    }
}
