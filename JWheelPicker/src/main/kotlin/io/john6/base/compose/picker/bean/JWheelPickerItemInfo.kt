package io.john6.base.compose.picker.bean

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.parcelize.Parcelize

@Parcelize
data class JWheelPickerItemInfo(
    val id: String,
    val index:Int,
    val fallbackText: String,
    @StringRes val resId: Int = 0,
) : Parcelable {
    companion object {
        val EMPTY = JWheelPickerItemInfo("", -1, "")
    }
}

@Composable
fun JWheelPickerItemInfo.getText(): String {
    return if (resId != 0) {
        stringResource(resId)
    }else{
        fallbackText
    }
}