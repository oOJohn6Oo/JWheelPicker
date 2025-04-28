package io.john6.base.compose.picker.dialog.single

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.john6.base.compose.picker.JPickerOverlayStyle
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import io.john6.base.compose.picker.dialog.IJPickerAdapter
import io.john6.base.compose.picker.dialog.JPickerDialogBaseData
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Required data for single picker dialog
 *
 * @param title title of picker dialog
 * @param overlayStyle see [JPickerOverlayStyle]
 * @param adapterClass data list of picker
 * @param adapterParamsAsBundle param that class need
 */
@Parcelize
data class JSinglePickerDialogAdapterData(
    override val title: Pair<Int, String> = 0 to "",
    @JPickerOverlayStyle
    override val overlayStyle: Int = 0,
    override val selectTextColor: @Composable () -> Color,
    override val isDraggable: Boolean = true,
    val adapterClass: Class<out IJPickerAdapter>,
    val adapterParamsAsBundle: Bundle = Bundle(),
) : JPickerDialogBaseData(){
    @IgnoredOnParcel
    val jAdapter: IJPickerAdapter by lazy { IJPickerAdapter.create(adapterClass, adapterParamsAsBundle) }
}