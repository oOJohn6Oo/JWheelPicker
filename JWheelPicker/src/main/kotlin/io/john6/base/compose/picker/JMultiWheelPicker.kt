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
 * 多滚轮选择器
 *
 * @param enableHapticFeedback 是否启用震动
 * @param hapticFeedBackYThreshold  震动范围
 * @param wheelCount 所需滚轮数量
 * @param key 当前滚轮数据的 key，仅当 key 改变时才会刷新数据
 * @param generateJWheelPickerInfo 生成滚轮数据的方法
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
    hapticFeedBackYThreshold: Float = 20f,
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
                confirmSelectYThreshold = hapticFeedBackYThreshold,
                onSelectedItemChanged = onSelectedItemChanged,
                generatePickerData = generateJWheelPickerInfo,
                key = key,
            )
        }
    }
}

/**
 * 多滚轮中的每个单项
 *
 * @param key 当前滚轮数据的 key，仅当 key 改变时才会刷新数据
 * @param generatePickerData 生成滚轮数据的方法
 * @param onSelectedItemChanged 选中项改变时的回调 滚轮信息, 选中项下标, 选中项数据
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
