package ai.heart.classickbeats.utils

import ai.heart.classickbeats.model.Date
import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.shared.util.toDbFormatString
import java.text.SimpleDateFormat
import java.util.*

object LoggingUtils {

    fun getLogTimeStampString(timeInput: Time?, dateInput: Date?): String {
        val time = timeInput ?: Time(0, 0)
        val date = dateInput ?: throw Exception("Date must be set")
        val timeStampStr =
            "${date.year}" +
                    "-${
                        String.format(
                            "%02d",
                            date.month
                        )
                    }-${
                        String.format(
                            "%02d",
                            date.day
                        )
                    } ${
                        String.format(
                            "%02d",
                            time.hourOfDay
                        )
                    }:${
                        String.format(
                            "%02d",
                            time.minute
                        )
                    }"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getDefault()
        val timeStamp = inputFormat.parse(timeStampStr)
        val timeStampUTC = Date(timeStamp.time - TimeZone.getDefault().rawOffset)
        return timeStampUTC.toDbFormatString()
    }
}
