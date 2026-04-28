package com.egip31.ioslauncher.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.egip31.ioslauncher.data.AppInfo
import com.egip31.ioslauncher.data.WallpaperStyle
import com.egip31.ioslauncher.ui.components.DynamicIsland
import com.egip31.ioslauncher.ui.components.StatusBar
import com.egip31.ioslauncher.ui.components.WallpaperBackground

/**
 * Root layout: wallpaper at the bottom, then Lock / Home / Spotlight, then overlays
 * (Control Center, Notification Center, long-press menu, wallpaper picker), then the
 * status bar overlay and Dynamic Island pill on top.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LauncherRoot(
    state: LauncherState,
    onLaunchApp: (AppInfo) -> Unit,
    onUnlock: () -> Unit,
    onShowSpotlight: () -> Unit,
    onHideSpotlight: () -> Unit,
    onShowControlCenter: () -> Unit,
    onShowNotificationCenter: () -> Unit,
    onDismissOverlay: () -> Unit,
    onLongClickApp: (AppInfo) -> Unit,
    onHideContextMenu: () -> Unit,
    onExitJiggle: () -> Unit,
    onUninstall: (AppInfo) -> Unit,
    onPickWallpaper: (WallpaperStyle) -> Unit,
) {
    var showWallpaperPicker by remember { mutableStateOf(false) }

    WallpaperBackground(style = state.wallpaper) {
        Box(modifier = Modifier.fillMaxSize()) {

            // ---------- main scene ----------
            when (state.scene) {
                Scene.LOCK -> LockScreen(onUnlock = onUnlock)
                Scene.HOME -> {
                    val pagerState = rememberPagerState(pageCount = { 2 })
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        when (page) {
                            0 -> HomeScreen(
                                pages = state.homePages,
                                dock = state.dock,
                                jiggleMode = state.jiggleMode,
                                onLaunchApp = onLaunchApp,
                                onLongClickApp = onLongClickApp,
                                onPullDownForSpotlight = onShowSpotlight,
                                onExitJiggle = onExitJiggle,
                            )
                            1 -> AppLibraryScreen(
                                apps = state.apps,
                                onLaunchApp = onLaunchApp,
                                onLongClickApp = onLongClickApp,
                            )
                        }
                    }
                }
                Scene.SPOTLIGHT -> SpotlightSearch(
                    apps = state.apps,
                    onLaunchApp = onLaunchApp,
                    onDismiss = onHideSpotlight,
                )
            }

            // ---------- top-edge swipe-down hot zones (only on Home) ----------
            if (state.scene == Scene.HOME && state.overlay == Overlay.NONE && !state.jiggleMode) {
                TopEdgeGestureDetector(
                    onSwipeFromTopLeft = onShowNotificationCenter,
                    onSwipeFromTopRight = onShowControlCenter,
                )
            }

            // ---------- overlays ----------
            when (state.overlay) {
                Overlay.CONTROL_CENTER -> ControlCenter(
                    onDismiss = onDismissOverlay,
                    onChangeWallpaper = { showWallpaperPicker = true },
                )
                Overlay.NOTIFICATION_CENTER -> NotificationCenter(onDismiss = onDismissOverlay)
                Overlay.NONE -> Unit
            }

            // ---------- long-press context menu ----------
            state.contextMenuApp?.let { app ->
                AppContextMenu(
                    app = app,
                    onDismiss = onHideContextMenu,
                    onUninstall = { onUninstall(app) },
                )
            }

            // ---------- wallpaper picker ----------
            if (showWallpaperPicker) {
                WallpaperPicker(
                    current = state.wallpaper,
                    onSelect = onPickWallpaper,
                    onDismiss = { showWallpaperPicker = false },
                )
            }

            // ---------- always-on-top decorations ----------
            if (state.scene == Scene.HOME && state.overlay == Overlay.NONE && state.contextMenuApp == null) {
                StatusBar(modifier = Modifier.align(Alignment.TopStart))
                DynamicIsland(modifier = Modifier.align(Alignment.TopCenter))
            }
        }
    }
}

/**
 * Detects a vertical swipe-down gesture starting from the top edge. The screen is split
 * into two halves: left half opens Notification Center, right half opens Control Center.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopEdgeGestureDetector(
    onSwipeFromTopLeft: () -> Unit,
    onSwipeFromTopRight: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.08f)
            .pointerInput(Unit) {
                var startedFromRight = false
                var dragSum = 0f
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        startedFromRight = offset.x > size.width / 2f
                        dragSum = 0f
                    },
                    onDragEnd = {
                        if (dragSum > 80f) {
                            if (startedFromRight) onSwipeFromTopRight() else onSwipeFromTopLeft()
                        }
                    },
                    onDragCancel = { dragSum = 0f },
                    onVerticalDrag = { change, amount ->
                        change.consume()
                        if (amount > 0f) dragSum += amount
                    },
                )
            }
    )
}
