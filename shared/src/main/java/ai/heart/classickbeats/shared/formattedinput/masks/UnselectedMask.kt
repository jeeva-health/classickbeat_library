package ai.heart.classickbeats.shared.formattedinput.masks

import ai.heart.classickbeats.shared.formattedinput.Mask

class UnselectedMask : Mask() {
    override val maskPattern: String
        get() = ""
    override val returnPattern: String
        get() = ""

    override fun getParsedText(maskedText: String): String? = null
    override fun isValidToParse(maskedText: String) = false
    override fun filterMaskedText(maskedText: String) = ""
}