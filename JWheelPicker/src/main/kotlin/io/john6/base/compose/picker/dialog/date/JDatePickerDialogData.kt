package io.john6.base.compose.picker.dialog.date

import android.os.Build
import androidx.annotation.RequiresApi
import io.john6.base.compose.picker.DatePickerMode
import io.john6.base.compose.picker.JPickerOverlayStyle
import io.john6.base.compose.picker.TimePickerMode
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Required data for JMultiPickerDialog
 *
 * @param title title of picker dialog
 * @param overlayStyle 0 for oval rectangle, 1 for line
 * @param containerHorizontalPaddingInDp add padding to container's horizontal side
 * @param datePickerMode decide how year-month-day shows, must be a const value of [DatePickerMode]
 * @param timePickerMode decide how hour-minute-second shows, must be a const value of [TimePickerMode]
 * @param startLocalDateTime picker's start time
 * @param endLocalDateTime picker's end time
 * @param initialSelectDateTime picker's initial select time, default is 1970-01-01 00:00:00 at local time
 *
 * TODO add params to custom date display text
 */
@RequiresApi(Build.VERSION_CODES.O)
data class JDatePickerDialogData(
    val title: Pair<Int, String> = 0 to "",
    @JPickerOverlayStyle
    val overlayStyle: Int = 0,
    val containerHorizontalPaddingInDp: Int = 0,
    @DatePickerMode val datePickerMode: Int = DatePickerMode.DATE_ALL,
    @TimePickerMode val timePickerMode: Int = TimePickerMode.TIME_ALL,
    val startLocalDateTime: LocalDateTime = LocalDateTime.ofEpochSecond(
        0L,
        0,
        ZoneOffset.UTC
    ),
    val endLocalDateTime: LocalDateTime = LocalDateTime.MAX,
    val initialSelectDateTime: LocalDateTime = LocalDateTime.now(),
) : Serializable