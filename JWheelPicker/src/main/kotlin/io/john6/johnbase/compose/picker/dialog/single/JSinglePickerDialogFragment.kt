package io.john6.johnbase.compose.picker.dialog.single

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import io.john6.johnbase.compose.R
import io.john6.johnbase.compose.picker.JMultiWheelPicker
import io.john6.johnbase.compose.picker.JWheelPicker
import io.john6.johnbase.compose.picker.JWheelPickerHelper.drawPickerLineOverlay
import io.john6.johnbase.compose.picker.JWheelPickerHelper.drawPickerRectOverlay
import io.john6.johnbase.compose.picker.JWheelPickerHelper.fragmentResultKey
import io.john6.johnbase.compose.picker.JWheelPickerInfo
import io.john6.johnbase.compose.picker.bean.JWheelPickerItemInfo
import io.john6.johnbase.compose.picker.dialog.JBasePickerDialogFragment
import io.john6.johnbase.compose.ui.JElevationOverlayInBothLightAndDarkMode
import io.john6.johnbase.compose.ui.bottomSafeDrawing
import io.john6.johnbase.compose.ui.jSurfaceColorAtElevation

/**
 * DialogFragment for custom single column picker
 *
 * Example to use
 * ```kotlin
 * JSinglePickerDialogFragment.show(
 *     parentFragmentManager,
 *     title = "Demo Picker",
 *     initialIndex = 4,
 *     dataList = ArrayList((0..10).map { JWheelPickerItemInfo(it.toString(), "item$it") }),
 * )
 * ```
 *
 * * result will be send by  [FragmentManager.setFragmentResult]，key is [io.john6.johnbase.compose.picker.JWheelPickerHelper.fragmentResultKey], value is [JWheelPickerItemInfo] after parceled
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
        Column(modifier = Modifier.bottomSafeDrawing()) {
            DefaultPickerHeader(
                title = getDialogTitle(title = mViewModel.requiredData.title),
                onSubmit = this@JSinglePickerDialogFragment::onSubmit
            )

            SinglePicker(
                modifier = Modifier
                    .fillMaxWidth(),
                requiredData = mViewModel.requiredData,
                setCurrentSelectedIndex = mViewModel::currentSelectedItemInfo::set
            )
        }
    }

    @Composable
    internal open fun SinglePicker(
        modifier: Modifier = Modifier,
        requiredData: JSinglePickerDialogData,
        setCurrentSelectedIndex: (JWheelPickerItemInfo) -> Unit
    ) {
        var currentSelectedIndex by rememberSaveable {
            mutableIntStateOf(requiredData.getSafeInitialIndex())
        }

        val onSelectedItemChanged: (itemData: JWheelPickerItemInfo) -> Unit =
            remember {
                {
                    currentSelectedIndex = it.index
                    setCurrentSelectedIndex(it)
                }
            }

        JWheelPicker(
            modifier = modifier,
            onSelectedItemChanged = onSelectedItemChanged,
            textStyle = TextStyle(color = MaterialTheme.colors.onBackground, fontSize = 20.sp),
            drawOverLay = rememberDefaultOverlayStyle(requiredData.overlayStyle),
            initialIndex = requiredData.getSafeInitialIndex(),
            itemCount = requiredData.dataList.size,
            itemData = { index ->
                requiredData.dataList[index]
            },
        )
    }

    companion object {
        const val TAG = "JSinglePicker"

        fun show(
            fragmentManager: FragmentManager,
            requiredData: JSinglePickerDialogData,
            tag: String = TAG,
        ): JSinglePickerDialogFragment? {
            if(fragmentManager.findFragmentByTag(tag) != null) {
                Log.d(TAG, "JSinglePicker with tag $tag exist")
                return null
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