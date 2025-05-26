package io.john6.demo.wheelpicker

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.fragment.app.FragmentActivity
import io.john6.base.compose.picker.JWheelPickerHelper
import io.john6.base.compose.picker.JWheelPickerHelper.fragmentResultKey
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import io.john6.base.compose.picker.dialog.TestMultipleJPickerAdapter
import io.john6.base.compose.picker.dialog.date.JDatePickerDialogData
import io.john6.base.compose.picker.dialog.date.JDateWheelPickerDialogFragment
import io.john6.base.compose.picker.dialog.multiple.JMultiPickerDialogData
import io.john6.base.compose.picker.dialog.multiple.JMultiplePickerDialogFragment
import io.john6.base.compose.picker.dialog.single.JSinglePickerDialogAdapterData
import io.john6.base.compose.picker.dialog.single.JSinglePickerDialogData
import io.john6.base.compose.picker.dialog.single.JSinglePickerDialogFragment
import java.time.LocalDateTime

class JWheelPickerDemoActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindow()
        setContentView(ComposeView(this).apply {
            setContent {
                DemoTheme {
                    DemoComposeScreen(
                        showSinglePicker = this@JWheelPickerDemoActivity::showSinglePicker,
                        showMultiplePicker = this@JWheelPickerDemoActivity::showMultiplePicker,
                        showDatePicker = this@JWheelPickerDemoActivity::showDatePicker,
                        showMonthDayPicker = this@JWheelPickerDemoActivity::showMonthDayPicker,
                    )
                }
            }
        })

        JWheelPickerHelper.DefaultTheme = { DemoTheme(it) }

        initListener()
    }

    private fun initListener() {
        supportFragmentManager.setFragmentResultListener(JSinglePickerDialogFragment.TAG, this) { _, bundle ->
            val isDismiss = bundle.getBoolean("dismiss", false)
            if(isDismiss){
                return@setFragmentResultListener
            }
            val result = bundle.getParcelableCompat(fragmentResultKey, JWheelPickerItemInfo::class.java)
            Toast.makeText(this, result?.id.toString(), Toast.LENGTH_SHORT).show()
        }
        supportFragmentManager.setFragmentResultListener(JMultiplePickerDialogFragment.TAG, this) { _, bundle ->
            val isDismiss = bundle.getBoolean("dismiss", false)
            if(isDismiss){
                return@setFragmentResultListener
            }
            val result = bundle.getParcelableArrayCompat(fragmentResultKey, JWheelPickerItemInfo::class.java)
            Toast.makeText(this, result?.joinToString { it.id }, Toast.LENGTH_SHORT)
                .show()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            supportFragmentManager.setFragmentResultListener(JDateWheelPickerDialogFragment.TAG, this) { _, bundle ->
                val isDismiss = bundle.getBoolean("dismiss", false)
                if(isDismiss){
                    return@setFragmentResultListener
                }
                val result = bundle.getSerializableCompat(fragmentResultKey, LocalDateTime::class.java)
                Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showSinglePicker(withAdapter: Boolean = false) {
        if (withAdapter) {
            JSinglePickerDialogFragment.show(
                supportFragmentManager,
                requiredData = JSinglePickerDialogAdapterData(
                    title = 0 to "SingleAdapterPicker",
                    overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_RECTANGLE,
                    selectTextColor = { MaterialTheme.colors.primary },
                    isDraggable = false,
                    adapterClass = TestMultipleJPickerAdapter::class.java,
                    adapterParamsAsBundle = Bundle().apply {
                        putInt("wheelCount", 1)
                        putIntArray("initialIndexes", intArrayOf(9))
                    },
                ),
            )
        } else {
            val fg = CustomTitleSinglePickerDialogFragment()
            fg.arguments = Bundle().apply {
                putParcelable("data", JSinglePickerDialogData(
                    title = 0 to "SinglePicker",
                    selectTextColor = { MaterialTheme.colors.primary },
                    isDraggable = false,
                    initialIndex = 20,
                    dataList = (0..30).map { JWheelPickerItemInfo(it.toString(), it, "item$it") },
                    overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_RECTANGLE
                ))
            }
            fg.show(supportFragmentManager, JSinglePickerDialogFragment.TAG)
        }
    }

    private fun showMultiplePicker() {
        JMultiplePickerDialogFragment.show(
            supportFragmentManager,
            requiredData = JMultiPickerDialogData(
                title = 0 to "MultiplePicker",
                overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_LINE,
                adapterClass = TestMultipleJPickerAdapter::class.java,
                selectTextColor = { MaterialTheme.colors.primary },
            ),
        )
    }

    private fun showMonthDayPicker() {
        JMultiplePickerDialogFragment.show(
            supportFragmentManager,
            requiredData = JMultiPickerDialogData(
                title = 0 to "MonthDayPicker",
                overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_RECTANGLE,
                selectTextColor = { MaterialTheme.colors.primary },
                adapterClass = MonthDayPickerAdapter::class.java,
                adapterParamsAsBundle = Bundle().apply {
                    val currentMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDateTime.now().monthValue - 1
                    } else {
                        8 - 1
                    }
                    val currentDay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDateTime.now().dayOfMonth - 1
                    } else {
                        8 - 1
                    }
                    putIntArray("initialIndexes", intArrayOf(currentMonth, currentDay))
                }
            ),
        )

    }

    private fun showDatePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            JDateWheelPickerDialogFragment.show(
                supportFragmentManager,
                requiredData = JDatePickerDialogData(
                    title = 0 to "DatePicker",
                    overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_RECTANGLE,
                    containerHorizontalPaddingInDp = 16,
                    selectTextColor = { MaterialTheme.colors.primary },
                ),
            )
        } else {
            Toast.makeText(
                this,
                "need Build.VERSION.SDK_INT >= Build.VERSION_CODES.O",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}