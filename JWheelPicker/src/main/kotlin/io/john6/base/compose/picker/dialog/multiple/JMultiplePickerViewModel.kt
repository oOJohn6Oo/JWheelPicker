package io.john6.base.compose.picker.dialog.multiple

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import io.john6.base.compose.picker.dialog.IJPickerAdapter

class JMultiplePickerViewModel(savedStateHandle: SavedStateHandle) :ViewModel(){
    val requiredData: JMultiPickerDialogData =
        savedStateHandle["data"] ?: throw IllegalArgumentException("data is required")

    val mMultipleJPickerAdapter = IJPickerAdapter.create(
        requiredData.adapterClass,
        requiredData.adapterParamsAsBundle
    )

    /**
     * 当前选中的 item 信息，初始为 [JWheelPickerItemInfo.EMPTY]
     */
    val currentSelectedItemInfo: MutableList<JWheelPickerItemInfo> =
        mMultipleJPickerAdapter.initialIndexes.map { JWheelPickerItemInfo.EMPTY }
            .toMutableList()

}
