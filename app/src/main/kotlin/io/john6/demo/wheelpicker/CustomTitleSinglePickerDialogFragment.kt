package io.john6.demo.wheelpicker

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.john6.base.compose.picker.dialog.single.JSinglePickerDialogFragment

/**
 * Example to show how to customize the title of the dialog
 */
class CustomTitleSinglePickerDialogFragment : JSinglePickerDialogFragment() {
    @Composable
    override fun ColumnScope.DefaultPickerHeader(
        title: String,
        confirmImgVector: ImageVector?,
        confirmImgPainter: Painter?,
        confirmText: String,
        onSubmit: () -> Unit
    ) {
        mViewModel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Custom title", modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            IconButton(onClick = onSubmit) {
                Icon(
                    painter = painterResource(io.john6.base.compose.jwheelpicker.R.drawable.ic_done_24dp_from_j_picker),
                    contentDescription = "Custom title",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}