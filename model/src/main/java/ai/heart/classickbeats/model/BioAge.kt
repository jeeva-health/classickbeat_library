package ai.heart.classickbeats.model

enum class BioAge(val startRange: Int = -1, val endRange: Int = -1) {
    Range1(startRange = 15, endRange = 25),
    Range2(startRange = 25, endRange = 35),
    Range3(startRange = 35, endRange = 45),
    Range4(startRange = 45, endRange = 55),
    Range5(startRange = 55, endRange = 65),
    Range6(startRange = 65)
}

fun BioAge.displayString() = when {
    startRange == -1 -> "<$endRange years"
    endRange == -1 -> "$startRange+ years"
    else -> "$startRange-$endRange years"
}