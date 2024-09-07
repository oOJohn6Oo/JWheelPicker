package io.john6.base.compose.picker.dialog.single

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import io.john6.base.compose.picker.dialog.IJPickerAdapter
import io.john6.base.compose.picker.dialog.JPickerDialogBaseData

/**
 * Special ViewModel for PickerDialog
 *
 * @param savedStateHandle all data needed for picker dialog
 * @property requiredData required [JSinglePickerDialogData] for picker dialog
 * @property currentSelectedItemInfo current selected Item info of picker
 */
class JSinglePickerViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    val requiredData: JPickerDialogBaseData =
        savedStateHandle["data"] ?: throw IllegalArgumentException("data is required")

    var currentSelectedItemInfo = JWheelPickerItemInfo.EMPTY
}