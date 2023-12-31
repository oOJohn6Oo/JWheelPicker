package io.john6.base.compose.picker.dialog.multiple

import android.os.Bundle
import android.os.Parcelable
import io.john6.base.compose.picker.JPickerOverlayStyle
import kotlinx.parcelize.Parcelize

/**
 * Required data for JMultiPickerDialog
 *
 * @param adapterClass class of IMultipleJPickerAdapter
 * @param title title of picker dialog
 * @param overlayStyle 0 for oval rectangle, 1 for line
 */
@Parcelize
data class JMultiPickerDialogData(
    val adapterClass: Class<out IMultipleJPickerAdapter>,
    val adapterParamsAsBundle: Bundle = Bundle(),
    val title: Pair<Int, String> = 0 to "",
    @JPickerOverlayStyle
    val overlayStyle: Int = 0,
) : Parcelable