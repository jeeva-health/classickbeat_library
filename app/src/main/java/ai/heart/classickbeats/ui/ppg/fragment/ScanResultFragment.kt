package ai.heart.classickbeats.ui.ppg.fragment

import ai.heart.classickbeats.NavHomeDirections
import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanResultBinding
import ai.heart.classickbeats.graph.LineGraph
import ai.heart.classickbeats.model.BioAge
import ai.heart.classickbeats.model.PPGData
import ai.heart.classickbeats.model.displayString
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.shared.util.toOrdinalFormattedDateString
import ai.heart.classickbeats.shared.util.toTimeString
import ai.heart.classickbeats.ui.common.ConfirmationViewModel
import ai.heart.classickbeats.ui.ppg.viewmodel.ScanResultViewModel
import ai.heart.classickbeats.ui.profile.ProfileHomeFragmentDirections
import ai.heart.classickbeats.utils.*
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
import androidx.fragment.app.activityViewModels
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

    private val confirmationDialogViewModel: ConfirmationViewModel by activityViewModels()

    private val args: ScanResultFragmentArgs by navArgs()

    private lateinit var waveformChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (scanId: Long, _) = args
        scanResultViewModel.getScanDetail(scanId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val (scanId: Long, isShowingHistory: Boolean) = args

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

        binding.backArrow.setSafeOnClickListener {
            navigateBack()
        }

        scanResultViewModel.scanDetails.observe(viewLifecycleOwner, EventObserver {
            showUi(it, scanId, isShowingHistory)
        })

        scanResultViewModel.showLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                scanResultViewModel.apiError?.let { showLongToast(it) }
            }
        })

        scanResultViewModel.scanDetails.value?.let {
            showUi(
                it.peekContent(),
                scanId,
                isShowingHistory
            )
        }

        confirmationDialogViewModel.positiveEvent.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                navigateToScanFragment()
                scanResultViewModel.submitDiscardRequest(scanId)
            }
        })

        confirmationDialogViewModel.negativeEvent.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                confirmationDialogViewModel.dismiss()
            }
        })
    }

    private fun showUi(scanResult: PPGData.ScanResult, scanId: Long, isShowingHistory: Boolean) {
        val bioAgeIndex = scanResult.ageBin
        val bioAge = BioAge.values()[bioAgeIndex]
        val bioAgeInfo: SpannableString = when (scanResult.bioAgeResult) {
            1 -> setBoldSpan(SpannableString.valueOf(getString(R.string.bio_age_more)), 91, 102)
            -1 -> setBoldSpan(SpannableString.valueOf(getString(R.string.bio_age_less)), 92, 103)
            else -> setBoldSpan(SpannableString.valueOf(getString(R.string.bio_age_same)), 91, 102)
        }

        val activeStarCount = scanResult.activeStar
        val lifeStyleText = scanResult.lifestyleCategory.toLifestyleText(requireContext())
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

            if (scanResult.isBaselineSet) {
                val stress = scanResult.stress
                when (stress.stressResult) {
                    1 -> {
                        stressDrawableInt = R.drawable.graph_less_stress
                        stressSpannableString = SpannableString(getString(R.string.less_stress_msg))
                        setStressMessageSpan(
                            stressSpannableString,
                            8,
                            21,
                            getContextColor(R.color.moderate_green_2)
                        )
                        stressTag.text = getString(R.string.low_stress)
                        stressTag.backgroundTintList =
                            ColorStateList.valueOf(getContextColor(R.color.moderate_green_2))
                    }
                    2 -> {
                        stressDrawableInt = R.drawable.graph_medium_stress
                        stressSpannableString =
                            SpannableString(getString(R.string.normal_stress_msg))
                        setStressMessageSpan(
                            stressSpannableString,
                            8,
                            16,
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
                            8,
                            21,
                            getContextColor(R.color.bright_red_3)
                        )
                        stressTag.text = getString(R.string.high_stress)
                        stressTag.backgroundTintList =
                            ColorStateList.valueOf(getContextColor(R.color.bright_red_3))
                    }
                }
            } else {
                stressTag.visibility = View.GONE
                stressGraphCard.visibility = View.GONE
                stressInsufficientCard.visibility = View.VISIBLE
                val completedScanCount = scanResult.stress.dataCount
                val scanTargetCount = scanResult.stress.targetDataCount
                val completedDistinctScanCount = scanResult.stress.distinctDataCount
                val targetDistinctCount = scanResult.stress.targetDistinctDataCount
                val scanCount = "$completedScanCount/$scanTargetCount " + getString(R.string.scans)
                val dayCount =
                    "$completedDistinctScanCount/$targetDistinctCount " + getString(R.string.days)
                progressText.text = scanCount
                progressText2.text = dayCount
                stressProgress.setProgress(completedScanCount, true)
                stressProgress2.setProgress(completedDistinctScanCount, true)
            }

            stressGraph.setImageResource(stressDrawableInt)
            stressMessage.text = stressSpannableString

            discardBtn.setSafeOnClickListener {
                showDiscardConfirmDialog()
            }

            saveBtn.setSafeOnClickListener {
                navigateToTimelineFragment()
            }

            if (isShowingHistory) {
                backArrow.visibility = View.VISIBLE
                stressInsufficientCard.visibility = View.GONE
                discardBtn.visibility = View.GONE
                saveBtn.visibility = View.GONE
            } else {
                backArrow.visibility = View.GONE
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

    private fun navigateToScanFragment() {
        val action = NavHomeDirections.actionGlobalScanFragment()
        navController.navigate(action)
    }

    private fun navigateToTimelineFragment() {
        val action =
            ScanResultFragmentDirections.actionScanResultFragmentToTimelineFragment()
        navController.navigate(action)
    }

    private fun navigateBack() {
        navController.navigateUp()
    }

    private fun showDiscardConfirmDialog() {
        val title = getString(R.string.discard_title)
        val negativeKey = getString(R.string.no)
        val positiveKey = getString(R.string.yes)
        val action = ProfileHomeFragmentDirections.actionGlobalConfirmationDialogFragment(
            title = title,
            negativeKey = negativeKey,
            positiveKey = positiveKey
        )
        navController.navigate(action)
    }
}
