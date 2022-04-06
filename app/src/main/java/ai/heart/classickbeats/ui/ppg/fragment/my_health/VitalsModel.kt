package ai.heart.classickbeats.ui.ppg.fragment.my_health

class VitalsModel {
    private var type: Int
    private var function = -1
    private var action: String? = null
    private var timeStamp: String
    private var value: String? = null
    private var unit: String? = null

    constructor(type: Int, timeStamp: String) {
        this.type = type
        this.timeStamp = timeStamp
    }

    constructor(type: Int, action: String?, timeStamp: String, function: Int) {
        this.type = type
        this.function = function
        this.action = action
        this.timeStamp = timeStamp
    }

    constructor(type: Int, action: String?, timeStamp: String, value: String?, unit: String?, function: Int) {
        this.type = type
        this.function = function
        this.action = action
        this.timeStamp = timeStamp
        this.value = value
        this.unit = unit
    }

    companion object {
        var UPCOMING_VITALS = 0
        var RECENT_VITALS_DAY = 1
        var RECENT_VITALS = 2
        var FUNCTION_HEART = 0
        var FUNCTION_BLOOD_PRESSURE = 1
        var FUNCTION_WEIGHT = 2
        var FUNCTION_INTAKE = 3
    }
}