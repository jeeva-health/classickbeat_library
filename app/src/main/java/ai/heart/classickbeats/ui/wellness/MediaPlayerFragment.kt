package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.FragmentMediaPlayerBinding
import ai.heart.classickbeats.model.WellnessType
import ai.heart.classickbeats.utils.setDarkStatusBar
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaPlayerFragment : Fragment(R.layout.fragment_media_player) {

    private var binding: FragmentMediaPlayerBinding? = null

    private val args: MediaPlayerFragmentArgs by navArgs()

    private lateinit var navController: NavController

    private var player: ExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDarkStatusBar()

        navController = findNavController()

        binding = FragmentMediaPlayerBinding.bind(view)

        val wellnessCategory = args.wellnessType

        binding?.apply {
            cross.setSafeOnClickListener {
                navigateUp()
            }

            when (wellnessCategory) {
                WellnessType.ANGER -> {
                    backgroundCircle1.visibility = View.VISIBLE
                    backgroundCircle2.visibility = View.VISIBLE
                    backgroundImage.visibility = View.GONE
                }
                else -> {
                    backgroundCircle1.visibility = View.GONE
                    backgroundCircle2.visibility = View.GONE
                    backgroundImage.visibility = View.VISIBLE
                    backgroundImage.setImageResource(wellnessCategory.getBackgroundImage())
                }
            }

            title.text = getString(wellnessCategory.getTitle())
        }

        initializePlayer(args.mediaUrl)
    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun initializePlayer(mediaUrl: String) {
        player = ExoPlayer.Builder(requireActivity()).build().also { exoPlayer ->
            binding?.playerControlView?.player = exoPlayer
            val mediaItem = MediaItem.fromUri(mediaUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    private fun releasePlayer() {
        player?.run {
            release()
        }
        player = null
    }

    private fun navigateUp() {
        findNavController().navigateUp()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        binding = null
    }
}