package ai.heart.classickbeats.ui.wellness

import ai.heart.classickbeats.MainActivity
import ai.heart.classickbeats.R
import ai.heart.classickbeats.model.Constants
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import timber.log.Timber

class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private var mediaPlayer: MediaPlayer? = null

    private var wifiLock: WifiManager.WifiLock? = null

    private val binder: IBinder = MediaPlayerBinder()

    inner class MediaPlayerBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setOnPreparedListener(this@MediaPlayerService)
            setOnErrorListener(this@MediaPlayerService)
            setDataSource("https://public-sound.s3.ap-south-1.amazonaws.com/public.mp3")
            prepareAsync()
        }
    }

    fun setSource(sourceUri: String) {
        mediaPlayer?.apply {
            setDataSource(sourceUri)
            prepareAsync()
        }
    }

    private fun acquireWifiLock(context: Context) {
        val wifiManager =
            context.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock")
        wifiLock?.acquire()
    }

    private fun releaseWifiLock() {
        wifiLock?.release()
    }

    override fun onCreate() {
        super.onCreate()

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }
        val notification: Notification =
            NotificationCompat.Builder(this, Constants.PLAYBACK_CHANNEL_ID)
                .setContentTitle(getText(R.string.meditation_notification_title))
                .setContentText(getText(R.string.meditation_notification_message))
                .setSmallIcon(R.drawable.ic_clock)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.meditation_ticker_text))
                .build()

        startForeground(Constants.PLAYBACK__NOTIFICATION_ID, notification)
        Timber.i("startForeground called")
    }

    fun init() {
        initMediaPlayer()
        acquireWifiLock(applicationContext)
    }

    fun getDuration() = mediaPlayer?.duration

    fun getProgress() = mediaPlayer?.currentPosition

    fun play() {
        Timber.i("play() called")
        mediaPlayer?.start()
    }

    fun pause() {
        Timber.i("pause() called")
        mediaPlayer?.pause()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onPrepared(p0: MediaPlayer?) {
        Timber.i("onPrepared() called")
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        Timber.e("onError() WHAT: $p1 EXTRA: $p2");
        return false;
    }

    override fun onDestroy() {
        Timber.i("onDestroy() called")
        releaseWifiLock()
        mediaPlayer?.release()
        super.onDestroy()
    }
}