package io.john6.johnbase.compose.picker.dialog.multiple

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.john6.johnbase.compose.picker.JMultiWheelPicker
import io.john6.johnbase.compose.picker.JWheelPickerInfo
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
import io.john6.johnbase.compose.picker.dialog.JBasePickerDialogFragment
import io.john6.johnbase.compose.picker.dialog.single.JSinglePickerDialogFragment
import io.john6.johnbase.compose.ui.JUtil.disableAllVerticalScroll
import io.john6.johnbase.compose.ui.bottomSafeDrawing
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * DialogFragment for custom multiple column picker
 *
 * Example to use
 * ```kotlin
 * JMultiplePickerDialogFragment.show(
 *     parentFragmentManager,
 *     requiredData = JMultiPickerDialogData(
 *         title = 0 to "MultiplePicker",
 *         overlayStyle = JWheelPickerHelper.overlayStyleOvalRectangle,
 *         adapterClass = IMultipleJPickerAdapter.testAdapter::class.java
 *     ),
 * )
 * ```
 *
 * * onSubmit 结果会以 [FragmentManager.setFragmentResult] 的方式返回，key 为 "result", value 为 [JWheelPickerItemInfo] ParcelableArray
 *
 */
class JMultiplePickerDialogFragment : JBasePickerDialogFragment() {
    private lateinit var mViewModel: JMultiplePickerViewModel
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
        Column(modifier = Modifier.bottomSafeDrawing()) {
            DefaultPickerHeader(
                title = getDialogTitle(title = mViewModel.requiredData.title),
                onSubmit = this@JMultiplePickerDialogFragment::onSubmit
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
        if(disableTouch) return
        disableTouch = true
        val tempFM = parentFragmentManager
        // In case multiple picker shows in a single fragment, we need to use tag to distinguish
        val desireTag = this@JMultiplePickerDialogFragment.tag ?: TAG
        MainScope().launch {
            dismiss()
            // Wait for wheel picker state to idle
            delay(200)
            tempFM.setFragmentResult(desireTag, Bundle().apply {
                putParcelableArray("result", mViewModel.currentSelectedItemInfo.toTypedArray())
            })
        }
    }

    @Composable
    fun MultiplePicker(
        modifier: Modifier,
        requiredData: JMultiPickerDialogData,
        adapter: IMultipleJPickerAdapter
    ){
        val selectedIndexes: MutableList<Int> = remember {
            mutableStateListOf(*adapter.initialIndexes).apply {
                forEachIndexed { index, initialIndex ->
                    val generatePickerInfoFunc = adapter.generateJWheelPickerInfo(index, this)
                    mViewModel.currentSelectedItemInfo[index] = generatePickerInfoFunc.itemData(initialIndex)
                }
            }
        }

        val generateJWheelPickerInfo: (wheelIndex: Int) -> JWheelPickerInfo = remember {
             { wheelIndex ->
                adapter.generateJWheelPickerInfo(wheelIndex, selectedIndexes)
            }
        }

        val onSelectedItemChanged: (pickerInfo:JWheelPickerInfo, itemInfo:JWheelPickerItemInfo) -> Unit = remember {
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
            modifier = modifier.disableAllVerticalScroll(disableTouch),
            wheelCount = adapter.wheelCount,
            generateJWheelPickerInfo = generateJWheelPickerInfo,
            drawOverLay = rememberDefaultOverlayStyle(requiredData.overlayStyle),
            key = key,
            onSelectedItemChanged = onSelectedItemChanged
        )
    }

    companion object {
        const val TAG = "JMultiplePicker"

        fun show(
            fragmentManager: FragmentManager,
            requiredData: JMultiPickerDialogData,
            tag: String = TAG,
        ): JMultiplePickerDialogFragment? {
            if(fragmentManager.findFragmentByTag(tag) != null) {
                Log.d(TAG, "JMultiplePicker with tag $tag exist")
                return null
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