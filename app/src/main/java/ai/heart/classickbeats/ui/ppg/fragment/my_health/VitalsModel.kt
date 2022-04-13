package ai.heart.classickbeats.ui.ppg.fragment.my_health

class VitalsModel {
    var type: Int
    var function = -1
    var action: String? = null
    var timeStamp: String
    var reading: String? = null
    var unit: String? = null
    //recent day
    constructor(type: Int, timeStamp: String) {
        this.type = type
        this.timeStamp = timeStamp
    }

    //upcoming vitals
    constructor(type: Int, reading: String, action: String, timeStamp: String, function: Int) {
        this.type = type
        this.reading = reading
        this.function = function
        this.action = action
        this.timeStamp = timeStamp
    }

    //recent vitals
    constructor(
        type: Int,
        reading: String?,
        action: String?,
        timeStamp: String,
        unit: String?,
        function: Int
    ) {
        this.type = type
        this.function = function
        this.action = action
        this.timeStamp = timeStamp
        this.reading = reading
        this.unit = unit
    }

    companion object {
         var UPCOMING_VITALS = 0
        var RECENT_VITALS_DAY = 1
        var RECENT_VITALS = 2

        //click function
        var FUNCTION_HEART = 0
        var FUNCTION_BLOOD_PRESSURE = 1
        var FUNCTION_WEIGHT = 2
        var FUNCTION_INTAKE = 3
    }
}