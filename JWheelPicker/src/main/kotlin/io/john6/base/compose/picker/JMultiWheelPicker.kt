package io.john6.base.compose.picker

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.john6.base.compose.picker.JWheelPickerHelper.drawPickerLineOverlay
import io.john6.base.compose.picker.bean.JWheelPickerInfo
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import kotlin.math.roundToInt


/**
 * Multiple WheelPicker
 *
 * @param modifier [Modifier] will be applied to the outside Widget
 * @param height Height of this Widget and it's child WheelPicker
 * @param itemVerticalPadding Will applied to vertical of each item, only available in [JWheelPickerArrangement.Vertical]
 * @param containerHorizontalPadding Will applied to horizontal of this Widget
 * @param enableHapticFeedback Vibrate when scroll, using [android.view.View.performHapticFeedback]
 * @param textStyle Text style of each Item
 * @param selectedTextColor Text color when item is selected
 * @param hapticFeedbackYThreshold  item will be selected while item centerY in [picker center - this , picker center + this]
 * @param wheelCount Total picker count inside this Widget
 * @param drawOverLay The overlay of this Picker, there are 2 preset style, [JWheelPickerHelper.OVERLAY_STYLE_RECTANGLE] and [JWheelPickerHelper.OVERLAY_STYLE_LINE]
 * @param generateJWheelPickerInfo The method to generate picker data
 * @param key Used to refresh the whole Picker data
 * @param onSelectedItemChanged The method will be called when item is selected
 */
@Composable
fun JMultiWheelPicker(
    modifier: Modifier = Modifier,
    height: Dp = 240.dp,
    itemVerticalPadding: Dp = 4.dp,
    containerHorizontalPadding: Dp = 0.dp,
    enableHapticFeedback: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    selectedTextColor: Color = Color.Unspecified,
    hapticFeedbackYThreshold: Float = 20f,
    wheelCount: Int,
    drawOverLay: (ContentDrawScope.(itemHeightPx: Int, edgeOffsetYPx: Float) -> Unit)? = { itemHeightPx, edgeOffsetYPx ->
        drawPickerLineOverlay(edgeOffsetYPx = edgeOffsetYPx, itemHeightPx = itemHeightPx)
    },
    generateJWheelPickerInfo: (wheelIndex: Int) -> JWheelPickerInfo,
    key: (wheelIndex: Int) -> Any,
    onSelectedItemChanged: (jWheelPickerInfo: JWheelPickerInfo, itemData: JWheelPickerItemInfo) -> Unit,
) {
    val density = LocalDensity.current
    // Item 高度
    val fontSize = textStyle.fontSize.takeIf { it != TextUnit.Unspecified } ?: 14.sp
    val itemHeightPx by remember(fontSize, itemVerticalPadding) {
        mutableIntStateOf(with(density) { (fontSize.toPx() + (itemVerticalPadding * 2).toPx()).roundToInt() })
    }

    Row(modifier = modifier
        .height(height)
        .drawWithContent {
            drawContent()
            drawOverLay?.invoke(this, itemHeightPx, (size.height - itemHeightPx) / 2f)
        }
        .padding(horizontal = containerHorizontalPadding)
    ) {
        repeat(wheelCount) { wheelIndex ->
            ItemWheelPicker(
                height = height,
                wheelIndex = wheelIndex,
                itemVerticalPadding = itemVerticalPadding,
                enableHapticFeedback = enableHapticFeedback,
                selectedTextColor = selectedTextColor,
                textStyle = textStyle,
                confirmSelectYThreshold = hapticFeedbackYThreshold,
                onSelectedItemChanged = onSelectedItemChanged,
                generatePickerData = generateJWheelPickerInfo,
                key = key,
            )
        }
    }
}

/**
 * Single WheelPicker
 *
 * @param wheelIndex Current index of the wheel
 * @param itemVerticalPadding Will applied to vertical of each item, only available in [JWheelPickerArrangement.Vertical]
 * @param enableHapticFeedback Vibrate when scroll, using [android.view.View.performHapticFeedback]
 * @param textStyle Text style of each Item
 * @param confirmSelectYThreshold  item will be selected while item centerY in [picker center - this , picker center + this]
 * @param selectedTextColor Text color when item is selected
 * @param key Used to refresh the whole Picker data
 * @param onSelectedItemChanged The method will be called when item is selected
 * @param generatePickerData The method to generate picker data
 */
@Composable
private fun RowScope.ItemWheelPicker(
    height: Dp,
    wheelIndex: Int,
    itemVerticalPadding: Dp,
    enableHapticFeedback: Boolean,
    textStyle: TextStyle,
    confirmSelectYThreshold: Float,
    selectedTextColor: Color = Color.Unspecified,
    key: (wheelIndex: Int) -> Any,
    onSelectedItemChanged: (JWheelPickerInfo, JWheelPickerItemInfo) -> Unit,
    generatePickerData: (wheelIndex: Int) -> JWheelPickerInfo
) {
    val pickerData by remember(generatePickerData, key(wheelIndex)) {
        mutableStateOf(generatePickerData(wheelIndex))
    }
    JWheelPicker(
        modifier = Modifier
            .weight(1f),
        size = height,
        itemPadding = itemVerticalPadding,
        enableHapticFeedback = enableHapticFeedback,
        textStyle = textStyle,
        selectedTextColor = selectedTextColor,
        confirmSelectDistanceThreshold = confirmSelectYThreshold,
        itemCount = pickerData.itemCount,
        itemData = pickerData.itemData,
        initialIndex = pickerData.initialIndex,
        drawOverLay = null,
        onSelectedItemChanged = { itemData ->
            onSelectedItemChanged(pickerData, itemData)
        }
    )
}
