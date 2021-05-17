package ai.heart.classickbeats.shared.formattedinput.masks

import ai.heart.classickbeats.shared.formattedinput.Mask
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateMask : Mask() {

    private val locale = Locale.ENGLISH

    override val maskPattern: String
        get() = "## / ## / ####"

    override val returnPattern: String
        get() = "####-##-##"

    override fun getParsedText(maskedText: String): String? {
        return if (isValidToParse(maskedText)) {
            SimpleDateFormat(OUTPUT_DATE_FORMAT, locale).format(
                SimpleDateFormat(INPUT_DATE_FORMAT, locale).parse(
                    maskedText
                )
            )
        } else {
            null
        }
    }

    override fun isValidToParse(maskedText: String): Boolean {
        return try {
            with(SimpleDateFormat(INPUT_DATE_FORMAT, locale)) {
                isLenient = false
                parse(maskedText) != null && maskedText.length == maskPattern.length
            }
        } catch (parseException: ParseException) {
            false
        }
    }

    override fun filterMaskedText(maskedText: String): String {
        return maskedText.filter { it.isDigit() }
    }

    companion object {
        private const val INPUT_DATE_FORMAT = "MM / dd / yyyy"
        private const val OUTPUT_DATE_FORMAT = "yyyy-MM-dd"
    }
}