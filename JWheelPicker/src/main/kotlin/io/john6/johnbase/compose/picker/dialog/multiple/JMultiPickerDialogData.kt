package io.john6.johnbase.compose.picker.dialog.multiple

import android.os.Parcelable
import io.john6.johnbase.compose.picker.JPickerOverlayStyle
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
    val adapterClass:Class<out IMultipleJPickerAdapter>,
    val title: Pair<Int, String> = 0 to "",
    @JPickerOverlayStyle
    val overlayStyle:Int = 0,
) : Parcelable