package io.john6.johnbase.compose.picker.dialog.multiple

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.john6.johnbase.compose.picker.JPickerOverlayStyle
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
import kotlinx.parcelize.Parcelize

class JMultiplePickerViewModel(savedStateHandle: SavedStateHandle) :ViewModel(){
    val requiredData: JMultiPickerDialogData = savedStateHandle.get<JMultiPickerDialogData>("data")!!

    val mMultipleJPickerAdapter = IMultipleJPickerAdapter.create(requiredData.adapterClass)

    /**
     * 当前选中的 item 信息，初始为 [JWheelPickerItemInfo.EMPTY]
     */
    val currentSelectedItemInfo: MutableList<JWheelPickerItemInfo> =
        mMultipleJPickerAdapter.initialIndexes.map { JWheelPickerItemInfo.EMPTY }
            .toMutableList()

}
