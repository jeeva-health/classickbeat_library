package ai.heart.classickbeats.media

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MeditationActivity : AppCompatActivity() {

    private var serviceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)
    }

    override fun onResume() {
        super.onResume()

        findViewById<MaterialButton>(R.id.play_btn).setOnClickListener {
            startService()
        }
    }

    private fun startService() {
        serviceIntent = Intent(this, MediaPlayerService::class.java)
        startService(serviceIntent)
    }

    override fun onDestroy() {
        stopService(serviceIntent)
        super.onDestroy()
    }
}