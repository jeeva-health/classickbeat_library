package ai.heart.classickbeatslib.model.entity


data class PPGEntity(

    @Transient
    val id: Long = -1,

    val localId: Int = -1,

    val rMeans: List<Float>? = null,

    val gMeans: List<Float>? = null,

    val bMeans: List<Float>? = null,

    val cameraTimeStamps: List<Long>? = null,

    val xAcceleration: List<Float>? = null,

    val yAcceleration: List<Float>? = null,

    val zAcceleration: List<Float>? = null,

    val accelerationTimestamp: List<Long>? = null,

    val filteredRMeans: List<Double>? = null,

    val hr: Float? = null,

    val sdnn: Float? = null,

    val meanNN: Float? = null,

    val rmssd: Float? = null,

    val pnn50: Float? = null,

    val ln: Float? = null,

    val quality: Float? = null,

    val bAgeBin: Int? = null,

    val activeStars: Int? = null,

    val stressLevel: Int? = null,

    val sleepRating: Int? = null,

    val moodRating: Int? = null,

    val healthRating: Int? = null,

    val scanState: String? = null,

    val timeStamp: String? = null,

    val ppgCount: Int? = null,

    val ppgDistinctDays: Int? = null,

    val isBaselineSet: Boolean? = null,

    val isCalculationComplete: Boolean? = null,

    val lifeStyleCategory: Int? = null,

    val isSaved: Boolean? = true,

    val heartAgeClassification: Int? = null,

    ) : BaseLogEntity(LogType.PPG)
