package ai.heart.classickbeats.monitor

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentCameraSelectionBinding
import ai.heart.classickbeats.utils.viewBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController


class CameraSelectionFragment : Fragment(R.layout.fragment_camera_selection) {

    private val binding by viewBinding(FragmentCameraSelectionBinding::bind)

    private val monitorViewModel: MonitorViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}