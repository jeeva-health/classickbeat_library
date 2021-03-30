package ai.heart.classickbeats.storage

interface PreferenceStorage {

    var onBoardingCompleted: Boolean
    var userName: String
    var userNumber: String
    var userEmail: String
}