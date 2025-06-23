package io.john6.demo.wheelpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import android.graphics.Color as androidColor

open class DemoDialogFragment : DialogFragment() {

    override fun getTheme() = R.style.JPickerDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DemoTheme {
                    DialogScreen(this@DemoDialogFragment::dismiss)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            WindowCompat.setDecorFitsSystemWindows(this, false)
            setBackgroundDrawable(androidColor.TRANSPARENT.toDrawable())
        }
    }
}

@Composable
private fun DialogScreen(dismissDialog: ()->Unit) {
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)
    if (!sheetState.isVisible) {
        LaunchedEffect(sheetState) {
            dismissDialog()
        }
    }
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetElevation = 1.dp,
        sheetGesturesEnabled = true,
        sheetShape = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        ),
        scrimColor = Color.Transparent,
        content = {},
        sheetContent = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
                items(10) {
                    Button(onClick = {}) {
                        Text("Item ${it + 1}")
                    }
                }
            }
        },
    )
}