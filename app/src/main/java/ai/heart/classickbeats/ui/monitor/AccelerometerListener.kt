package ai.heart.classickbeats.ui.monitor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import timber.log.Timber
import kotlin.math.sqrt

class AccelerometerListener(private val accelerationHandler: () -> Unit) : SensorEventListener {

    private var mAccel = 0f
    private var mAccelCurrent = 0f
    private var mAccelLast = 0f


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val mGravity = event.values.clone()
            val x: Float = mGravity[0]
            val y: Float = mGravity[1]
            val z: Float = mGravity[2]
            mAccelLast = mAccelCurrent
            mAccelCurrent = sqrt(x * x + y * y + z * z)
            val delta: Float = mAccelCurrent - mAccelLast
            mAccel = mAccel * 0.9f + delta
            if (mAccel > 3) {
                Timber.i("acceleration: $mAccel")
                accelerationHandler.invoke()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}