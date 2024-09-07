package io.john6.base.compose.picker.dialog.single

import io.john6.base.compose.picker.JPickerOverlayStyle
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import io.john6.base.compose.picker.dialog.JPickerDialogBaseData
import kotlinx.parcelize.Parcelize

/**
 * Required data for single picker dialog
 *
 * @param title title of picker dialog
 * @param initialIndex initial selected index of picker
 * @param dataList data list of picker
 * @param overlayStyle 0 for oval rectangle, 1 for line
 */
@Parcelize
data class JSinglePickerDialogData(
    override val title: Pair<Int, String> = 0 to "",
    @JPickerOverlayStyle
    override val overlayStyle: Int = 0,
    override val selectTextColorResId: Int = 0,
    val initialIndex: Int = 0,
    val dataList: List<JWheelPickerItemInfo> = emptyList(),
) : JPickerDialogBaseData() {
    /**
     * Prevent initialIndex negative or  out of range
     */
    fun getSafeInitialIndex(): Int {
        return initialIndex.takeIf { it in 0..dataList.lastIndex } ?: 0
    }
}