package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentMediaPlayerBinding
import ai.heart.classickbeats.model.WellnessType
import ai.heart.classickbeats.utils.setSafeOnClickListener
import ai.heart.classickbeats.utils.viewBinding
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MediaPlayerFragment : Fragment(R.layout.fragment_media_player) {

    private val binding by viewBinding(FragmentMediaPlayerBinding::bind)

    private lateinit var navController: NavController

    private val wellnessViewModel: WellnessViewModel by activityViewModels()

    private var serviceIntent: Intent? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playPauseBtn.setSafeOnClickListener(500) {
            startService()
        }
    }

    private fun startService() {
        serviceIntent = Intent(requireActivity(), MediaPlayerService::class.java)
        serviceIntent?.putExtra("wellnessType", WellnessType.SLEEP)
        serviceIntent?.putExtra("mediaUrl", "")
        requireActivity().startService(serviceIntent)
    }

    override fun onDestroyView() {
        requireActivity().stopService(serviceIntent)
        super.onDestroyView()
    }
}