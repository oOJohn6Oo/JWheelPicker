package io.john6.demo.wheelpicker

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.Window
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import io.john6.base.compose.picker.JWheelPickerHelper
import io.john6.base.compose.picker.JWheelPickerHelper.fragmentResultKey
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import io.john6.base.compose.picker.dialog.date.JDatePickerDialogData
import io.john6.base.compose.picker.dialog.date.JDateWheelPickerDialogFragment
import io.john6.base.compose.picker.dialog.multiple.JMultiPickerDialogData
import io.john6.base.compose.picker.dialog.multiple.JMultiplePickerDialogFragment
import io.john6.base.compose.picker.dialog.TestMultipleJPickerAdapter
import io.john6.base.compose.picker.dialog.single.JSinglePickerDialogAdapterData
import io.john6.base.compose.picker.dialog.single.JSinglePickerDialogData
import io.john6.base.compose.picker.dialog.single.JSinglePickerDialogFragment
import java.io.Serializable
import java.time.LocalDateTime

class JWheelPickerDemoActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindow()
        setContentView(ComposeView(this).apply {
            setContent {
                val isDarkTheme = isSystemInDarkTheme()
                val view = LocalView.current
                LaunchedEffect(isDarkTheme) {
                    view.context.findWindow()?.apply {
                        WindowCompat.getInsetsController(this, view).apply {
                            isAppearanceLightStatusBars = !isDarkTheme
                            isAppearanceLightNavigationBars = !isDarkTheme
                        }
                    }
                }
                DemoComposeScreen(
                    showSinglePicker = this@JWheelPickerDemoActivity::showSinglePicker,
                    showMultiplePicker = this@JWheelPickerDemoActivity::showMultiplePicker,
                    showDatePicker = this@JWheelPickerDemoActivity::showDatePicker,
                    showMonthDayPicker = this@JWheelPickerDemoActivity::ShowMonthDayPicker,
                )
            }
        })

        JWheelPickerHelper.DefaultTheme = {
            MaterialTheme(shapes = Shapes, content = it)
        }

        initListener()
    }

    private fun setupWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
    }

    private fun initListener() {
        supportFragmentManager.setFragmentResultListener(JSinglePickerDialogFragment.TAG, this) { tag, bundle ->
            val isDismiss = bundle.getBoolean("dismiss", false)
            if(isDismiss){
                return@setFragmentResultListener
            }
            val result = bundle.getParcelableCompat(fragmentResultKey, JWheelPickerItemInfo::class.java)
            Toast.makeText(this, result?.id.toString(), Toast.LENGTH_SHORT).show()
        }
        supportFragmentManager.setFragmentResultListener(JMultiplePickerDialogFragment.TAG, this) { tag, bundle ->
            val isDismiss = bundle.getBoolean("dismiss", false)
            if(isDismiss){
                return@setFragmentResultListener
            }
            val result = bundle.getParcelableArrayCompat(fragmentResultKey, JWheelPickerItemInfo::class.java)
            Toast.makeText(this, result?.joinToString { it.id }, Toast.LENGTH_SHORT)
                .show()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            supportFragmentManager.setFragmentResultListener(JDateWheelPickerDialogFragment.TAG, this) { tag, bundle ->
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
                    selectTextColorResId = android.R.color.holo_blue_dark,
                    adapterClass = TestMultipleJPickerAdapter::class.java,
                    adapterParamsAsBundle = Bundle().apply {
                        putInt("wheelCount", 1)
                        putIntArray("initialIndexes", intArrayOf(9))
                    },
                ),
            )
        } else {
            JSinglePickerDialogFragment.show(
                supportFragmentManager,
                requiredData = JSinglePickerDialogData(
                    title = 0 to "SinglePicker",
                    initialIndex = 20,
                    dataList = (0..30).map { JWheelPickerItemInfo(it.toString(), it, "item$it") },
                    overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_RECTANGLE
                ),
            )
        }
    }

    private fun showMultiplePicker() {
        JMultiplePickerDialogFragment.show(
            supportFragmentManager,
            requiredData = JMultiPickerDialogData(
                title = 0 to "MultiplePicker",
                overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_LINE,
                adapterClass = TestMultipleJPickerAdapter::class.java,
            ),
        )
    }

    private fun ShowMonthDayPicker() {
        JMultiplePickerDialogFragment.show(
            supportFragmentManager,
            requiredData = JMultiPickerDialogData(
                title = 0 to "MonthDayPicker",
                overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_RECTANGLE,
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
                    containerHorizontalPaddingInDp = 16
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

    @Suppress("DEPRECATION")
    private fun <T : Parcelable> Bundle.getParcelableCompat(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelable(key, clazz)
        } else {
            getParcelable(key) as? T
        }
    }

    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    private fun <T : Serializable> Bundle.getSerializableCompat(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, clazz)
        } else {
            getSerializable(key) as? T
        }
    }

    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    private fun <T : Parcelable> Bundle.getParcelableArrayCompat(
        key: String,
        clazz: Class<T>
    ): Array<T>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArray(key, clazz)
        } else {
            getParcelableArray(key) as? Array<T>
        }
    }

    private fun Context.findWindow(): Window? {
        return when (this) {
            is Activity -> window
            is ContextWrapper -> baseContext.findWindow()
            else -> null
        }
    }
}
