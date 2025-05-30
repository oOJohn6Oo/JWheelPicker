package io.john6.base.compose.picker.dialog.date

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import io.john6.base.compose.jwheelpicker.R
import io.john6.base.compose.picker.JDateWheelPicker
import io.john6.base.compose.picker.JWheelPickerHelper.fragmentResultKey
import io.john6.base.compose.picker.dialog.JBasePickerDialogFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import onlyBottomSafeDrawing
import java.time.LocalDateTime


/**
 * DialogFragment for custom date picker
 *
 * Example to use
 * ```kotlin
 * JDateWheelPickerDialogFragment.show(
 *     supportFragmentManager,
 *     requiredData = JDatePickerDialogData(
 *         title = 0 to "DatePicker",
 *         overlayStyle = JWheelPickerHelper.overlayStyleOvalRectangle,
 *         containerHorizontalPaddingInDp = 16
 *     ),
 * )
 * ```
 *
 * * result will be send by [FragmentManager.setFragmentResult]
 * key is [io.john6.base.compose.picker.JWheelPickerHelper.fragmentResultKey]
 * value is [LocalDateTime] after serialized
 *
 * ```kt
 * supportFragmentManager.setFragmentResultListener(JDateWheelPickerDialogFragment.TAG, this) { _, bundle ->
 *     val result = bundle.getSerializableCompat(fragmentResultKey, LocalDateTime::class.java)
 *     // Write Your Code Here
 * }
 * ```
 */
@RequiresApi(Build.VERSION_CODES.O)
open class JDateWheelPickerDialogFragment : JBasePickerDialogFragment() {
    protected lateinit var mViewModel: JDateWheelPickerViewModel
    private var disableTouch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            SavedStateViewModelFactory(null, this, arguments)
        )[JDateWheelPickerViewModel::class.java]
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
                onSubmit = this@JDateWheelPickerDialogFragment::onSubmit,
                confirmText = stringResource(android.R.string.ok),
            )

            DatePicker(
                modifier = Modifier
                    .fillMaxWidth(),
                requiredData = mViewModel.requiredData,
                onSelectedTimeChanged = mViewModel::currentSelectedDateTime::set
            )

        }
    }

    override fun onSubmit() {
        if (disableTouch) return
        disableTouch = true
        val tempFM = parentFragmentManager
        // In case multiple picker shows in a single fragment, we need to use tag to distinguish
        val desireTag = this@JDateWheelPickerDialogFragment.tag ?: TAG
        MainScope().launch {
            dismiss()
            // Wait for wheel picker state to idle
            delay(200)
            tempFM.setFragmentResult(desireTag, Bundle().apply {
                putSerializable(fragmentResultKey, mViewModel.currentSelectedDateTime)
            })
        }
    }

    @Composable
    fun DatePicker(
        modifier: Modifier,
        requiredData: JDatePickerDialogData,
        onSelectedTimeChanged: (LocalDateTime) -> Unit,
    ) {

        JDateWheelPicker(
            modifier = modifier,
            datePickerMode = requiredData.datePickerMode,
            timePickerMode = requiredData.timePickerMode,
            containerHorizontalPadding = requiredData.containerHorizontalPaddingInDp.dp,
            startLocalDateTime = requiredData.startLocalDateTime,
            endLocalDateTime = requiredData.endLocalDateTime,
            initialSelectDateTime = requiredData.initialSelectDateTime,
            drawOverLay = rememberDefaultOverlayStyle(requiredData.overlayStyle),
            onSelectedTimeChanged = onSelectedTimeChanged,
            selectedTextColor = requiredData.selectTextColor(),
        )
    }

    companion object {
        const val TAG = "JDatePicker"

        fun show(
            fragmentManager: FragmentManager,
            requiredData: JDatePickerDialogData,
            tag: String = TAG,
        ): JDateWheelPickerDialogFragment? {
            if (fragmentManager.findFragmentByTag(tag) != null) {
                Log.d(TAG, "JDatePicker with tag $tag exist")
                return null
            }
            return JDateWheelPickerDialogFragment().apply {
                if (arguments == null) {
                    arguments = Bundle()
                }
                arguments?.putSerializable("data", requiredData)
                show(fragmentManager, tag)
            }
        }
    }

}