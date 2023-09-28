package io.john6.johnbase.compose.picker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.text.intl.Locale
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatterBuilder
import java.time.format.TextStyle
import java.time.temporal.ChronoField

/**
 * Container of [JDateWheelPicker]'s helper functions
 */
@RequiresApi(Build.VERSION_CODES.O)
object JDatePickerHelper {

    /**
     * 通过 滚轮下标及当前日期时间格式，生成当前滚轮所需数据
     *
     * @param wheelIndex 滚轮下标
     */
    fun generateWheelPickerDataForWheelIndex(
        wheelIndex: Int,
        dateTimeMode: Int,
        selectedDateTime: LocalDateTime,
        startLocalDateTime: LocalDateTime,
        endLocalDateTime: LocalDateTime,
        getItemText: (wheelIndex: Int, actualValue: Int, selectedDateTime: LocalDateTime, lang:String) -> ((Int) -> String)
    ): JWheelPickerInfo {
        val desireIndex = mapWheelTypeFromIndex(wheelIndex, dateTimeMode)

        val itemCount: Int
        // 当前滚轮的开始值、结束值及当前选中项的值
        val dateTimeValue =
            getDateTimeValue(desireIndex, selectedDateTime, startLocalDateTime, endLocalDateTime)

        val itemText
           = getItemText(wheelIndex, dateTimeValue.first, selectedDateTime, Locale.current.language)

        val itemData: ((Int) -> JWheelPickerItemInfo) = {
            JWheelPickerItemInfo(
                id = (dateTimeValue.first + it).toString(),
                index = it,
                fallbackText = itemText(it),
            )
        }
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
    private fun getDateTimeValue(
        dateTimeTypeIndex: Int,
        selectedDateTime: LocalDateTime,
        startLocalDateTime: LocalDateTime,
        endLocalDateTime: LocalDateTime
    ): Triple<Int, Int, Int> {
        return when (dateTimeTypeIndex) {
            0 -> getYearValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
            1 -> getMonthValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
            2 -> getDayValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
            3 -> getHourValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
            4 -> getMinuteValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
            else -> getSecondValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
        }
    }
    
    /**
     * 通过日期及时间模式获取所需要的滚轮总数
     *
     * @return 需要的滚轮总数
     */
    fun getWheelCountForDateTime(
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
     * 返回 key 判断当前滚轮数据是否需要更新
     *
     * @return key
     */
    fun getDateTimeKeyFromType(
        wheelIndex: Int,
        dateTimeMode: Int,
        selectedDateTime: LocalDateTime,
        startLocalDateTime: LocalDateTime,
        endLocalDateTime: LocalDateTime
    ): Any {
        val desireKey = when (mapWheelTypeFromIndex(wheelIndex, dateTimeMode)) {
            0 -> 0
            1 -> getMonthValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
            2 -> getDayValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
            3 -> getHourValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
            4 -> getMinuteValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
            5 -> getSecondValue(selectedDateTime, startLocalDateTime, endLocalDateTime)
            else -> -1
        }
        return if (desireKey is Triple<*, *, *>)
            desireKey.first to desireKey.second
        else desireKey

    }


    /**
     * 通过滚轮下标获取唯一标识
     *
     * @param wheelIndex 滚轮下标(第几个滚轮)
     *
     * @return 当前滚轮的唯一标识（年、月、日）
     */
    fun mapWheelTypeFromIndex(
        wheelIndex: Int,
        dateTimeMode: Int,
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

    //<editor-fold desc="获取各个时间格式的开始值、结束值及当前选中项">
    /**
     * 将当前时间的年份值 与 开始时间、结束时间的年份值相比较，返回筛选时应显示的三个值
     */
    fun getYearValue(
        selectedDateTime: LocalDateTime,
        startLocalDateTime: LocalDateTime,
        endLocalDateTime: LocalDateTime
    ): Triple<Int, Int, Int> {
        val startValue = startLocalDateTime.year
        val endValue = endLocalDateTime.year
        val selectedIndex = selectedDateTime.year - startValue

        return Triple(startValue, endValue, selectedIndex)
    }

    /**
     * 将当前时间的月份值 与 开始时间、结束时间的月份值相比较，返回筛选时应显示的三个值
     */
    fun getMonthValue(
        selectedDateTime: LocalDateTime,
        startLocalDateTime: LocalDateTime,
        endLocalDateTime: LocalDateTime
    ): Triple<Int, Int, Int> {
        // the month-of-year,from 1 to 12

        val startValue = if (selectedDateTime.year == startLocalDateTime.year) {
            startLocalDateTime.monthValue
        } else {
            Month.JANUARY.value
        }

        val endValue = if (selectedDateTime.year == endLocalDateTime.year) {
            endLocalDateTime.monthValue
        } else {
            Month.DECEMBER.value
        }
        val selectedIndex = selectedDateTime.monthValue - startValue

        return Triple(startValue, endValue, selectedIndex)
    }

    /**
     * 将当前时间的日期值 与 开始时间、结束时间的日期值相比较，返回筛选时应显示的三个值
     */
    fun getDayValue(
        selectedDateTime: LocalDateTime,
        startLocalDateTime: LocalDateTime,
        endLocalDateTime: LocalDateTime
    ): Triple<Int, Int, Int> {
        val startValue = if (selectedDateTime.year == startLocalDateTime.year
            && selectedDateTime.month == startLocalDateTime.month
        ) {
            startLocalDateTime.dayOfMonth
        } else {
            1
        }
        val endValue = if (selectedDateTime.year == endLocalDateTime.year
            && selectedDateTime.month == endLocalDateTime.month
        ) {
            endLocalDateTime.dayOfMonth
        } else {
            selectedDateTime.toLocalDate().lengthOfMonth()
        }
        val selectedIndex = selectedDateTime.dayOfMonth - startValue

        return Triple(startValue, endValue, selectedIndex)
    }

    /**
     * 将当前时间的小时值 与 开始时间、结束时间的小时值相比较，返回筛选时应显示的三个值
     */
    fun getHourValue(
        selectedDateTime: LocalDateTime,
        startLocalDateTime: LocalDateTime,
        endLocalDateTime: LocalDateTime
    ): Triple<Int, Int, Int> {

        val selectedDateInZeroTime = selectedDateTime.zeroTime()
        val startValue = if (selectedDateInZeroTime == startLocalDateTime.zeroTime()
        ) {
            startLocalDateTime.hour
        } else {
            0
        }
        val endValue = if (selectedDateInZeroTime == endLocalDateTime.zeroTime()) {
            endLocalDateTime.hour
        } else {
            23
        }
        val selectedIndex = selectedDateTime.hour - startValue

        return Triple(startValue, endValue, selectedIndex)
    }

    /**
     * 将当前时间的分钟值 与 开始时间、结束时间的分钟值相比较，返回筛选时应显示的三个值
     */
    fun getMinuteValue(
        selectedDateTime: LocalDateTime,
        startLocalDateTime: LocalDateTime,
        endLocalDateTime: LocalDateTime
    ): Triple<Int, Int, Int> {
        val selectedDateInZeroTimeKeepHour = selectedDateTime.zeroTimeAndKeepHour()
        val startValue =
            if (selectedDateInZeroTimeKeepHour == startLocalDateTime.zeroTimeAndKeepHour()) {
                startLocalDateTime.minute
            } else {
                0
            }
        val endValue =
            if (selectedDateInZeroTimeKeepHour == endLocalDateTime.zeroTimeAndKeepHour()) {
                endLocalDateTime.minute
            } else {
                59
            }
        val selectedIndex = selectedDateTime.minute - startValue
        return Triple(startValue, endValue, selectedIndex)
    }

    fun getSecondValue(
        selectedDateTime: LocalDateTime,
        startLocalDateTime: LocalDateTime,
        endLocalDateTime: LocalDateTime
    ): Triple<Int, Int, Int> {

        val selectedDateInZeroTimeKeepHourMinute = selectedDateTime.zeroTimeAndKeepHourAndMinute()
        val startValue =
            if (selectedDateInZeroTimeKeepHourMinute == startLocalDateTime.zeroTimeAndKeepHourAndMinute()) {
                startLocalDateTime.minute
            } else {
                0
            }
        val endValue =
            if (selectedDateInZeroTimeKeepHourMinute == endLocalDateTime.zeroTimeAndKeepHourAndMinute()) {
                endLocalDateTime.minute
            } else {
                59
            }
        val selectedIndex = selectedDateTime.second - startValue
        return Triple(startValue, endValue, selectedIndex)
    }

//</editor-fold>

    //<editor-fold desc="Formatter related">
    var defaultMonthFormatter by lazyMutable {
        DateTimeFormatterBuilder().appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
            .toFormatter(java.util.Locale.getDefault())
    }

    fun getMonthDisplayText(LocalDateTime: LocalDateTime, language: String): String {
        if (!language.equals(defaultMonthFormatter.locale.language, true)) {
            defaultMonthFormatter = defaultMonthFormatter.withLocale(java.util.Locale.getDefault())
        }
        return defaultMonthFormatter.format(LocalDateTime)
    }

    fun getDefaultTimeDisplayText(timeValue:Int):String{
        return timeValue.toString().padStart(2, '0')
    }

    /**
     * 默认情况下的滚轮数据文字显示的构建方法
     */
    fun getDefaultItemText(
        wheelIndex: Int,
        startValue: Int,
        selectedDateTime: LocalDateTime,
        language: String,
    ): ((Int) -> String) {
        return when (wheelIndex) {
            0 -> {
                if (language.equals("zh", ignoreCase = true)) {
                    { "${startValue + it}年" }
                }else{
                    { (startValue + it).toString() }
                }
            }

            1 -> {
                // the month-of-year,from 1 to 12
                {
                    getMonthDisplayText(
                        selectedDateTime.withMonth(startValue + it),
                        language
                    )
                }
            }

            2 -> {
                // the day-of-month, from 1 to 31

                if (language.equals("zh", ignoreCase = true)) {
                    { "${startValue + it}日" }
                }else{
                    { (startValue + it).toString() }
                }
            }

            3 -> {
                //the hour-of-day, from 0 to 23
                {
                    getDefaultTimeDisplayText(startValue + it)
                }
            }

            4 -> {
                //the minute-of-hour, from 0 to 59
                {
                    getDefaultTimeDisplayText(startValue + it)
                }
            }

            5 -> {
                //the second-of-minute, from 0 to 59
                {
                    getDefaultTimeDisplayText(startValue + it)
                }
            }

            else -> {
                { "" }
            }
        }

    }

    //</editor-fold>

    fun LocalDateTime.zeroTime(): LocalDateTime {
        return this.with(LocalTime.MIN)
    }

    fun LocalDateTime.zeroTimeAndKeepHour(): LocalDateTime {
        return this.with(LocalTime.MIN.plusHours(this.hour.toLong()))
    }

    fun LocalDateTime.zeroTimeAndKeepHourAndMinute(): LocalDateTime {
        return this.with(
            LocalTime.MIN.plusHours(this.hour.toLong())
                .plusMinutes(this.minute.toLong())
        )
    }

    fun LocalDateTime.toEpochMilli() =
        this.toInstant(OffsetDateTime.now().offset).toEpochMilli()
}