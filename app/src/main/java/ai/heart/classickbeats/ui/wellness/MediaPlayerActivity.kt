package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.databinding.ActivityMediaPlayerBinding
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import timber.log.Timber
import java.util.*

class MediaPlayerActivity : AppCompatActivity() {

    private var binding: ActivityMediaPlayerBinding? = null

    private lateinit var mService: MediaPlayerService

    private var mBound: Boolean = false

    private var isPlaying: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Timber.i("onServiceConnected() called")
            val binder = service as MediaPlayerService.MediaPlayerBinder
            mService = binder.getService()
            mBound = true
            mService.init()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Timber.i("onServiceDisconnected() called")
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)
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

        window.statusBarColor = ContextCompat.getColor(this, R.color.very_dark_blue)

        val playButton = binding!!.playPauseBtn
        playButton.setSafeOnClickListener(400) {
            if (isPlaying) {
                mService.pause()
                isPlaying = false
                playButton.setImageResource(R.drawable.ic_play)
            } else {
                mService.play()
                isPlaying = true
                playButton.setImageResource(R.drawable.ic_pause)
                startProgress()
            }
        }
    }

    private fun startProgress() {
        val totalDuration = mService.getDuration()

        val mTimer = Timer()
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val currentPosition = mService.getProgress()
                    val progress = ((currentPosition ?: 0) * 100) / (totalDuration ?: 1)
                    Timber.i("totalDuration: $totalDuration, currentPosition: $currentPosition, progress: $progress")
                    binding?.audioProgressBar?.setProgress(progress, true)
                }
            }
        }, 0, 400)
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }
}