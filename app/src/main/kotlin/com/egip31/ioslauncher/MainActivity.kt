package com.egip31.ioslauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
        setContent {
            IosLauncherTheme {
                val vm: LauncherViewModel = viewModel(factory = LauncherViewModel.factory(application))
                val state by vm.state.collectAsState()

                // Back button: dismiss overlays / context menu / spotlight in iOS-like priority,
                // otherwise fall back to default behavior.
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
}
