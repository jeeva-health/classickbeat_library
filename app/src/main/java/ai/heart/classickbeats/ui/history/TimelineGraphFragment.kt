package ai.heart.classickbeats.ui.history

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentTimelineGraphBinding
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TimelineGraphFragment : Fragment(R.layout.fragment_timeline_graph) {

    private val binding by viewBinding(FragmentTimelineGraphBinding::bind)

    private val timelineViewModel: TimelineViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()


    }
}