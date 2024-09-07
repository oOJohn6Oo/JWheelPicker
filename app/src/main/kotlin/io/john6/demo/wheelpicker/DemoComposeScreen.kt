package io.john6.demo.wheelpicker

import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import io.john6.base.compose.picker.JWheelPicker
import io.john6.base.compose.picker.JWheelPickerArrangement
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo

@Composable
internal fun DemoComposeScreen(
    showSinglePicker: (withAdapter: Boolean) -> Unit,
    showMultiplePicker: () -> Unit,
    showDatePicker: () -> Unit,
    showMonthDayPicker: () -> Unit,
) {
    MaterialTheme {
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxSize(),
            contentPadding = WindowInsets.safeDrawing.add(WindowInsets(left = 16.dp, right = 16.dp))
                .asPaddingValues()
        ) {
            item {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = "JWheelPicker Demo",
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.h5
                )
            }

            item {
                Button(onClick = { showSinglePicker(false) }) {
                    Text(text = "Show Single Picker")
                }
            }
            item {
                Button(onClick = { showSinglePicker(true) }) {
                    Text(text = "Show Single Adapter Picker")
                }
            }
            item {
                Button(onClick = showMultiplePicker) {
                    Text(text = "Show Multiple Picker")
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                item {
                    Button(onClick = showDatePicker) {
                        Text(text = "Show Date Picker")
                    }
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    JWheelPicker(
                        modifier = Modifier.align(Alignment.Center),
                        size = 300.dp,
                        itemTextList = (0..10).map {
                            JWheelPickerItemInfo(
                                it.toString(),
                                it,
                                "item$it"
                            )
                        },
                        drawOverLay = null,
                        arrangement = JWheelPickerArrangement.Horizontal,
                        itemWidthDp = 60.dp,
                        selectedTextColor = MaterialTheme.colors.primary
                    )
                }
            }
            item {
                Button(onClick = showMonthDayPicker) {
                    Text(text = "ShowMonthDayPicker")
                }
            }
        }
    }
}