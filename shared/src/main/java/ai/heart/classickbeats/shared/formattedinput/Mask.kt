package ai.heart.classickbeats.shared.formattedinput

import ai.heart.classickbeats.shared.formattedinput.masks.*

abstract class Mask {
    abstract val maskPattern: String
    abstract val returnPattern: String

    abstract fun getParsedText(maskedText: String): String?
    abstract fun isValidToParse(maskedText: String): Boolean
    abstract fun filterMaskedText(maskedText: String): String

    // Enum class order must be the same as attrs.xml order
    enum class Type : MaskCreator {
        DATE {
            override fun create(maskPattern: String?, returnPattern: String?): Mask =
                DateMask()
        },
        PHONE {
            override fun create(maskPattern: String?, returnPattern: String?): Mask =
                PhoneMask()
        },
        HEIGHT {
            override fun create(maskPattern: String?, returnPattern: String?): Mask =
                HeightMask()
        },
        CUSTOM {
            override fun create(maskPattern: String?, returnPattern: String?): Mask =
                CustomMask(
                    maskPattern.orEmpty(),
                    returnPattern.orEmpty()
                )
        },
        UNSELECTED {
            override fun create(maskPattern: String?, returnPattern: String?): Mask =
                UnselectedMask()
        }
    }
}