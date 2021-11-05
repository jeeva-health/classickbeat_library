package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.ActivityMediaPlayerBinding
import ai.heart.classickbeats.model.WellnessType
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import timber.log.Timber
import java.util.*

class MediaPlayerActivity : AppCompatActivity() {

    private var binding: ActivityMediaPlayerBinding? = null

    private lateinit var mService: MediaPlayerService

    private var mBound: Boolean = false

    private var isPlaying: Boolean = false

    private var isActivityResumed = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Timber.i("onServiceConnected() called")
            val binder = service as MediaPlayerService.MediaPlayerBinder
            mService = binder.getService()
            mBound = true
            mService.init()
            mService.playerPrepared.observe(this@MediaPlayerActivity, {
                if (it) {
                    binding?.progressBar?.visibility = View.GONE
                }
            })
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Timber.i("onServiceDisconnected() called")
            mBound = false
        }
    }

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
            }
            WindowInsetsCompat.CONSUMED
        }

        val mediaUrl = intent?.getStringExtra("media_url")
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
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart() called")
        Intent(this, MediaPlayerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume() called")
        isActivityResumed = true

        binding?.cross?.setSafeOnClickListener {
            finish()
        }

        val playButton = binding!!.playPauseBtn
        playButton.setSafeOnClickListener(400) {
            if (isPlaying) {
                mService.pause()
                isPlaying = false
                playButton.setImageResource(R.drawable.ic_play)
            } else {
                mService.play()
                binding?.playTxt?.visibility = View.GONE
                isPlaying = true
                playButton.setImageResource(R.drawable.ic_pause)
                startProgress()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isActivityResumed = false
    }

    private fun startProgress() {
        val totalDuration = mService.getDuration()

        val initialDelay = 400L
        val period = 400L

        val mTimer = Timer()
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                if (isActivityResumed) {
                    runOnUiThread {
                        val currentPosition = mService.getProgress() + period.toInt()
                        val progress = ((currentPosition ?: 0) * 100) / (totalDuration ?: 1)
                        Timber.i("totalDuration: $totalDuration, currentPosition: $currentPosition, progress: $progress")
                        binding?.audioProgressBar?.setProgress(progress, true)
                        if (progress == 100) {
                            finish()
                        }
                    }
                }
            }
        }, initialDelay, period)
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }
}