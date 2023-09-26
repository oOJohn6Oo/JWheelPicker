package io.john6.johnbase.compose.picker.dialog.date

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import io.john6.johnbase.compose.picker.DatePickerMode
import io.john6.johnbase.compose.picker.JDatePickerHelper.toEpochMilli
import io.john6.johnbase.compose.picker.JPickerOverlayStyle
import io.john6.johnbase.compose.picker.TimePickerMode
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

/**
 * Required data for JMultiPickerDialog
 *
 * @param title title of picker dialog
 * @param overlayStyle 0 for oval rectangle, 1 for line
 * @param containerHorizontalPaddingInDp add padding to container's horizontal side
 * @param datePickerMode decide how year-month-day shows, must be a const value of [DatePickerMode]
 * @param timePickerMode decide how hour-minute-second shows, must be a const value of [TimePickerMode]
 * @param startDateInMillis picker's start time in millis
 * @param endDateInMillis picker's end time in millis
 * @param initialSelectedDateInMillis picker's initial select time
 *
 * TODO add params to custom date display text
 */
@Parcelize
@RequiresApi(Build.VERSION_CODES.O)
data class JDatePickerDialogData(
    val title: Pair<Int, String> = 0 to "",
    @JPickerOverlayStyle
    val overlayStyle: Int = 0,
    val containerHorizontalPaddingInDp:Int = 0,
    @DatePickerMode val datePickerMode: Int = DatePickerMode.DATE_ALL,
    @TimePickerMode val timePickerMode: Int = TimePickerMode.TIME_ALL,
    val startDateInMillis: Long = 0,
    val endDateInMillis: Long = Long.MAX_VALUE,
    val initialSelectedDateInMillis: Long = LocalDateTime.now().toEpochMilli(),
) : Parcelable