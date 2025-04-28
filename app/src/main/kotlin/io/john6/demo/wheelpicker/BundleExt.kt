package io.john6.demo.wheelpicker

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable


@Suppress("DEPRECATION")
internal fun <T : Parcelable> Bundle.getParcelableCompat(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, clazz)
    } else {
        getParcelable(key) as? T
    }
}

@Suppress("DEPRECATION", "UNCHECKED_CAST")
internal fun <T : Serializable> Bundle.getSerializableCompat(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, clazz)
    } else {
        getSerializable(key) as? T
    }
}

@Suppress("DEPRECATION", "UNCHECKED_CAST")
internal fun <T : Parcelable> Bundle.getParcelableArrayCompat(
    key: String,
    clazz: Class<T>
): Array<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArray(key, clazz)
    } else {
        getParcelableArray(key) as? Array<T>
    }
}