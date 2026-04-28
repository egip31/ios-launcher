package com.egip31.ioslauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.egip31.ioslauncher.ui.theme.WallpaperBottom
import com.egip31.ioslauncher.ui.theme.WallpaperMid
import com.egip31.ioslauncher.ui.theme.WallpaperTop

@Composable
fun WallpaperBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(WallpaperTop, WallpaperMid, WallpaperBottom),
                )
            )
    ) {
        content()
    }
}
