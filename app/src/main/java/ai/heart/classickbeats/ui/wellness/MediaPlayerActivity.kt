package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.databinding.ActivityMediaPlayerBinding
import ai.heart.classickbeats.model.WellnessType
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import timber.log.Timber


class MediaPlayerActivity : AppCompatActivity() {

    private var binding: ActivityMediaPlayerBinding? = null

    private var player: ExoPlayer? = null

    private var isActivityResumed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        ViewCompat.setOnApplyWindowInsetsListener(binding?.rootLayout!!) { rootLayout: View, windowInsets: WindowInsetsCompat ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            rootLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                topMargin = 0
                rightMargin = insets.right
                bottomMargin = insets.bottom
            }
            WindowInsetsCompat.CONSUMED
        }

        val wellnessCategory: WellnessType =
            intent?.getSerializableExtra("wellness_category") as WellnessType? ?: WellnessType.SLEEP

        binding?.apply {
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

//            seekBackward.isEnabled = false
        }
    }

    override fun onStart() {
        super.onStart()

        val mediaUrl = intent?.getStringExtra("media_url") ?: throw Exception("media_url required")

        initializePlayer(mediaUrl)
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume() called")
        isActivityResumed = true

        binding?.cross?.setSafeOnClickListener {
            finish()
        }

//        val playButton = binding!!.playPauseBtn
//        playButton.setSafeOnClickListener(400) {
//            if (isPlaying) {
//                isPlaying = false
//                player?.pause()
//                playButton.setImageResource(R.drawable.ic_play)
//            } else {
//                binding?.playTxt?.visibility = View.GONE
//                isPlaying = true
//                player?.play()
//                playButton.setImageResource(R.drawable.ic_pause)
//            }
//        }
    }

    private fun initializePlayer(mediaUrl: String) {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
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

    override fun onStop() {
        super.onStop()

        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}
