import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.ElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln
/**
 * Add padding for Bottom section of our view
 */
internal fun Modifier.bottomSafeDrawing(): Modifier = composed {
    this.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
}

/**
 * Some times We do not care the safe area at the horizontal edge, like in dialog
 */
internal fun Modifier.onlyBottomSafeDrawing(): Modifier = composed {
    this.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
}

@Composable
fun jSurfaceColorAtElevation(
    color: Color,
    elevationOverlay: ElevationOverlay?,
    absoluteElevation: Dp
): Color {
    return if (color == MaterialTheme.colors.surface && elevationOverlay != null) {
        elevationOverlay.apply(color, absoluteElevation)
    } else {
        color
    }
}


@ReadOnlyComposable
@Composable
fun calculateForegroundColor(backgroundColor: Color, elevation: Dp): Color {
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    val baseForegroundColor = contentColorFor(backgroundColor)
    return baseForegroundColor.copy(alpha = alpha)
}

object JElevationOverlayInBothLightAndDarkMode : ElevationOverlay {
    @ReadOnlyComposable
    @Composable
    override fun apply(color: Color, elevation: Dp): Color {
        val foregroundColor = calculateForegroundColor(color, elevation)
        return foregroundColor.compositeOver(color)
    }
}
