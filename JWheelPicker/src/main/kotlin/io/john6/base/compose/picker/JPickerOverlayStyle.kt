package io.john6.base.compose.picker

import androidx.annotation.IntDef

@IntDef(
    value = [JWheelPickerHelper.OVERLAY_STYLE_RECTANGLE, JWheelPickerHelper.OVERLAY_STYLE_LINE]
)
@Retention(AnnotationRetention.SOURCE)
annotation class JPickerOverlayStyle