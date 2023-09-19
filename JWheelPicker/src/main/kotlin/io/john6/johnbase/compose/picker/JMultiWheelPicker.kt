package io.john6.johnbase.compose.picker

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.john6.johnbase.compose.picker.JWheelPickerHelper.drawPickerLineOverlay
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
import kotlin.math.roundToInt


/**
 * 多滚轮选择器
 * @author Liu Qiang
 * @since 2023-03-05
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
    enableHapticFeedback: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
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
        }, horizontalArrangement = Arrangement.SpaceAround) {
        repeat(wheelCount) { wheelIndex ->
            ItemWheelPicker(
                height = height,
                wheelIndex = wheelIndex,
                itemVerticalPadding = itemVerticalPadding,
                enableHapticFeedback = enableHapticFeedback,
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
    key: (wheelIndex: Int) -> Any,
    onSelectedItemChanged: (JWheelPickerInfo, JWheelPickerItemInfo) -> Unit,
    generatePickerData: (wheelIndex: Int) -> JWheelPickerInfo
) {
    val pickerData by remember(generatePickerData, key(wheelIndex)) {
        Log.d("lq", "generatePickerData $wheelIndex :${generatePickerData(wheelIndex)}")
        mutableStateOf(generatePickerData(wheelIndex))
    }
    JWheelPicker(
        modifier = Modifier
           .weight(1f),
        height = height,
        itemVerticalPadding = itemVerticalPadding,
        enableHapticFeedback = enableHapticFeedback,
        textStyle = textStyle,
        confirmSelectYThreshold = confirmSelectYThreshold,
        itemCount = pickerData.itemCount,
        itemData = pickerData.itemData,
        initialIndex = pickerData.initialIndex,
        drawOverLay = null,
        onSelectedItemChanged = { itemData ->
            onSelectedItemChanged(pickerData, itemData)
        }
    )
}

/**
 * 滚轮通用数据
 * @param id 当前滚轮的唯一标识
 * @param itemCount Item 总数量
 * @param itemData 获取 Item 数据的方法
 * @param initialIndex 默认选中项的下标
 */
data class JWheelPickerInfo(
    val id: Int,
    val itemCount: Int,
    val itemData: (index: Int) -> JWheelPickerItemInfo,
    val initialIndex: Int,
) {
    companion object {
        val EMPTY = JWheelPickerInfo(
            0, 0, { JWheelPickerItemInfo.EMPTY }, 0
        )
    }
}