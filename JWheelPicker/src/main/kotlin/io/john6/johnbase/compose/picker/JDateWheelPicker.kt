package io.john6.johnbase.compose.picker

import android.os.Build
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.john6.johnbase.compose.picker.DatePickerMode.Companion.DATE_ALL
import io.john6.johnbase.compose.picker.DatePickerMode.Companion.DATE_MONTH_DAY
import io.john6.johnbase.compose.picker.DatePickerMode.Companion.DATE_NONE
import io.john6.johnbase.compose.picker.DatePickerMode.Companion.DATE_YEAR_MONTH
import io.john6.johnbase.compose.picker.JDatePickerHelper.generateWheelPickerDataForWheelIndex
import io.john6.johnbase.compose.picker.JWheelPickerHelper.drawPickerRectOverlay
import io.john6.johnbase.compose.picker.TimePickerMode.Companion.TIME_ALL
import io.john6.johnbase.compose.picker.TimePickerMode.Companion.TIME_HOUR_MINUTE
import io.john6.johnbase.compose.picker.TimePickerMode.Companion.TIME_MINUTE_SECOND
import io.john6.johnbase.compose.picker.TimePickerMode.Companion.TIME_NONE
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * ## 3D 滚轮时间选择器
 *
 *
 * @param enableHapticFeedback 是否启用震动
 * @param hapticFeedBackYThreshold  在可震动 OffsetY 范围
 * @param datePickerMode 需要的日期格式，参考[DatePickerMode]
 * @param timePickerMode 需要的时间格式，参考[TimePickerMode]
 * @param drawOverLay 遮罩层样式，可使用 [JWheelPickerHelper] 中的工具方法快速定制
 * @param startLocalDateTime 选择器的开始时间，default is 1970-01-01 00:00:00 at local time
 * @param endLocalDateTime 结束时间，默认为可支持的最大时间 详见 [LocalDateTime.MAX]
 * @param initialSelectDateTime 初始选中时间，默认为当前时间
 *
 * @param onSelectedTimeChanged 选中的时间改变时的回调，默认无
 * @param getItemText 定制每个滚轮的时间显示格式
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JDateWheelPicker(
    modifier: Modifier = Modifier,
    height: Dp = 240.dp,
    itemVerticalPadding: Dp = 4.dp,
    containerHorizontalPadding: Dp = 0.dp,
    enableHapticFeedback: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    hapticFeedBackYThreshold: Float = 20f,
    @DatePickerMode datePickerMode: Int,
    @TimePickerMode timePickerMode: Int,
    startLocalDateTime: LocalDateTime = LocalDateTime.ofEpochSecond(0L, 0, ZoneOffset.UTC),
    endLocalDateTime: LocalDateTime = LocalDateTime.MAX,
    initialSelectDateTime: LocalDateTime = LocalDateTime.now(),
    getItemText: (wheelIndex: Int, actualValue: Int, selectedDateTime: LocalDateTime, lang:String) -> (Int) -> String = JDatePickerHelper::getDefaultItemText,
    onSelectedTimeChanged: (LocalDateTime) -> Unit = {},
    drawOverLay: (ContentDrawScope.(itemHeightPx: Int, edgeOffsetYPx: Float) -> Unit)? = { itemHeightPx, edgeOffsetYPx ->
        drawPickerRectOverlay(edgeOffsetYPx, itemHeightPx)
    },
) {

    // Ensure the input is valid
    LaunchedEffect(key1 = startLocalDateTime, key2 = endLocalDateTime, key3 = initialSelectDateTime){
        // 最小时间超过最大时间 ===> 错误
        if (startLocalDateTime >= endLocalDateTime) {
            throw IllegalStateException("minimalDateInMillis must less than maximalDateInMillis")
        }

        // 当前选中时间 不在 [最小时间,最大时间] ==>= 错误
        if (initialSelectDateTime !in startLocalDateTime .. endLocalDateTime) {
            throw IllegalStateException("initialDateTime must in range of minimalDateInMillis minimalDateInMillis")
        }
    }

    // 日期时间聚合模式
    val dateTimeMode by remember(datePickerMode, timePickerMode) {
        mutableIntStateOf(datePickerMode or timePickerMode)
    }
    // 当前选择的时间
    var selectLocalDateTime by rememberSaveable(
        inputs = arrayOf(
            startLocalDateTime,
            endLocalDateTime,
            initialSelectDateTime,
            dateTimeMode
        ), key = "selectedDateTime"
    ) {
        mutableStateOf(initialSelectDateTime)
    }

    // 获取当前选择的时间 key
    val getSelectedDateTimeKey: (Int) -> Any = remember(dateTimeMode) {
        { wheelIndex ->
            JDatePickerHelper.getDateTimeKeyFromType(
                wheelIndex,
                dateTimeMode,
                selectLocalDateTime,
                startLocalDateTime,
                endLocalDateTime
            )
        }
    }

    // 上次选择的时间
    var lastSelectedDateTime = remember(dateTimeMode) {
        selectLocalDateTime
    }

    // 滚轮数量
    val wheelCount = remember(dateTimeMode) {
        JDatePickerHelper.getWheelCountForDateTime(dateTimeMode)
    }

    // 所有选择项改变的回调
    // 这里作用为 时间改变时更新当前已选中的时间值，用来更新滚轮选项
    val onSelectedItemChanged: (JWheelPickerInfo, JWheelPickerItemInfo) -> Unit = remember(dateTimeMode) {
        { desireData, itemData ->
            val desireValue = itemData.id.toInt()
            val desireSelectedDateTime = when (desireData.id) {
                0 -> lastSelectedDateTime.withYear(desireValue)
                1 -> lastSelectedDateTime.withMonth(desireValue)
                2 -> lastSelectedDateTime.withDayOfMonth(desireValue)
                3 -> lastSelectedDateTime.withHour(desireValue)
                4 -> lastSelectedDateTime.withMinute(desireValue)
                5 -> lastSelectedDateTime.withSecond(desireValue)
                else -> null
            }
            if (desireSelectedDateTime != null) {
                selectLocalDateTime = desireSelectedDateTime
                if (lastSelectedDateTime != desireSelectedDateTime) {
                    lastSelectedDateTime = desireSelectedDateTime
                    onSelectedTimeChanged(lastSelectedDateTime)
                }
            }
        }
    }

    // 每一项所需数据的构建方法
    val generateJWheelPickerInfo: (Int) -> JWheelPickerInfo =
        remember(dateTimeMode, getItemText) {
            {
                generateWheelPickerDataForWheelIndex(
                    it,
                    dateTimeMode,
                    selectLocalDateTime,
                    startLocalDateTime,
                    endLocalDateTime,
                    getItemText
                )
            }
        }

    JMultiWheelPicker(
        modifier = modifier,
        height = height,
        itemVerticalPadding = itemVerticalPadding,
        containerHorizontalPadding = containerHorizontalPadding,
        enableHapticFeedback = enableHapticFeedback,
        textStyle = textStyle,
        hapticFeedBackYThreshold = hapticFeedBackYThreshold,
        wheelCount = wheelCount,
        generateJWheelPickerInfo = generateJWheelPickerInfo,
        key = getSelectedDateTimeKey,
        onSelectedItemChanged = onSelectedItemChanged,
        drawOverLay = drawOverLay
    )
}


@IntDef(
    value = [
        DATE_ALL,
        DATE_YEAR_MONTH,
        DATE_MONTH_DAY,
        DATE_NONE,
    ],
)
@Retention(AnnotationRetention.SOURCE)
annotation class DatePickerMode {
    companion object {
        const val DATE_ALL = 0b111_000
        const val DATE_YEAR_MONTH = 0b110_000
        const val DATE_MONTH_DAY = 0b011_000
        const val DATE_NONE = 0b000_000
    }
}

@IntDef(
    value = [
        TIME_ALL,
        TIME_HOUR_MINUTE,
        TIME_MINUTE_SECOND,
        TIME_NONE,
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimePickerMode {
    companion object {
        const val TIME_ALL = 0b111
        const val TIME_HOUR_MINUTE = 0b110
        const val TIME_MINUTE_SECOND = 0b011
        const val TIME_NONE = 0b000
    }
}
