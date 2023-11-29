package ai.heart.classickbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ScanBeatActivity : AppCompatActivity() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_beat)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ai.heart.classickbeats.ui.fragment.ScanFragment())
                .commitNow()
        }
    }
}