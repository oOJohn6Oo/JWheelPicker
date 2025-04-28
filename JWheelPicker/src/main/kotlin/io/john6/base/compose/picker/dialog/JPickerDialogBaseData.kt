package io.john6.base.compose.picker.dialog

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.john6.base.compose.picker.JPickerOverlayStyle
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * @param title The title of the picker dialog
 * @param overlayStyle The style of the overlay when the picker dialog is displayed
 * @param selectTextColor The text color when selected
 * @param isDraggable Control whether the picker dialog can be dragged
 */
@Parcelize
open class JPickerDialogBaseData(
    open val title: Pair<Int, String> = 0 to "",
    @JPickerOverlayStyle
    open val overlayStyle: Int = 0,
    open val selectTextColor:  @Composable () -> Color  = { Color.Unspecified },
    open val isDraggable: Boolean = true,
) : Parcelable,Serializable