package io.john6.base.compose.picker.dialog.multiple

import android.os.Bundle
import android.os.Parcelable
import io.john6.base.compose.picker.JPickerOverlayStyle
import io.john6.base.compose.picker.dialog.IJPickerAdapter
import io.john6.base.compose.picker.dialog.JPickerDialogBaseData
import kotlinx.parcelize.Parcelize

/**
 * Required data for JMultiPickerDialog
 *
 * @param title title of picker dialog
 * @param overlayStyle see [JPickerOverlayStyle]
 * @param adapterClass class that extend [IJPickerAdapter]
 * @param adapterParamsAsBundle param that class need
 */
@Parcelize
data class JMultiPickerDialogData(
    override val title: Pair<Int, String> = 0 to "",
    @JPickerOverlayStyle
    override val overlayStyle: Int = 0,
    override val selectTextColorResId: Int = 0,
    val adapterClass: Class<out IJPickerAdapter>,
    val adapterParamsAsBundle: Bundle = Bundle(),
) : JPickerDialogBaseData()