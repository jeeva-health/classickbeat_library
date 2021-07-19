package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHistoryHomeBinding
import ai.heart.classickbeats.model.BioAge
import ai.heart.classickbeats.model.LogType
import ai.heart.classickbeats.model.PPGData
import ai.heart.classickbeats.model.StressResult
import ai.heart.classickbeats.model.entity.BaseLogEntity
import ai.heart.classickbeats.model.entity.PPGEntity
import ai.heart.classickbeats.shared.result.EventObserver
import ai.heart.classickbeats.shared.util.computeAge
import ai.heart.classickbeats.shared.util.toDate
import ai.heart.classickbeats.utils.hideLoadingBar
import ai.heart.classickbeats.utils.showLoadingBar
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class HistoryHomeFragment : Fragment(R.layout.fragment_history_home) {

    private val binding by viewBinding(FragmentHistoryHomeBinding::bind)

    private val historyViewModel: HistoryViewModel by activityViewModels()

    private lateinit var navController: NavController

    private lateinit var historyAdapter: HistoryAdapter

    private var userAge: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        historyViewModel.getUser()

        historyViewModel.getHistoryData()

        historyAdapter = HistoryAdapter(requireContext(), historyItemClickListener)

        binding.historyRv.apply {
            adapter = historyAdapter
        }

        historyViewModel.refreshData.observe(viewLifecycleOwner, {
            historyAdapter.submitList(historyViewModel.historyData)
            historyAdapter.notifyDataSetChanged()
        })

        historyViewModel.userData.observe(viewLifecycleOwner, EventObserver {
            userAge = it.dob.toDate()?.computeAge() ?: -1
        })

        historyViewModel.showLoading.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                showLoadingBar()
            } else {
                hideLoadingBar()
            }
        })
    }

    private val historyItemClickListener = fun(data: BaseLogEntity) {
        when (data.type) {
            LogType.BloodPressure -> {
            }
            LogType.GlucoseLevel -> {
            }
            LogType.WaterIntake -> {
            }
            LogType.Weight -> {
            }
            LogType.Medicine -> {
            }
            LogType.PPG -> {
                val ppgEntity = data as PPGEntity

                val bAgeBin = ppgEntity.bAgeBin ?: 0
                val bioAge = BioAge.values()[bAgeBin]
                val bioAgeResult = if (userAge != -1) {
                    when {
                        userAge < bioAge.startRange -> -1
                        userAge > bioAge.endRange -> 1
                        else -> 0
                    }
                } else {
                    0
                }

                val isActive = ppgEntity.sedRatioLog ?: 0f < 0

                val ppgScanResult = PPGData.ScanResult(
                    bpm = ppgEntity.hr ?: 0.0f,
                    aFib = "Not Detected",
                    quality = ppgEntity.quality?.toString() ?: "",
                    ageBin = ppgEntity.bAgeBin ?: 0,
                    bioAgeResult = bioAgeResult,
                    activeStar = 6 - (ppgEntity.sedStars ?: 0),
                    sdnn = ppgEntity.sdnn ?: 0.0f,
                    pnn50 = ppgEntity.pnn50 ?: 0.0f,
                    rmssd = ppgEntity.rmssd ?: 0.0f,
                    isActive = isActive,
                    stress = StressResult(dataCount = 0),
                    timeStamp = Date()
                )
            }
        }
    }
}