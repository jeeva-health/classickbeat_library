package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.R
import ai.heart.classickbeats.utils.setSafeOnClickListener
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import timber.log.Timber

class MediaPlayerActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_media_player)
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

        val playButton = findViewById<ImageView>(R.id.play_pause_btn)
        playButton.setSafeOnClickListener(400) {
            if (isPlaying) {
                mService.pause()
                isPlaying = false
                playButton.setImageResource(R.drawable.ic_play)
            } else {
                mService.play()
                isPlaying = true
                playButton.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }
}