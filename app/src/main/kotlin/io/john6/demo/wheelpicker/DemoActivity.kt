package io.john6.demo.wheelpicker

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity

class DemoActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindow()
        setContentView(ComposeView(this).apply {
            setContent {
                DemoTheme {
                    DemoPage(this@DemoActivity::showDialog)
                }
            }
        })
    }

    private fun showDialog(){
        DemoDialogFragment().show(supportFragmentManager, "DemoDialogFragment")
    }
}

@Composable
private fun DemoPage(showDialog: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = showDialog) {
            Text("Show Dialog")
        }
    }
}