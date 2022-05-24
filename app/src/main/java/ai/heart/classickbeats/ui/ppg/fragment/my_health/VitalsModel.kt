package ai.heart.classickbeats.ui.ppg.fragment.my_health

class VitalsModel {
    var type: Int = -1
    var icon: Int = 0
    var name: String? = null
    var reading: String? = null
    var unit: String? = null
    var timeStamp: String
    var action: String? = null
    var function = -1

    //recent day
    constructor(type: Int, timeStamp: String) {
        this.type = type
        this.timeStamp = timeStamp
    }

    //upcoming vitals


    //recent vitals
    constructor(
        type: Int,
        icon: Int,
        name: String?,
        reading: String?,
        unit: String?,
        timeStamp: String,
        function: Int
    ) {
        this.type = type
        this.icon = icon
        this.name = name
        this.reading = reading
        this.unit = unit
        this.timeStamp = timeStamp
        this.function = function
    }
    constructor(
        type: Int,
        icon: Int,
        name: String?,
        timeStamp: String,
        action: String?,
        function: Int
    ) {
        this.type = type
        this.icon = icon
        this.name = name
        this.timeStamp = timeStamp
        this.action = action
        this.function = function
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