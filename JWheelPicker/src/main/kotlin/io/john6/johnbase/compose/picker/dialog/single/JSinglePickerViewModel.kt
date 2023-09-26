package io.john6.johnbase.compose.picker.dialog.single

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo

/**
 * Special ViewModel for PickerDialog
 *
 * @param savedStateHandle all data needed for picker dialog
 * @property requiredData required [JSinglePickerDialogData] for picker dialog
 * @property currentSelectedItemInfo current selected Item info of picker
 */
class JSinglePickerViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    val requiredData: JSinglePickerDialogData =
        savedStateHandle["data"] ?: throw IllegalArgumentException("data is required")

    var currentSelectedItemInfo: JWheelPickerItemInfo = requiredData.dataList.getOrNull(
        requiredData.getSafeInitialIndex()
    ) ?: JWheelPickerItemInfo.EMPTY

}