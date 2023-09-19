package io.john6.johnbase.compose.picker

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
import java.io.Serializable
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.util.Locale
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * 工具方法
 *
 * @author Liu Qiang
 * @version v3.7.0
 * @since 2023-03-14
 *
 */
object JWheelPickerHelper:Serializable {
    const val overlayStyleOvalRectangle = 0
    const val overlayStyleLine = 1

    //<editor-fold desc="Formatter related">
    var defaultMonthFormatter by lazyMutable {
        DateTimeFormatterBuilder().appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
            .toFormatter(Locale.getDefault())
    }

    fun getMonthDisplayText(zonedDateTime: ZonedDateTime, language: String): String {
        if (!language.equals(defaultMonthFormatter.locale.language, true)) {
            defaultMonthFormatter = defaultMonthFormatter.withLocale(Locale.getDefault())
        }
        return defaultMonthFormatter.format(zonedDateTime)
    }

    fun getDefaultTimeDisplayText(timeValue:Int):String{
        return timeValue.toString().padStart(2, '0')
    }
    //</editor-fold>

    /**
     * 绘制分割线风格的遮罩
     */
    fun ContentDrawScope.drawPickerLineOverlay(
        edgeOffsetYPx: Float,
        itemHeightPx: Int,
        scrimColor: Color = Color.White,
        lineColor: Color = Color.Gray,
        lineWidth: Float = 2f,
        horizontalPadding:Float = 0f,
        verticalPadding:Float = 0f,
        radius:Float = 0f,
    ) {
        val w = this.size.width
        val h = this.size.height
        val scrimHeight = edgeOffsetYPx + verticalPadding
        val highlightHeight = itemHeightPx - verticalPadding * 2f
        val path = getCenterItemPath(
            width = w,
            height = h,
            itemHeightPx = itemHeightPx,
            edgeOffsetY = edgeOffsetYPx,
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
            radius = radius
        )
        this.drawPath(path, scrimColor, alpha = 0.7f)
        this.drawLine(
            color = lineColor,
            start = Offset(0f, scrimHeight),
            end = Offset(w, scrimHeight),
            strokeWidth = lineWidth
        )
        this.drawLine(
            color = lineColor,
            start = Offset(0f, scrimHeight + highlightHeight),
            end = Offset(w, scrimHeight + highlightHeight),
            strokeWidth = lineWidth
        )
    }

    /**
     * 绘制矩形框风格的遮罩
     */
    fun ContentDrawScope.drawPickerRectOverlay(
        edgeOffsetYPx: Float,
        itemHeightPx: Int,
        scrimColor: Color = Color.White,
        fillColor: Color = Color.White,
        horizontalPadding: Float = 0f,
        verticalPadding: Float = 0f,
        radius: Float = 0f,
    ) {
        val w = this.size.width
        val h = this.size.height

        val scrimHeight = edgeOffsetYPx + verticalPadding
        val highlightHeight = itemHeightPx - verticalPadding * 2f
//        val colors = listOf(
//            scrimColor.copy(alpha = 1f),
//            scrimColor.copy(alpha = 0.8f),
//            scrimColor.copy(alpha = 0.6f)
//        )
//        FIXME 暗色模式下有明显的渐变色块
//        this.drawRect(
//            brush = Brush.verticalGradient(
//                colors = colors,
//                startY = 0f,
//                endY = scrimHeight
//            ),
//            size = Size(w, scrimHeight),
//        )
//        this.drawRect(
//            brush = Brush.verticalGradient(
//                colors = colors,
//                startY = h,
//                endY = h - scrimHeight
//            ),
//            topLeft = Offset(x = 0f, y = scrimHeight + highlightHeight),
//            size = Size(w, scrimHeight),
//        )
        val path = getCenterItemPath(
            width = w,
            height = h,
            itemHeightPx = itemHeightPx,
            edgeOffsetY = edgeOffsetYPx,
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
            radius = radius
        )
        this.drawPath(path, scrimColor, alpha = 0.7f)

        this.drawRoundRect(
            color = fillColor.copy(alpha = 0.4f),
            topLeft = Offset(x = horizontalPadding, y = scrimHeight),
            size = Size(w - horizontalPadding * 2f, highlightHeight),
            cornerRadius = CornerRadius(radius),
            style = Fill,
        )
    }

    /**
     * 从整个可视区域中裁去中间 Item 所占区域，以 Path 形式返回
     */
    private fun getCenterItemPath(
        width: Float, height: Float,
        itemHeightPx: Int,
        edgeOffsetY: Float,
        horizontalPadding:Float = 0f,
        verticalPadding:Float = 0f,
        radius:Float = 0f
    ): Path {
        val wholeSizePath = Path().apply {
            addRect(Rect(0f,0f,width,height))
        }
        val focusPath = Path().apply {
            addRoundRect(RoundRect(horizontalPadding, edgeOffsetY+verticalPadding, width - horizontalPadding,edgeOffsetY + itemHeightPx - verticalPadding,
            radius, radius))
        }
        val resPath = Path()
        resPath.op(wholeSizePath,focusPath, PathOperation.Difference)
        return resPath
    }


    /**
     * 默认情况下的滚轮数据文字显示的构建方法
     */
    fun getDefaultItemText(
        wheelIndex: Int,
        startValue: Int,
        selectedDateTime: ZonedDateTime,
        language: String,
    ): ((Int) -> JWheelPickerItemInfo) {
        return {
            JWheelPickerItemInfo(startValue.toString(), it,"item$startValue")
        }
//        return when (wheelIndex) {
//            0 -> {
//                if (language.equals("zh", ignoreCase = true)) {
//                    { "${startValue + it}年" }
//                }else{
//                    { (startValue + it).toString() }
//                }
//            }
//
//            1 -> {
//                // the month-of-year,from 1 to 12
//                { getMonthDisplayText(selectedDateTime.withMonth(startValue + it), language) }
//            }
//
//            2 -> {
//                // the day-of-month, from 1 to 31
//
//                if (language.equals("zh", ignoreCase = true)) {
//                    { "${startValue + it}日" }
//                }else{
//                    { (startValue + it).toString() }
//                }
//            }
//
//            3 -> {
//                //the hour-of-day, from 0 to 23
//                {
//                    getDefaultTimeDisplayText(startValue + it)
//                }
//            }
//
//            4 -> {
//                //the minute-of-hour, from 0 to 59
//                {
//                    getDefaultTimeDisplayText(startValue + it)
//                }
//            }
//
//            5 -> {
//                //the second-of-minute, from 0 to 59
//                {
//                    getDefaultTimeDisplayText(startValue + it)
//                }
//            }
//
//            else -> {
//                { "" }
//            }
//        }

    }
}

class lazyMutable<T>(val initializer: () -> T):ReadWriteProperty<Any?,T> {
    private var currentValue:T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return currentValue ?: initializer().also { currentValue = it }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        currentValue = value
    }

}