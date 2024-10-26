package io.john6.base.compose.picker

import android.view.HapticFeedbackConstants
import androidx.annotation.FloatRange
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.john6.base.compose.picker.JWheelPickerHelper.drawPickerLineOverlay
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import io.john6.base.compose.picker.bean.getText
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

enum class JWheelPickerArrangement {
    Horizontal, Vertical
}

/**
 * iOS style data picker
 *
 * @param enableHapticFeedback 是否启用震动
 * @param hapticFeedBackThreshold  在可震动 OffsetX/Y 范围
 * @param itemTextList item 文字列表
 */
@Composable
fun JWheelPicker(
    modifier: Modifier = Modifier,
    arrangement: JWheelPickerArrangement = JWheelPickerArrangement.Vertical,
    size: Dp = 240.dp,
    itemWidthDp: Dp = Dp.Unspecified,
    itemPadding: Dp = 4.dp,
    selectedTextColor: Color = Color.Unspecified,
    enableHapticFeedback: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    hapticFeedBackThreshold: Float = 20f,
    itemTextList: List<JWheelPickerItemInfo>,
    initialIndex: Int = 0,
    drawOverLay: (ContentDrawScope.(itemHeightPx: Int, edgeOffsetYPx: Float) -> Unit)? = { itemHeightPx, edgeOffsetYPx ->
        drawPickerLineOverlay(edgeOffsetYPx = edgeOffsetYPx, itemHeightPx = itemHeightPx)
    },
    onSelectedItemChanged: ((itemData: JWheelPickerItemInfo) -> Unit)? = null,
) {
    JWheelPicker(
        modifier = modifier,
        arrangement = arrangement,
        size = size,
        itemWidthDp = itemWidthDp,
        itemPadding = itemPadding,
        selectedTextColor = selectedTextColor,
        enableHapticFeedback = enableHapticFeedback,
        textStyle = textStyle,
        confirmSelectDistanceThreshold = hapticFeedBackThreshold,
        itemCount = itemTextList.size,
        itemData = { itemTextList[it] },
        initialIndex = initialIndex,
        drawOverLay = drawOverLay,
        onSelectedItemChanged = onSelectedItemChanged
    )
}

/**
 * iOS style data picker
 *
 * @param enableHapticFeedback vibrate using [android.view.View.performHapticFeedback]
 * @param confirmSelectDistanceThreshold  item will be selected while item centerY in [picker center - this , picker center + this]
 * @param itemCount 可滚动的 Item 数量
 * @param itemData a function which take a index param return a [JWheelPickerItemInfo]
 *
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JWheelPicker(
    modifier: Modifier = Modifier,
    arrangement: JWheelPickerArrangement = JWheelPickerArrangement.Vertical,
    size: Dp = 240.dp,
    itemWidthDp: Dp = Dp.Unspecified,
    itemPadding: Dp = 4.dp,
    selectedTextColor: Color = Color.Unspecified,
    enableHapticFeedback: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    confirmSelectDistanceThreshold: Float = 20f,
    itemCount: Int,
    itemData: (index: Int) -> JWheelPickerItemInfo,
    initialIndex: Int = 0,
    drawOverLay: (ContentDrawScope.(itemHeightPx: Int, edgeOffsetYPx: Float) -> Unit)? = { itemHeightPx, edgeOffsetYPx ->
        drawPickerLineOverlay(edgeOffsetYPx = edgeOffsetYPx, itemHeightPx = itemHeightPx)
    },
    onSelectedItemChanged: ((itemData: JWheelPickerItemInfo) -> Unit)? = null,
) {
    if ((arrangement == JWheelPickerArrangement.Horizontal) && itemWidthDp == Dp.Unspecified) {
        throw RuntimeException("Item width must be specified when arrangement is horizontal")
    }

    val localView = LocalView.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val isDragged = lazyListState.interactionSource.collectIsDraggedAsState().value
    val isScrolling = lazyListState.isScrollInProgress

    // Item 高度
    val fontSize = textStyle.fontSize.takeIf { it != TextUnit.Unspecified } ?: 14.sp
    val itemHeightPx by remember(fontSize, itemPadding) {
        mutableIntStateOf(with(density) { (fontSize.toPx() + (itemPadding * 2).toPx()).roundToInt() })
    }
    // 边缘 Item 移动到中心需要的偏移量
    val edgeOffsetPx = remember(size, itemHeightPx, itemWidthDp) {
        when (arrangement) {
            JWheelPickerArrangement.Vertical ->
                (with(density) { size.toPx() } - itemHeightPx) / 2f
            JWheelPickerArrangement.Horizontal ->
                (with(density) { size.toPx() - itemWidthDp.toPx() }) / 2f
        }
    }
    val edgeOffsetDp = remember(size) {
        with(density) { edgeOffsetPx.absoluteValue.toDp() }
    }

    LaunchedEffect(key1 = itemCount){
        if(itemCount == 0){
            onSelectedItemChanged?.invoke(JWheelPickerItemInfo.EMPTY)
        }else{
            onSelectedItemChanged?.invoke(itemData(initialIndex))
        }
    }

    var currentSelectedItemIndex by remember { mutableIntStateOf(initialIndex) }
    var desireSelectedItemIndex by remember { mutableIntStateOf(initialIndex) }

    val animateScrollToItemCenter: (Pair<Int, Float>) -> Unit = remember(onSelectedItemChanged) {
        { centerItemInfo ->
            val centerItemIndex = centerItemInfo.first
            if (centerItemIndex >= 0) {
                if (currentSelectedItemIndex != centerItemIndex) {
                    currentSelectedItemIndex = centerItemIndex
                    onSelectedItemChanged?.invoke(itemData(currentSelectedItemIndex))
                }
                scope.launch { lazyListState.animateScrollToItem(centerItemIndex) }
            }
        }
    }

    val performHapticFeedback = {
        if (enableHapticFeedback) {
            scope.launch {
                if (!localView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)) {
                    localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            }
        }
    }

    val getLayoutInfo =  {
        lazyListState.layoutInfo
    }


    // 最终文字样式
    val desireTextStyle: (Int) -> TextStyle = if(selectedTextColor == Color.Unspecified){
        // 未设置选中文字颜色，则直接为不可变的方法
        {
            textStyle
        }
    }else{ // 设置了选中文字颜色，则需要根据选中下标判断是否需要变色
        remember(initialIndex, currentSelectedItemIndex, selectedTextColor, textStyle.color) {
            {
                val finalColor = if (it != currentSelectedItemIndex) {
                    textStyle.color
                } else {
                    selectedTextColor
                }
                textStyle.copy(color = finalColor)
            }
        }
    }

    var needPerformSnapScroll by remember { mutableStateOf(false) }

    var hasPerformHapticFeedback by remember {
        mutableStateOf(true)
    }

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        when (arrangement) {
            JWheelPickerArrangement.Vertical -> LazyColumn(
                modifier = modifier
                    .height(size)
                    .disableParentNestedVerticalScroll()
                    .drawWithContent {
                        this.drawContent()
                        drawOverLay?.invoke(this, itemHeightPx, edgeOffsetPx)
                    },
                state = lazyListState,
//                flingBehavior = rememberJMaxScrollFlingBehavior(4000f),
                contentPadding = PaddingValues(vertical = edgeOffsetDp),
            ) {
                items(itemCount, key = { itemData(it) }) {
                    VerticalWheelPickerItem(
                        index = it,
                        itemHeightDp = with(density) { itemHeightPx.toDp() },
                        text = itemData(it).getText(),
                        textStyle = desireTextStyle(it),
                        getLayoutInfo = getLayoutInfo,
                    )
                }
            }

            JWheelPickerArrangement.Horizontal -> LazyRow(
                modifier = modifier
                    .width(size)
                    .disableParentNestedHorizontalScroll()
                    .drawWithContent {
                        this.drawContent()
                        drawOverLay?.invoke(this, itemHeightPx, edgeOffsetPx)
                    },
                state = lazyListState,
//                flingBehavior = rememberJMaxScrollFlingBehavior(4000f),
                contentPadding = PaddingValues(horizontal = edgeOffsetDp),
            ) {
                items(itemCount, key = { itemData(it) }) {
                    HorizontalWheelPickerItem(
                        index = it,
                        itemSizeDp = DpSize(
                            width = itemWidthDp,
                            height = with(density) { itemHeightPx.toDp() }
                        ),
                        text = itemData(it).getText(),
                        textStyle = desireTextStyle(it),
                        getLayoutInfo = getLayoutInfo,
                    )
                }
            }
        }

    }

    if (isScrolling) {
        val centerItemInfo = getCurrentCenterItemInfo(getLayoutInfo)
        if (centerItemInfo.first != desireSelectedItemIndex) {
            desireSelectedItemIndex = centerItemInfo.first
            hasPerformHapticFeedback = false
        }

        // 当前选中值变化 && OffsetY 在可震动范围内
        if (!hasPerformHapticFeedback && centerItemInfo.second.absoluteValue <= confirmSelectDistanceThreshold) {
            hasPerformHapticFeedback = true
            performHapticFeedback()
        }
        if (isDragged) {
            needPerformSnapScroll = true
        }
    } else {
        if (needPerformSnapScroll && !isDragged) {  // 未在滚动 && 用户未拖动(手指不在屏幕上)
            needPerformSnapScroll = false
            animateScrollToItemCenter(desireSelectedItemIndex to 0f)
        }
    }

}

/**
 * Render a Item of each Picker
 *
 * @param index index of this item, use this to know if this item is visible or not
 * @param text text shows inside this item
 * @param itemHeightDp height of this item
 * @param textStyle text style of this item
 * @param getLayoutInfo get this layoutInfo of this LazyList
 */
@Composable
private fun VerticalWheelPickerItem(
    index: Int,
    text: String,
    itemHeightDp: Dp,
    textStyle: TextStyle,
    getLayoutInfo: () -> LazyListLayoutInfo,
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(itemHeightDp)
        .graphicsLayer {
            render3DVerticalItemEffect(
                index,
                getLayoutInfo
            )
        }) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .align(Alignment.Center),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = textStyle,
        )
    }
}

@Composable
private fun HorizontalWheelPickerItem(
    index: Int,
    text: String,
    itemSizeDp: DpSize,
    textStyle: TextStyle,
    getLayoutInfo: () -> LazyListLayoutInfo,
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .size(itemSizeDp)
        .graphicsLayer {
            render3DHorizontalItemEffect(index, getLayoutInfo)
        }) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .align(Alignment.Center),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = textStyle,
        )
    }
}

/**
 * Implementation of a 3D item, do rotationX, scaleX and translationY to this GraphicsLayer
 */
private fun GraphicsLayerScope.render3DVerticalItemEffect(
    index: Int,
    getLayoutInfo: () -> LazyListLayoutInfo,
) {
    val layoutInfo = getLayoutInfo()
    val itemInfo = layoutInfo.visibleItemsInfo.find { i -> i.index == index }
        ?: return

    // Item Y 坐标
    val posY = getItemCenter(itemInfo) + layoutInfo.beforeContentPadding
    val centerY = layoutInfo.viewportSize.height / 2F
    // Item 偏移量
    val offset = (posY - centerY) / centerY
    // 此时 item rotateY 已经到了看不见的位置，再显示也没有意义了
    if (offset.absoluteValue >= 1.0f) {
        alpha = 0f
        return
    }

    rotationX = -90 * offset

    val scale = 1 - (offset.absoluteValue).pow(2) * 0.37f
    scaleX = scale

    translationY = if (offset == 0f) {
        0f
    } else {
        // 半径
        val r = (2f * centerY / Math.PI).toFloat()
        // 视觉上的 Y 的坐标位置
        val h =
            (sin(Math.toRadians(offset.absoluteValue * 90.0)) * r * JWheelPickerHelper.defaultVerticalWheelCurveRate).toFloat()
        val diffY = if (offset < 0) {
            (centerY - h.absoluteValue) - posY.absoluteValue
        } else {
            (centerY + h.absoluteValue) - posY.absoluteValue
        }
        diffY
    }
}

private fun GraphicsLayerScope.render3DHorizontalItemEffect(
    index: Int,
    getLayoutInfo: () -> LazyListLayoutInfo,
) {
    val layoutInfo = getLayoutInfo()
    val itemInfo = layoutInfo.visibleItemsInfo.find { i -> i.index == index }
        ?: return

    // Item X 坐标
    val posX = getItemCenter(itemInfo) + layoutInfo.beforeContentPadding
    val centerX = layoutInfo.viewportSize.width / 2F
    // Item 偏移量
    val offset = (centerX - posX) / centerX
    // 此时 item rotateX 已经到了看不见的位置，再显示也没有意义了
    if (offset.absoluteValue >= 1.0f) {
        alpha = 0f
        return
    }

    rotationY = -90 * offset

    val scale = 1 - (offset.absoluteValue).pow(2) * 0.37f
    scaleY = scale

    translationX = if (offset == 0f) {
        0f
    } else {
        // 半径
        val r = (2f * centerX / Math.PI).toFloat()
        // 视觉上的 Y 的坐标位置
        val h =
            (sin(Math.toRadians(offset.absoluteValue * 90.0)) * r * JWheelPickerHelper.defaultHorizontalWheelCurveRate).toFloat()
        val diffX = if (offset > 0) {
            (centerX - h.absoluteValue) - posX.absoluteValue
        } else {
            (centerX + h.absoluteValue) - posX.absoluteValue
        }
        diffX
    }
}

/**
 * 获取当前 Item 中心点在Y轴的偏移值
 */
private fun getItemCenter(itemInfo: LazyListItemInfo): Float {
    val itemCenter = itemInfo.size / 2F
    return itemInfo.offset.toFloat() + itemCenter
}

/**
 * 获取当前离控件中心最近的 Item 的下标
 *
 * @return first: 离中间最近的 Item Index, second: diffY
 */
private fun getCurrentCenterItemInfo(
    getLayoutInfo: () -> LazyListLayoutInfo
): Pair<Int, Float> {
    return getLayoutInfo().run {
        val center = (viewportEndOffset - viewportStartOffset) / 2F
        var res = -1
        var minDiff = Float.MAX_VALUE
        visibleItemsInfo.forEach {
            val tempOffsetPos = getItemCenter(it) + beforeContentPadding - center
            if (tempOffsetPos.absoluteValue < minDiff.absoluteValue) {
                minDiff = tempOffsetPos
                res = it.index
            }
        }
        res to minDiff
    }
}

private fun Modifier.disableParentNestedHorizontalScroll(disabled: Boolean = true) =
    if (disabled) this.nestedScroll(HorizontalParentScrollConsumer) else this

private val HorizontalParentScrollConsumer = object : NestedScrollConnection {

    override suspend fun onPostFling(consumed: Velocity, available: Velocity) = available

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = available
}


private fun Modifier.disableParentNestedVerticalScroll(disabled: Boolean = true) =
    if (disabled) this.nestedScroll(VerticalParentScrollConsumer) else this

private val VerticalParentScrollConsumer = object : NestedScrollConnection {

    override suspend fun onPostFling(consumed: Velocity, available: Velocity) = available

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = available
}
