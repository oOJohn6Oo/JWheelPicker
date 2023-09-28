package io.john6.johnbase.compose.picker

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.LocalDateTime
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

    var fragmentResultKey = "result"

    /**
     * 绘制分割线风格的遮罩
     */
    fun ContentDrawScope.drawPickerLineOverlay(
        edgeOffsetYPx: Float,
        itemHeightPx: Int,
        scrimColor: Color = Color.White.copy(alpha = 0.7f),
        lineColor: Color = Color.Gray.copy(alpha = 0.1f),
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
        this.drawPath(path, scrimColor)
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
        scrimColor: Color = Color.White.copy(alpha = 0.7f),
        fillColor: Color = Color.Gray.copy(alpha = 0.4f),
        horizontalPadding: Float = 0f,
        verticalPadding: Float = 0f,
        radius: Float = 12f,
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
        this.drawPath(path, scrimColor)

        this.drawRoundRect(
            color = fillColor,
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