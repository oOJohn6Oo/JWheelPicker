package io.john6.base.compose.picker.dialog.single

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import io.john6.base.compose.picker.JWheelPicker
import io.john6.base.compose.picker.JWheelPickerHelper.fragmentResultKey
import io.john6.base.compose.picker.bean.JWheelPickerItemInfo
import io.john6.base.compose.picker.dialog.JBasePickerDialogFragment
import io.john6.base.compose.jwheelpicker.R
import onlyBottomSafeDrawing

/**
 * DialogFragment for custom single column picker
 *
 * Simple Example
 * ```kt
 * JSinglePickerDialogFragment.show(
 *     supportFragmentManager,
 *     requiredData = JSinglePickerDialogData(
 *         title = 0 to "SinglePicker",
 *         initialIndex = 20,
 *         dataList = dataList = (0..30).map { JWheelPickerItemInfo(it.toString(), it, "item$it") },
 *         overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_RECTANGLE
 *     ),
 * )
 *```
 *
 * Complex Example
 *
 * ```kt
 * JSinglePickerDialogFragment.show(
 *     supportFragmentManager,
 *     requiredData = JSinglePickerDialogAdapterData(
 *         title = 0 to "SingleAdapterPicker",
 *         overlayStyle = JWheelPickerHelper.OVERLAY_STYLE_RECTANGLE,
 *         adapterClass = TestMultipleJPickerAdapter::class.java,
 *         adapterParamsAsBundle = Bundle().apply {
 *             putInt("wheelCount", 1)
 *             putIntArray("initialIndexes", intArrayOf(10))
 *         }
 *     ),
 * )
 * ```
 *
 * * result will be send by  [FragmentManager.setFragmentResult]
 * key is [io.john6.base.compose.picker.JWheelPickerHelper.fragmentResultKey]
 * value is [JWheelPickerItemInfo] after parceled
 *
 * ```kt
 * supportFragmentManager.setFragmentResultListener(JSinglePickerDialogFragment.TAG, this) { _, bundle ->
 *     val result = bundle.getParcelableCompat(fragmentResultKey, JWheelPickerItemInfo::class.java)
 *     // Write Your Code Here
 * }
 * ```
 */
open class JSinglePickerDialogFragment : JBasePickerDialogFragment() {

    private lateinit var mViewModel: JSinglePickerViewModel

    override fun getTheme() = R.style.JPickerDialogTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            SavedStateViewModelFactory(null, this, arguments)
        )[JSinglePickerViewModel::class.java]
    }

    override fun onSubmit() {
        // In case multiple picker shows in a single fragment, we need to use tag to distinguish
        val desireTag = this.tag ?: TAG
        parentFragmentManager.setFragmentResult(desireTag, Bundle().apply {
            putParcelable(fragmentResultKey, mViewModel.currentSelectedItemInfo)
        })
        dismiss()
    }

    @Composable
    override fun ContentView() {
        Column(modifier = Modifier.onlyBottomSafeDrawing()) {
            DefaultPickerHeader(
                title = getDialogTitle(title = mViewModel.requiredData.title),
                confirmImgPainter = painterResource(R.drawable.ic_done_24dp_from_j_picker),
                onSubmit = this@JSinglePickerDialogFragment::onSubmit
            )

            val data = mViewModel.requiredData

            if (data is JSinglePickerDialogAdapterData) {
                SinglePicker(
                    modifier = Modifier.fillMaxWidth(),
                    requiredData = data,
                    setCurrentSelectedIndex = mViewModel::currentSelectedItemInfo::set
                )
            } else {
                SinglePicker(
                    modifier = Modifier.fillMaxWidth(),
                    requiredData = mViewModel.requiredData as JSinglePickerDialogData,
                    setCurrentSelectedIndex = mViewModel::currentSelectedItemInfo::set
                )
            }
        }
    }

    @Composable
    internal fun SinglePicker(
        modifier: Modifier,
        requiredData: JSinglePickerDialogData,
        setCurrentSelectedIndex: (JWheelPickerItemInfo) -> Unit
    ) {
        var currentSelectedIndex by rememberSaveable {
            mutableIntStateOf(requiredData.getSafeInitialIndex())
        }

        val onSelectedItemChanged: (itemData: JWheelPickerItemInfo) -> Unit = {
            currentSelectedIndex = it.index
            setCurrentSelectedIndex(it)
        }

        JWheelPicker(
            modifier = modifier,
            onSelectedItemChanged = onSelectedItemChanged,
            textStyle = TextStyle(color = MaterialTheme.colors.onBackground, fontSize = 20.sp),
            selectedTextColor = requiredData.getDesireSelectTextColor(LocalContext.current),
            drawOverLay = rememberDefaultOverlayStyle(requiredData.overlayStyle),
            initialIndex = requiredData.getSafeInitialIndex(),
            itemCount = requiredData.dataList.size,
            itemData = { index ->
                requiredData.dataList[index]
            },
        )
    }

    @Composable
    internal fun SinglePicker(
        modifier: Modifier,
        requiredData: JSinglePickerDialogAdapterData,
        setCurrentSelectedIndex: (JWheelPickerItemInfo) -> Unit
    ) {

        var currentSelectedIndex:Int by rememberSaveable {
            mutableIntStateOf(requiredData.jAdapter.initialIndexes[0])
        }

        val onSelectedItemChanged: (itemData: JWheelPickerItemInfo) -> Unit = {
            currentSelectedIndex = it.index
            setCurrentSelectedIndex(it)
        }

        val pickerData by remember {
            mutableStateOf(requiredData.jAdapter.generateJWheelPickerInfo(0, listOf(currentSelectedIndex)))
        }


        JWheelPicker(
            modifier = modifier,
            onSelectedItemChanged = onSelectedItemChanged,
            textStyle = TextStyle(color = MaterialTheme.colors.onBackground, fontSize = 20.sp),
            drawOverLay = rememberDefaultOverlayStyle(requiredData.overlayStyle),
            selectedTextColor = requiredData.getDesireSelectTextColor(LocalContext.current),
            initialIndex = currentSelectedIndex,
            itemCount = pickerData.itemCount,
            itemData = pickerData.itemData,
        )
    }

    companion object {
        const val TAG = "JSinglePicker"

        private fun checkFragmentWithTagExist(
            fragmentManager: FragmentManager,
            tag: String = TAG
        ): Fragment? {
            val res = fragmentManager.findFragmentByTag(tag)
            if (res != null) Log.d(TAG, "JSinglePicker with tag $tag exist")
            return res
        }

        /**
         * Show Simple Single Picker
         *
         * @param fragmentManager which the returned [JSinglePickerDialogFragment] will shown in
         * * For Activity，use `supportFragmentManager`
         * * For Fragment, most of the case just use [Fragment.getChildFragmentManager]
         *
         * @param requiredData use [JSinglePickerDialogData] for simple and short list, use [JSinglePickerDialogAdapterData] the other case
         * @param tag used to identify this [Fragment], also will be the FragmentResult API 's tag
         *
         */
        fun show(
            fragmentManager: FragmentManager,
            requiredData: JSinglePickerDialogData,
            tag: String = TAG,
        ): JSinglePickerDialogFragment {

            val existFragment = checkFragmentWithTagExist(fragmentManager, tag)
            if (existFragment is JSinglePickerDialogFragment) {
                return existFragment
            }

            return JSinglePickerDialogFragment().apply {
                if (arguments == null) {
                    arguments = Bundle()
                }
                arguments?.putParcelable("data", requiredData)
                show(fragmentManager, tag)
            }
        }

        /**
         * Show Complex or Large Single Picker
         *
         * @param fragmentManager which the returned [JSinglePickerDialogFragment] will shown in
         * * For Activity，use `supportFragmentManager`
         * * For Fragment, most of the case just use [Fragment.getChildFragmentManager]
         *
         * @param requiredData use [JSinglePickerDialogData] for simple and short list, use [JSinglePickerDialogAdapterData] the other case
         * @param tag used to identify this [Fragment], also will be the FragmentResult API 's tag
         *
         */
        fun show(
            fragmentManager: FragmentManager,
            requiredData: JSinglePickerDialogAdapterData,
            tag: String = TAG,
        ): JSinglePickerDialogFragment {

            val existFragment = checkFragmentWithTagExist(fragmentManager, tag)
            if (existFragment is JSinglePickerDialogFragment) {
                return existFragment
            }

            return JSinglePickerDialogFragment().apply {
                if (arguments == null) {
                    arguments = Bundle()
                }
                arguments?.putParcelable("data", requiredData)
                show(fragmentManager, tag)
            }
        }
    }
}