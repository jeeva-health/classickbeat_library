package ai.heart.classickbeats.ui.widgets

import ai.heart.classickbeats.model.Time
import ai.heart.classickbeats.ui.logging.LoggingViewModel
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import java.util.*


class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private val loggingViewModel: LoggingViewModel by activityViewModels()

    private val is24HourFormat: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, is24HourFormat)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val selectedTime = Time(hourOfDay, minute)
        loggingViewModel.setLogTime(selectedTime)
    }
}