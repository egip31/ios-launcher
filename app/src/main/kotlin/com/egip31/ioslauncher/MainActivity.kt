package com.egip31.ioslauncher

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egip31.ioslauncher.ui.LauncherRoot
import com.egip31.ioslauncher.ui.LauncherViewModel
import com.egip31.ioslauncher.ui.Overlay
import com.egip31.ioslauncher.ui.Scene
import com.egip31.ioslauncher.ui.theme.IosLauncherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Hide both the system status bar and navigation bar so our wallpaper extends
        // truly edge-to-edge and our custom iOS-style status bar / home indicator are
        // the only chrome on screen. Sticky-immersive-style: bars come back briefly on
        // a swipe-from-edge and then auto-hide.
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        // Avoid screen burn-in from completely hidden bars on some OEMs by keeping the
        // window flagged as fullscreen — the bars are still hideable above this.
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        setContent {
            IosLauncherTheme {
                val vm: LauncherViewModel = viewModel(factory = LauncherViewModel.factory(application))
                val state by vm.state.collectAsState()

                BackHandler(enabled = state.contextMenuApp != null) { vm.hideContextMenu() }
                BackHandler(enabled = state.overlay != Overlay.NONE) { vm.dismissOverlay() }
                BackHandler(enabled = state.scene == Scene.SPOTLIGHT) { vm.hideSpotlight() }
                BackHandler(enabled = state.jiggleMode) { vm.exitJiggleMode() }

                LauncherRoot(
                    state = state,
                    onLaunchApp = vm::launchApp,
                    onUnlock = vm::unlock,
                    onShowSpotlight = vm::showSpotlight,
                    onHideSpotlight = vm::hideSpotlight,
                    onShowControlCenter = vm::showControlCenter,
                    onShowNotificationCenter = vm::showNotificationCenter,
                    onDismissOverlay = vm::dismissOverlay,
                    onLongClickApp = vm::showContextMenu,
                    onHideContextMenu = vm::hideContextMenu,
                    onExitJiggle = vm::exitJiggleMode,
                    onUninstall = vm::uninstallApp,
                    onPickWallpaper = vm::setWallpaper,
                )
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Re-hide bars whenever the window regains focus (e.g. after returning from
            // an Android settings panel) — otherwise the bars stick around.
            WindowInsetsControllerCompat(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}
