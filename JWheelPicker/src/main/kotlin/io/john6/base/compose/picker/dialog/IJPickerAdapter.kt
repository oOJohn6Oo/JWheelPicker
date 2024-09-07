package io.john6.base.compose.picker.dialog

import android.os.Bundle
import androidx.annotation.Keep
import io.john6.base.compose.picker.bean.JWheelPickerInfo
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo

/**
 * Adapter for generate data for [io.john6.base.compose.picker.JMultiWheelPicker]
 */
interface IJPickerAdapter {
    val wheelCount: Int
    val initialIndexes: IntArray
    fun key(wheelIndex: Int, selectedIndexes: List<Int>): Any
    fun generateJWheelPickerInfo(wheelIndex: Int, selectedIndexes: List<Int>): JWheelPickerInfo

    companion object {
        fun create(
            clazz: Class<out IJPickerAdapter>,
            args: Bundle? = null
        ): IJPickerAdapter {
            return try {
                clazz.getDeclaredConstructor(Bundle::class.java).newInstance(args)
            } catch (e: Exception) {
                clazz.getDeclaredConstructor().newInstance()
            }
        }
    }
}

/**
 * Just a Demo Adapter
 */
class TestMultipleJPickerAdapter @Keep constructor(val args: Bundle) : IJPickerAdapter {

    override val wheelCount: Int
        get() = args.getInt("wheelCount", 2)
    override val initialIndexes: IntArray
        get() = args.getIntArray("initialIndexes") ?: intArrayOf(6, 6)

    override fun key(wheelIndex: Int, selectedIndexes: List<Int>): Any {
        return if (wheelIndex == 0) {
            0
        } else {
            selectedIndexes[0]
        }
    }

    override fun generateJWheelPickerInfo(
        wheelIndex: Int,
        selectedIndexes: List<Int>
    ): JWheelPickerInfo {
        return JWheelPickerInfo(
            id = wheelIndex,
            itemCount = when (wheelIndex) {
                0 -> 10
                else -> {
                    if(selectedIndexes[0] == 2) 0
                    else 20
                }
            },
            initialIndex = initialIndexes[wheelIndex],
            itemData = { index ->
                if (wheelIndex == 0) {
                    JWheelPickerItemInfo(
                        id = (index).toString(),
                        index = index,
                        fallbackText = 65.toChar().plus(index).toString()
                    )
                } else {
                    val start = 10 * selectedIndexes[0]
                    JWheelPickerItemInfo(
                        id = (start + index).toString(),
                        index = index,
                        fallbackText = (start + index).toString()
                    )
                }
            })
    }

}