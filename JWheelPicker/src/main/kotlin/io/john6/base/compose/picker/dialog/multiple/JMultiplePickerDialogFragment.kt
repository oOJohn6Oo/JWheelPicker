package io.john6.base.compose.picker.dialog.multiple

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import io.john6.base.compose.picker.JMultiWheelPicker
import io.john6.base.compose.picker.JWheelPickerHelper.fragmentResultKey
import io.john6.base.compose.picker.bean.JWheelPickerInfo
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import io.john6.base.compose.picker.dialog.IJPickerAdapter
import io.john6.base.compose.picker.dialog.JBasePickerDialogFragment
import io.john6.base.compose.jwheelpicker.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import onlyBottomSafeDrawing


/**
 * DialogFragment for custom multiple column picker
 *
 * Example to use
 * ```kotlin
 * JMultiplePickerDialogFragment.show(
 *     supportFragmentManager,
 *     requiredData = JMultiPickerDialogData(
 *         title = 0 to "MultiplePicker",
 *         overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_LINE,
 *         adapterClass = TestMultipleJPickerAdapter::class.java
 *     ),
 * )
 * ```
 *
 * * result will be send by [FragmentManager.setFragmentResult]
 * key is [io.john6.base.compose.picker.JWheelPickerHelper.fragmentResultKey]
 * value is [JWheelPickerItemInfo] after Parceled
 *
 * ```kt
 * supportFragmentManager.setFragmentResultListener(JMultiplePickerDialogFragment.TAG, this) { _, bundle ->
 *     val result = bundle.getParcelableArrayCompat(fragmentResultKey, JWheelPickerItemInfo::class.java)
 *     // Write Your Code Here
 * }
 * ```
 */
class JMultiplePickerDialogFragment : JBasePickerDialogFragment() {
    protected lateinit var mViewModel: JMultiplePickerViewModel
    private var disableTouch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            SavedStateViewModelFactory(null, this, arguments)
        )[JMultiplePickerViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        disableTouch = false
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @Composable
    override fun ContentView() {
        Column(modifier = Modifier.onlyBottomSafeDrawing()) {
            DefaultPickerHeader(
                title = getDialogTitle(title = mViewModel.requiredData.title),
                confirmImgVector = null,
                confirmImgPainter = painterResource(R.drawable.ic_done_24dp_from_j_picker),
                onSubmit = this@JMultiplePickerDialogFragment::onSubmit,
                confirmText = stringResource(android.R.string.ok),
            )

            MultiplePicker(
                modifier = Modifier
                    .fillMaxWidth(),
                requiredData = mViewModel.requiredData,
                adapter = mViewModel.mMultipleJPickerAdapter
            )

        }
    }

    override fun onSubmit() {
        if (disableTouch) return
        disableTouch = true
        val tempFM = parentFragmentManager
        // In case multiple picker shows in a single fragment, we need to use tag to distinguish
        val desireTag = this@JMultiplePickerDialogFragment.tag ?: TAG
        MainScope().launch {
            dismiss()
            // Wait for wheel picker state to idle
            delay(200)
            tempFM.setFragmentResult(desireTag, Bundle().apply {
                putParcelableArray(
                    fragmentResultKey,
                    mViewModel.currentSelectedItemInfo.toTypedArray()
                )
            })
        }
    }

    @Composable
    fun MultiplePicker(
        modifier: Modifier,
        requiredData: JMultiPickerDialogData,
        adapter: IJPickerAdapter
    ) {
        val selectedIndexes: MutableList<Int> = remember {
            mutableStateListOf(*adapter.initialIndexes.asList().toTypedArray())
        }

        val generateJWheelPickerInfo: (wheelIndex: Int) -> JWheelPickerInfo = remember {
            { wheelIndex ->
                adapter.generateJWheelPickerInfo(wheelIndex, selectedIndexes)
            }
        }

        val onSelectedItemChanged: (pickerInfo: JWheelPickerInfo, itemInfo: JWheelPickerItemInfo) -> Unit =
            remember {
                { pickerInfo, itemInfo ->
                    selectedIndexes[pickerInfo.id] = itemInfo.index
                    mViewModel.currentSelectedItemInfo[pickerInfo.id] = itemInfo
                }
            }

        val key: (wheelIndex: Int) -> Any = remember {
            { wheelIndex ->
                adapter.key(wheelIndex, selectedIndexes)
            }
        }

        JMultiWheelPicker(
            modifier = modifier,
            wheelCount = adapter.wheelCount,
            generateJWheelPickerInfo = generateJWheelPickerInfo,
            drawOverLay = rememberDefaultOverlayStyle(requiredData.overlayStyle),
            key = key,
            onSelectedItemChanged = onSelectedItemChanged,
            selectedTextColor = requiredData.selectTextColor(),
        )
    }

    companion object {
        const val TAG = "JMultiplePicker"

        fun show(
            fragmentManager: FragmentManager,
            requiredData: JMultiPickerDialogData,
            tag: String = TAG,
        ): JMultiplePickerDialogFragment {
            val existFragment = fragmentManager.findFragmentByTag(tag)
            if (existFragment is JMultiplePickerDialogFragment) {
                Log.d(TAG, "JMultiplePicker with tag $tag exist")
                return existFragment
            }
            return JMultiplePickerDialogFragment().apply {
                if (arguments == null) {
                    arguments = Bundle()
                }
                arguments?.putParcelable("data", requiredData)
                show(fragmentManager, tag)
            }
        }
    }

}