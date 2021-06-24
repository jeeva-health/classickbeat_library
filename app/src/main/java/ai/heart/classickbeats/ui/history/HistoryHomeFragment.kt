package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentHistoryHomeBinding
import ai.heart.classickbeats.shared.result.EventObserver
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


@AndroidEntryPoint
class HistoryHomeFragment : Fragment(R.layout.fragment_history_home) {

    private val binding by viewBinding(FragmentHistoryHomeBinding::bind)

    private val historyViewModel: HistoryViewModel by activityViewModels()

    private lateinit var navController: NavController

    private lateinit var historyAdapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        historyViewModel.getHistoryData()

        historyAdapter = HistoryAdapter(requireContext())

        binding.historyRv.apply {
            adapter = historyAdapter
        }

        historyViewModel.refreshData.observe(viewLifecycleOwner, {
            historyAdapter.submitList(historyViewModel.historyData)
            historyAdapter.notifyDataSetChanged()
        })

        historyViewModel.showLoading.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                showLoadingBar()
            } else {
                hideLoadingBar()
            }
        })
    }
}