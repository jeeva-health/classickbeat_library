package ai.heart.classickbeats.shared.formattedinput

interface MaskCreator {
    fun create(maskPattern: String? = null, returnPattern: String? = null): Mask
}