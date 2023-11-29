package ai.heart.classickbeatslib

interface MonitorCallback {


    fun onStartScanning()
    fun onEndScanning()

    fun onScanStopUnexpectedly(s: String?=null)
    fun updateHeartRate(bpm: Int?=null)
     fun scanCoordinateUpdate(pair: Pair<Int, Double>)
}