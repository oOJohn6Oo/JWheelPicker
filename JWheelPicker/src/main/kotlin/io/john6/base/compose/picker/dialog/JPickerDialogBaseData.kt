package io.john6.base.compose.picker.dialog

import android.content.Context
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import io.john6.base.compose.picker.JPickerOverlayStyle
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
open class JPickerDialogBaseData(
    open val title: Pair<Int, String> = 0 to "",
    @JPickerOverlayStyle
    open val overlayStyle: Int = 0,
    @ColorRes
    open val selectTextColorResId: Int = 0
) : Parcelable,Serializable{

    fun getDesireSelectTextColor(context: Context): Color {
        return if (selectTextColorResId == 0) Color.Unspecified
        else Color(ContextCompat.getColor(context, selectTextColorResId))
    }
}