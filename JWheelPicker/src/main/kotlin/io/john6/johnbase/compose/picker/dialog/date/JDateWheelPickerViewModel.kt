package io.john6.johnbase.compose.picker.dialog.date

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class JDateWheelPickerViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val requiredData: JDatePickerDialogData = savedStateHandle.get<JDatePickerDialogData>("data")!!

    /**
     * 当前选中的 item 信息，初始为 [JWheelPickerItemInfo.EMPTY]
     */
    var currentSelectedDateTime: LocalDateTime = requiredData.initialSelectDateTime
}
