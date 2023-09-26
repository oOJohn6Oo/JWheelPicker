package io.john6.johnbase.compose.picker.dialog.single

import android.os.Parcelable
import io.john6.johnbase.compose.picker.JPickerOverlayStyle
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
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
    val title: Pair<Int, String> = 0 to "",
    val initialIndex: Int = 0,
    val dataList: List<JWheelPickerItemInfo> = emptyList(),
    @JPickerOverlayStyle
    val overlayStyle: Int = 0,
) : Parcelable {
    /**
     * Prevent initialIndex negative or  out of range
     */
    fun getSafeInitialIndex(): Int {
        return initialIndex.takeIf { it in 0..dataList.lastIndex } ?: 0
    }
}