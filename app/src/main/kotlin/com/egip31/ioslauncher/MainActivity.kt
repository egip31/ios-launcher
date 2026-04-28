package com.egip31.ioslauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egip31.ioslauncher.ui.LauncherRoot
import com.egip31.ioslauncher.ui.LauncherViewModel
import com.egip31.ioslauncher.ui.theme.IosLauncherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IosLauncherTheme {
                val vm: LauncherViewModel = viewModel(factory = LauncherViewModel.factory(application))
                val state by vm.state.collectAsState()
                LauncherRoot(
                    state = state,
                    onLaunchApp = vm::launchApp,
                    onRefresh = vm::refresh
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * The HOME button on Android sends a new intent to the active home activity.
     * We override this to "snap back" to page 0 (typical launcher behavior).
     */
    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        // ViewModel observes lifecycle; nothing else needed here for now.
    }
}
