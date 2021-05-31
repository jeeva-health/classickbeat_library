package ai.heart.classickbeats.model

enum class Gender(val displayStr: String, val valStr: String) {
    MALE("Male", "M"),
    FEMALE("Female", "F"),
    OTHERS("Others", "O")
}

fun String.stringToGender() = when (this) {
    "M" -> Gender.MALE
    "F" -> Gender.FEMALE
    else -> Gender.OTHERS
}