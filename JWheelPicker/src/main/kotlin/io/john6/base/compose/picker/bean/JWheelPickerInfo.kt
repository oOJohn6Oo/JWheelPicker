package io.john6.base.compose.picker.bean
/**
 * 定义每个滚轮呈现数据的信息
 *
 * 滚轮通用数据
 * @param id 当前滚轮的唯一标识
 * @param itemCount Item 总数量
 * @param itemData 获取 Item 数据的方法
 * @param initialIndex 默认选中项的下标
 */
data class JWheelPickerInfo(
    val id: Int,
    val itemCount: Int,
    val itemData: (index: Int) -> JWheelPickerItemInfo,
    val initialIndex: Int,
) {
    companion object {
        val EMPTY = JWheelPickerInfo(
            0, 0, { JWheelPickerItemInfo.EMPTY }, 0
        )
    }
}