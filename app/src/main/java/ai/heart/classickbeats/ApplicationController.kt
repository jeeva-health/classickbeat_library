package ai.heart.classickbeats

import android.app.Application
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ApplicationController : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
    }
}