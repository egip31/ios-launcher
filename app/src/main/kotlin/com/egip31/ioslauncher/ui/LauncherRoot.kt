package com.egip31.ioslauncher.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.egip31.ioslauncher.data.AppInfo
import com.egip31.ioslauncher.ui.components.WallpaperBackground

/**
 * Top-level pager: page 0 is the home grid, page 1 is the App Library — mimicking
 * the iOS gesture of swiping right-to-left past the last home page to reveal it.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun LauncherRoot(
    state: LauncherState,
    onLaunchApp: (AppInfo) -> Unit,
    onRefresh: () -> Unit = {},
) {
    WallpaperBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // Outer pager with two "scenes": HOME and APP_LIBRARY.
            val pagerState = rememberPagerState(pageCount = { 2 })

            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> HomeScreen(
                        pages = state.homePages,
                        dock = state.dock,
                        onLaunchApp = onLaunchApp,
                    )
                    1 -> AppLibraryScreen(
                        apps = state.apps,
                        onLaunchApp = onLaunchApp,
                    )
                }
            }
        }
    }
}
