package io.john6.johnbase.compose.picker

import android.os.Build
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import io.john6.johnbase.compose.picker.DatePickerMode.Companion.DATE_ALL
import io.john6.johnbase.compose.picker.DatePickerMode.Companion.DATE_MONTH_DAY
import io.john6.johnbase.compose.picker.DatePickerMode.Companion.DATE_NONE
import io.john6.johnbase.compose.picker.DatePickerMode.Companion.DATE_YEAR_MONTH
import io.john6.johnbase.compose.picker.TimePickerMode.Companion.TIME_ALL
import io.john6.johnbase.compose.picker.TimePickerMode.Companion.TIME_HOUR_MINUTE
import io.john6.johnbase.compose.picker.TimePickerMode.Companion.TIME_MINUTE_SECOND
import io.john6.johnbase.compose.picker.TimePickerMode.Companion.TIME_NONE
import io.john6.johnbase.compose.picker.JWheelPickerHelper.drawPickerLineOverlay
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
import java.time.Instant
import java.time.LocalTime
import java.time.Month
import java.time.ZonedDateTime

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

/**
 * ## 3D 滚轮时间选择器
 *
 * ### 问题点：
 * * 1 如何规避 repeat 导致的重复 composition
 * * 2 如何在开源项目的标准下，简洁、高效地封装API及引导数据流流向
 * * 3 如何恢复 Scope 中被中断的任务
 *
 * @param enableHapticFeedback 是否启用震动
 * @param hapticFeedBackYThreshold  在可震动 OffsetY 范围
 * @param initialSelectedDateTime 初始选中的时间，默认当前系统时间
 * @param datePickerMode 需要的日期格式，参考[DatePickerMode]
 * @param timePickerMode 需要的时间格式，参考[TimePickerMode]
 * @param drawOverLay 遮罩层样式，可使用 [CEWheelPickerHelper] 中的工具方法快速定制
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JDateWheelPicker(
    modifier: Modifier = Modifier,
    itemVerticalPadding: Dp = 12.dp,
    enableHapticFeedback: Boolean = true,
    fontSize: TextUnit = TextStyle.Default.fontSize,
    textStyle: TextStyle = LocalTextStyle.current,
    hapticFeedBackYThreshold: Float = 20f,
    @DatePickerMode datePickerMode: Int,
    @TimePickerMode timePickerMode: Int,
    startDateInMillis: Long = Long.MIN_VALUE,
    endDateInMillis: Long = Long.MAX_VALUE,
    initialSelectedDateTime: ZonedDateTime = ZonedDateTime.now(),
    getItemText: (wheelIndex: Int, actualValue: Int, selectedDateTime: ZonedDateTime, lang:String) -> (Int) -> JWheelPickerItemInfo = JWheelPickerHelper::getDefaultItemText,
    onSelectedTimeChanged: (ZonedDateTime) -> Unit = {},
    drawOverLay: (ContentDrawScope.(itemHeightPx: Int, edgeOffsetYPx: Float) -> Unit)? = { itemHeightPx, edgeOffsetYPx ->
        drawPickerLineOverlay(edgeOffsetYPx = edgeOffsetYPx, itemHeightPx = itemHeightPx)
    },
) {
    // 最小时间超过最大时间 ===> 错误
    if (startDateInMillis >= endDateInMillis) {
        throw IllegalStateException("minimalDateInMillis must less than maximalDateInMillis")
    }

    // 当前选中时间 不在 [最小时间,最大时间] ==>= 错误
    if (initialSelectedDateTime.toInstant().toEpochMilli()
            .let { it < startDateInMillis || it > endDateInMillis }
    ) {
        throw IllegalStateException("initialDateTime must in range of minimalDateInMillis minimalDateInMillis")
    }
    // 时区Id，默认取 初始时间 中的值
    val zone = initialSelectedDateTime.zone
    // 开始时间 以 ZonedDateTime 为表示形式
    val startZoneDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateInMillis), zone)
    // 结束时间， 以 ZonedDateTime 为表示形式
    val endZoneDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateInMillis), zone)

    // 日期时间聚合模式
    val dateTimeMode by remember(datePickerMode, timePickerMode) {
        mutableStateOf(datePickerMode or timePickerMode)
    }

    // 当前选择的时间
    var selectedDateTime by rememberSaveable(
        inputs = arrayOf(
            startDateInMillis,
            endDateInMillis,
            initialSelectedDateTime,
            dateTimeMode
        ), key = "selectedDateTime"
    ) {
        mutableStateOf(initialSelectedDateTime)
    }

    // 获取当前选择的时间 key
    val getSelectedDateTimeKey: (Int) -> Any = remember(dateTimeMode) {
        { wheelIndex ->
            getDateTimeKeyFromType(
                wheelIndex,
                dateTimeMode,
                selectedDateTime,
                startZoneDateTime,
                endZoneDateTime
            )
        }
    }

    // 上次选择的时间
    var lastSelectedDateTime = remember(dateTimeMode) {
        selectedDateTime
    }

    // 滚轮数量
    val wheelCount = remember(dateTimeMode) {
        getWheelCountForDateTime(dateTimeMode)
    }

    // 所有选择项改变的回调
    val onSelectedItemChanged: (JWheelPickerInfo, Int, JWheelPickerItemInfo) -> Unit = remember(dateTimeMode) {
        { desireData, index, itemData ->
//            val desireValue = desireData.startValue + index
//            val desireSelectedDateTime = when (desireData.id) {
//                0 -> lastSelectedDateTime.withYear(desireValue)
//                1 -> lastSelectedDateTime.withMonth(desireValue)
//                2 -> lastSelectedDateTime.withDayOfMonth(desireValue)
//                3 -> lastSelectedDateTime.withHour(desireValue)
//                4 -> lastSelectedDateTime.withMinute(desireValue)
//                5 -> lastSelectedDateTime.withSecond(desireValue)
//                else -> null
//            }
//            if (desireSelectedDateTime != null) {
//                selectedDateTime = desireSelectedDateTime
//                if (lastSelectedDateTime != desireSelectedDateTime) {
//                    lastSelectedDateTime = desireSelectedDateTime
//                    onSelectedTimeChanged(lastSelectedDateTime)
//                }
//            }
        }
    }

    // 每一项所需数据的构建方法
    val generateJWheelPickerInfo: (Int) -> JWheelPickerInfo =
        remember(dateTimeMode, getItemText) {
            {
                generateWheelPickerDataForWheelIndex(
                    it,
                    dateTimeMode,
                    selectedDateTime,
                    startZoneDateTime,
                    endZoneDateTime,
                    getItemText
                )
            }
        }

//    JMultiWheelPicker(
//        modifier = modifier,
//        itemVerticalPadding = itemVerticalPadding,
//        enableHapticFeedback = enableHapticFeedback,
//        textStyle = textStyle,
//        hapticFeedBackYThreshold = hapticFeedBackYThreshold,
//        wheelCount = wheelCount,
//        generateJWheelPickerInfo = generateJWheelPickerInfo,
//        key = getSelectedDateTimeKey,
//        onSelectedItemChanged = onSelectedItemChanged,
//        drawOverLay = drawOverLay
//    )
}

/**
 * 通过日期及时间模式获取所需要的滚轮总数
 *
 * @return 需要的滚轮总数
 */
private fun getWheelCountForDateTime(
    dateTimeMode: Int,
): Int {
    var count = 0
    repeat(6) { index ->
        val valid = (dateTimeMode and (0b100_000).shr(index)) != 0
        if (valid) count++
    }
    return count
}

/**
 * 通过滚轮下标获取唯一标识
 *
 * @param wheelIndex 滚轮下标(第几个滚轮)
 *
 * @return 当前滚轮的唯一标识（年、月、日）
 */
private fun mapWheelTypeFromIndex(
    wheelIndex: Int,
    dateTimeMode:Int,
): Int {
    var realModeInInt = -1
    var currentIndex = -1
    repeat(6) {
        val desireIndex = (0b100_000).shr(it)
        val valid = (dateTimeMode and desireIndex) != 0
        if (valid) currentIndex++

        if (currentIndex == wheelIndex && realModeInInt == -1) {
            realModeInInt = it
        }
    }
    return realModeInInt
}

/**
 * 返回 key 判断当前滚轮数据是否需要更新
 *
 * @return key
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun getDateTimeKeyFromType(
    wheelIndex: Int,
    dateTimeMode:Int,
    selectedDateTime: ZonedDateTime,
    startZoneDateTime: ZonedDateTime,
    endZoneDateTime: ZonedDateTime
): Any {
    val desireKey = when (mapWheelTypeFromIndex(wheelIndex, dateTimeMode)) {
        0 -> 0
        1 -> getMonthValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
        2 -> getDayValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
        3 -> getHourValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
        4 -> getMinuteValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
        5 -> getSecondValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
        else -> -1
    }
//    "getDateTimeKeyFromType index:$wheelIndex,${dateTimeMode.toString(2)} ===>$desireKey".log()
    return if (desireKey is Triple<*,*,*>)
        desireKey.first to desireKey.second
    else desireKey

}

/**
 * 通过 滚轮下标及当前日期时间格式，生成当前滚轮所需数据
 *
 * @param wheelIndex 滚轮下标
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun generateWheelPickerDataForWheelIndex(
    wheelIndex: Int,
    dateTimeMode: Int,
    selectedDateTime: ZonedDateTime,
    startZoneDateTime: ZonedDateTime,
    endZoneDateTime: ZonedDateTime,
    getItemText: (wheelIndex: Int, actualValue: Int, selectedDateTime: ZonedDateTime, lang:String) -> ((Int) -> JWheelPickerItemInfo)
): JWheelPickerInfo {
    val desireIndex = mapWheelTypeFromIndex(wheelIndex, dateTimeMode)

    val itemCount: Int
    // 当前滚轮的开始值、结束值及当前选中项的值
    val dateTimeValue =
        getDateTimeValue(desireIndex, selectedDateTime, startZoneDateTime, endZoneDateTime)
    val itemData: ((Int) -> JWheelPickerItemInfo) =
        getItemText(wheelIndex, dateTimeValue.first, selectedDateTime, Locale.current.language)
    itemCount = (dateTimeValue.second - dateTimeValue.first) + 1
    return JWheelPickerInfo(
        desireIndex,
        itemCount,
        itemData,
        dateTimeValue.third,
    )
}

/**
 * 通过当前时间格式标识获取选择器的开始值、结束值及当前选中项
 *
 * @param dateTimeTypeIndex 年-月-日 时:分:秒 对应{0,1,2,3,4,5}
 *
 * @return
 *
 * - [Triple.first] :开始值
 * - [Triple.second] :结束值
 * - [Triple.third] :当前选中项的值
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun getDateTimeValue(
    dateTimeTypeIndex: Int,
    selectedDateTime: ZonedDateTime,
    startZoneDateTime: ZonedDateTime,
    endZoneDateTime: ZonedDateTime
): Triple<Int, Int, Int> {
    return when (dateTimeTypeIndex) {
        0 -> getYearValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
        1 -> getMonthValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
        2 -> getDayValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
        3 -> getHourValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
        4 -> getMinuteValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
        else -> getSecondValue(selectedDateTime, startZoneDateTime, endZoneDateTime)
    }
}

//<editor-fold desc="获取各个时间格式的开始值、结束值及当前选中项">
@RequiresApi(Build.VERSION_CODES.O)
private fun getYearValue(
    selectedDateTime: ZonedDateTime,
    startZoneDateTime: ZonedDateTime,
    endZoneDateTime: ZonedDateTime
): Triple<Int, Int, Int> {
    val startValue = startZoneDateTime.year
    val endValue = endZoneDateTime.year
    val selectedIndex = selectedDateTime.year - startValue

    return Triple(startValue, endValue, selectedIndex)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getMonthValue(
    selectedDateTime: ZonedDateTime,
    startZoneDateTime: ZonedDateTime,
    endZoneDateTime: ZonedDateTime
): Triple<Int, Int, Int> {
    // the month-of-year,from 1 to 12

    val startValue = if (selectedDateTime.year == startZoneDateTime.year) {
        startZoneDateTime.monthValue
    } else {
        Month.JANUARY.value
    }

    val endValue = if (selectedDateTime.year == endZoneDateTime.year) {
        endZoneDateTime.monthValue
    } else {
        Month.DECEMBER.value
    }
    val selectedIndex = selectedDateTime.monthValue - startValue

    return Triple(startValue, endValue, selectedIndex)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getDayValue(
    selectedDateTime: ZonedDateTime,
    startZoneDateTime: ZonedDateTime,
    endZoneDateTime: ZonedDateTime
): Triple<Int, Int, Int> {
    val startValue = if (selectedDateTime.year == startZoneDateTime.year
        && selectedDateTime.month == startZoneDateTime.month
    ) {
        startZoneDateTime.dayOfMonth
    } else {
        1
    }
    val endValue = if (selectedDateTime.year == endZoneDateTime.year
        && selectedDateTime.month == endZoneDateTime.month
    ) {
        endZoneDateTime.dayOfMonth
    } else {
        selectedDateTime.toLocalDate().lengthOfMonth()
    }
    val selectedIndex = selectedDateTime.dayOfMonth - startValue

    return Triple(startValue, endValue, selectedIndex)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getHourValue(
    selectedDateTime: ZonedDateTime,
    startZoneDateTime: ZonedDateTime,
    endZoneDateTime: ZonedDateTime
): Triple<Int, Int, Int> {

    val zeroOClockOfSelected = selectedDateTime.zeroAllTime().toInstant().toEpochMilli()
    val startValue = if (zeroOClockOfSelected == startZoneDateTime.zeroAllTime().toInstant()
            .toEpochMilli()
    ) {
        startZoneDateTime.hour
    } else {
        0
    }
    val endValue = if (zeroOClockOfSelected == endZoneDateTime.zeroAllTime().toInstant()
            .toEpochMilli()
    ) {
        endZoneDateTime.hour
    } else {
        23
    }
    val selectedIndex = selectedDateTime.hour - startValue

    return Triple(startValue, endValue, selectedIndex)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getMinuteValue(
    selectedDateTime: ZonedDateTime,
    startZoneDateTime: ZonedDateTime,
    endZoneDateTime: ZonedDateTime
): Triple<Int, Int, Int> {
    val zeroMinuteOfSelected =
        selectedDateTime.zeroTimeWithHour().toInstant().toEpochMilli()
    val startValue =
        if (zeroMinuteOfSelected == startZoneDateTime.zeroTimeWithHour().toInstant()
                .toEpochMilli()
        ) {
            startZoneDateTime.minute
        } else {
            0
        }
    val endValue =
        if (zeroMinuteOfSelected == endZoneDateTime.zeroTimeWithHour().toInstant()
                .toEpochMilli()
        ) {
            endZoneDateTime.minute
        } else {
            59
        }
    val selectedIndex = selectedDateTime.minute - startValue
    return Triple(startValue, endValue, selectedIndex)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getSecondValue(
    selectedDateTime: ZonedDateTime,
    startZoneDateTime: ZonedDateTime,
    endZoneDateTime: ZonedDateTime
): Triple<Int, Int, Int> {

    val zeroSecondOfSelected =
        selectedDateTime.zeroTimeWithHourMinute().toInstant().toEpochMilli()
    val startValue =
        if (zeroSecondOfSelected == startZoneDateTime.zeroTimeWithHourMinute()
                .toInstant()
                .toEpochMilli()
        ) {
            startZoneDateTime.minute
        } else {
            0
        }
    val endValue =
        if (zeroSecondOfSelected == endZoneDateTime.zeroTimeWithHourMinute().toInstant()
                .toEpochMilli()
        ) {
            endZoneDateTime.minute
        } else {
            59
        }
    val selectedIndex = selectedDateTime.second - startValue
    return Triple(startValue, endValue, selectedIndex)
}

//</editor-fold>



@RequiresApi(Build.VERSION_CODES.O)
fun ZonedDateTime.zeroAllTime(): ZonedDateTime {
    return this.with(LocalTime.MIN)
}

@RequiresApi(Build.VERSION_CODES.O)
fun ZonedDateTime.zeroTimeWithHour(): ZonedDateTime {
    return this.with(LocalTime.MIN.plusHours(this.hour.toLong()))
}

@RequiresApi(Build.VERSION_CODES.O)
fun ZonedDateTime.zeroTimeWithHourMinute(): ZonedDateTime {
    return this.with(
        LocalTime.MIN.plusHours(this.hour.toLong())
            .plusMinutes(this.minute.toLong())
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun ZonedDateTime.toEpochMilli() = this.toInstant().toEpochMilli()

