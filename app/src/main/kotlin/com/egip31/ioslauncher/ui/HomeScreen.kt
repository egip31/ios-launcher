package com.egip31.ioslauncher.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.egip31.ioslauncher.data.AppInfo
import com.egip31.ioslauncher.ui.components.AppIcon
import com.egip31.ioslauncher.ui.components.Dock
import com.egip31.ioslauncher.ui.components.HomeIndicator
import com.egip31.ioslauncher.ui.components.PageIndicator

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    pages: List<List<AppInfo>>,
    dock: List<AppInfo>,
    jiggleMode: Boolean,
    onLaunchApp: (AppInfo) -> Unit,
    onLongClickApp: (AppInfo) -> Unit,
    onPullDownForSpotlight: () -> Unit,
    onExitJiggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pageCount = pages.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 44.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Spacer(Modifier.height(8.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) { pageIndex ->
            val pageApps = pages.getOrElse(pageIndex) { emptyList() }
            HomePage(
                apps = pageApps,
                jiggleMode = jiggleMode,
                onLaunchApp = onLaunchApp,
                onLongClickApp = onLongClickApp,
                onPullDownForSpotlight = onPullDownForSpotlight,
                onExitJiggle = onExitJiggle,
            )
        }

        PageIndicator(
            pageCount = pageCount,
            currentPage = currentPage,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
        )

        Dock(
            apps = dock,
            onLaunchApp = onLaunchApp,
            onLongClickApp = onLongClickApp,
            jiggle = jiggleMode,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        HomeIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun HomePage(
    apps: List<AppInfo>,
    jiggleMode: Boolean,
    onLaunchApp: (AppInfo) -> Unit,
    onLongClickApp: (AppInfo) -> Unit,
    onPullDownForSpotlight: () -> Unit,
    onExitJiggle: () -> Unit,
) {
    var pullAccum by remember { mutableStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(jiggleMode) {
                if (jiggleMode) {
                    // Tap empty area to exit jiggle mode (mimics iOS "Done").
                    detectVerticalDragGestures(
                        onDragStart = { onExitJiggle() },
                        onVerticalDrag = { _, _ -> },
                    )
                } else {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (pullAccum > 200f) onPullDownForSpotlight()
                            pullAccum = 0f
                        },
                        onDragCancel = { pullAccum = 0f },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            if (dragAmount > 0f) pullAccum += dragAmount
                        },
                    )
                }
            }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        ) {
            items(items = apps, key = { it.id }) { app ->
                AppIcon(
                    app = app,
                    onClick = { onLaunchApp(app) },
                    onLongClick = { onLongClickApp(app) },
                    iconSize = 56.dp,
                    jiggle = jiggleMode,
                )
            }
        }
    }
}
