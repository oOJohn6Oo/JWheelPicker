package io.john6.johnbase.compose.picker.dialog.date

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.john6.johnbase.compose.picker.DatePickerMode
import io.john6.johnbase.compose.picker.JDatePickerHelper.toEpochMilli
import io.john6.johnbase.compose.picker.JPickerOverlayStyle
import io.john6.johnbase.compose.picker.TimePickerMode
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class JDateWheelPickerViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val requiredData: JDatePickerDialogData = savedStateHandle.get<JDatePickerDialogData>("data")!!

    /**
     * 当前选中的 item 信息，初始为 [JWheelPickerItemInfo.EMPTY]
     */
    var currentSelectedDateTime: LocalDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(requiredData.initialSelectedDateInMillis),
        ZoneId.systemDefault()
    )
}
