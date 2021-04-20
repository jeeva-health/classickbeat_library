package ai.heart.classickbeats.media

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

class MediaPlayer {

    fun createPlayer(context: Context, url: String) {
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepareAsync()
        }
    }
}