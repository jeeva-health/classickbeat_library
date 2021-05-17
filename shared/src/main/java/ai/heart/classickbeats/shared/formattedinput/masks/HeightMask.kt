package ai.heart.classickbeats.shared.formattedinput.masks

import ai.heart.classickbeats.shared.formattedinput.Mask
import ai.heart.classickbeats.shared.formattedinput.maskers.Masker.Companion.POUND

class HeightMask : Mask() {

    override val maskPattern: String
        get() = "# ft - ## in"

    override val returnPattern: String
        get() = "##"

    override fun getParsedText(maskedText: String): String? {
        return filterMaskedText(maskedText).takeIf { isValidToParse(maskedText) }
    }

    override fun isValidToParse(maskedText: String): Boolean {
        return maskedText.length == maskPattern.length
    }

    override fun filterMaskedText(maskedText: String): String {
        val extractedList = maskedText.mapIndexed { index, char ->
            char.toString().takeIf {
                maskPattern[index] == POUND
            }.orEmpty()
        }
        val heightInches =
            extractedList[0].toInt() * 12 + extractedList[1].toInt() * 10 + extractedList[2].toInt()
        return heightInches.toString()
    }
}