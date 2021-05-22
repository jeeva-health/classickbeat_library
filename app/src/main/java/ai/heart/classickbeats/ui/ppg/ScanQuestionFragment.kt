package ai.heart.classickbeats.ui.ppg

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentScanQuestionBinding
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels


class ScanQuestionFragment : Fragment(R.layout.fragment_scan_question) {

    private val binding by viewBinding(FragmentScanQuestionBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}