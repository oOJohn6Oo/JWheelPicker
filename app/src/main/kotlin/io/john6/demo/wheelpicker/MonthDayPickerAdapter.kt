package io.john6.demo.wheelpicker

import android.os.Bundle
import io.john6.base.compose.picker.bean.JWheelPickerInfo
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import io.john6.base.compose.picker.dialog.IJPickerAdapter

/**
 * Picker for Month and Day
 */
class MonthDayPickerAdapter(private val args: Bundle):IJPickerAdapter {
    override val wheelCount: Int
        get() = 2

    override val initialIndexes: IntArray
        get() = args.getIntArray("initialIndexes") ?: intArrayOf(0, 0)

    override fun key(wheelIndex: Int, selectedIndexes: List<Int>): Any {
        if(wheelIndex == 0){
           return "Month picker will never change"
        }else{
            return when (selectedIndexes[0] + 1) {
                4,6,9,11 -> 30
                2 -> 29
                else -> 31
            }
        }
    }

    override fun generateJWheelPickerInfo(
        wheelIndex: Int,
        selectedIndexes: List<Int>
    ): JWheelPickerInfo {
        return JWheelPickerInfo(
            id = wheelIndex,
            itemCount = if (wheelIndex == 0) {
                12
            } else when (selectedIndexes[0] + 1) {
                4,6,9,11 -> 30
                2 -> 29
                else -> 31
            },
            initialIndex = initialIndexes[wheelIndex],
            itemData = { index ->
                if (wheelIndex == 0) {
                    JWheelPickerItemInfo(
                        id = (index).toString(),
                        index = index,
                        fallbackText = "Month ${index + 1}",
                    )
                } else {
                    JWheelPickerItemInfo(
                        id = (index).toString(),
                        index = index,
                        fallbackText =  "Day ${index + 1}",
                    )
                }
            })
    }
}