package io.john6.johnbase.compose.picker.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import android.graphics.Color as androidColor
import io.john6.johnbase.compose.JohnAppTheme
import io.john6.johnbase.compose.picker.JPickerOverlayStyle
import io.john6.johnbase.compose.picker.JWheelPickerHelper
import io.john6.johnbase.compose.picker.JWheelPickerHelper.drawPickerLineOverlay
import io.john6.johnbase.compose.picker.JWheelPickerHelper.drawPickerRectOverlay
import io.john6.johnbase.compose.spaceLarge
import io.john6.johnbase.compose.spaceMedium
import io.john6.johnbase.compose.ui.JElevationOverlayInBothLightAndDarkMode
import io.john6.johnbase.compose.ui.jSurfaceColorAtElevation
import io.john6.johnbase.jwheelpicker.R

/**
 * DialogFragment for custom Picker
 */
open class JBasePickerDialogFragment : DialogFragment() {

    override fun getTheme() = R.style.JPickerDialogTheme

    /**
     * TODO to be implemented
     */
    private val isDraggable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JohnAppTheme {
                    JBottomSheet()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            WindowCompat.setDecorFitsSystemWindows(this, false)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(androidColor.TRANSPARENT))
        }
    }

    /**
     * BottomSheet 由外部遮罩区域、手势导致的弹窗消失
     */
    @OptIn(ExperimentalMaterialApi::class)
    internal open fun confirmStateChange(pendingValue: ModalBottomSheetValue): Boolean {
        return isCancelable
    }

    internal open fun onSubmit() {
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    internal open fun JBottomSheet() {
        val sheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Expanded,
            skipHalfExpanded = true,
            confirmValueChange = this::confirmStateChange
        )

        if (!sheetState.isVisible && isCancelable) {
            LaunchedEffect(sheetState) {
                dismiss()
            }
        }
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetElevation = 1.dp,
            sheetShape = MaterialTheme.shapes.large.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp)
            ),
            scrimColor = Color.Transparent,
            content = {},
            sheetContent = {
                ContentView()
            },
        )
    }

    @Composable
    internal open fun ContentView() {

    }

    @Composable
    internal open fun DefaultPickerHeader(
        title: String,
        imageVector: ImageVector = Icons.Outlined.Send,
        onSubmit: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spaceLarge,
                    vertical = MaterialTheme.spaceMedium
                )
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
            IconButton(onClick = onSubmit, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(imageVector = imageVector, contentDescription = "Submit")
            }
        }
    }

    @Composable
    internal open fun rememberDefaultOverlayStyle(@JPickerOverlayStyle overlayStyle:Int): (ContentDrawScope.(Int, Float) -> Unit)? {

        val scrimColor = jSurfaceColorAtElevation(
            color = MaterialTheme.colors.surface,
            elevationOverlay = LocalElevationOverlay.current,
            absoluteElevation = 1.dp
        )

        val fillColor = jSurfaceColorAtElevation(
            color = MaterialTheme.colors.surface,
            elevationOverlay = JElevationOverlayInBothLightAndDarkMode,
            absoluteElevation = 8.dp
        )
        val lineColor = MaterialTheme.colors.onBackground.copy(alpha = 0.1f)

        val horizontalOverlayPadding = with(LocalDensity.current){ (16.dp).toPx() }
        val verticalOverlayPadding = with(LocalDensity.current){ 0 - (4.dp).toPx() }
        val overlayRadius = with(LocalDensity.current){ (8.dp).toPx() }

        val drawOverLay: (ContentDrawScope.(itemHeightPx: Int, edgeOffsetYPx: Float) -> Unit)? =
            remember {
                { itemHeightPx, edgeOffsetYPx ->
                    if (overlayStyle == JWheelPickerHelper.overlayStyleOvalRectangle) {
                        drawPickerRectOverlay(
                            edgeOffsetYPx = edgeOffsetYPx,
                            itemHeightPx = itemHeightPx,
                            scrimColor = scrimColor,
                            fillColor = fillColor,
                            radius = overlayRadius,
                            horizontalPadding = horizontalOverlayPadding,
                            verticalPadding = verticalOverlayPadding,
                        )
                    } else {
                        drawPickerLineOverlay(
                            edgeOffsetYPx = edgeOffsetYPx,
                            itemHeightPx = itemHeightPx,
                            scrimColor = scrimColor,
                            lineColor = lineColor,
                            verticalPadding = verticalOverlayPadding,
                        )
                    }
                }
            }
        return drawOverLay
    }

    @Composable
    fun getDialogTitle(title: Pair<Int, String>): String {
        return if (title.first == 0) {
            title.second
        } else {
            stringResource(title.first)
        }
    }
}