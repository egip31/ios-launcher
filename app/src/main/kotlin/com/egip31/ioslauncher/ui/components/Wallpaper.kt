package com.egip31.ioslauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.egip31.ioslauncher.data.WallpaperStyle

private val PurpleColors = listOf(Color(0xFF1B3A5B), Color(0xFF6A0F8E), Color(0xFF8E2DE2))
private val BlueColors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
private val SunsetColors = listOf(Color(0xFFFF512F), Color(0xFFDD2476), Color(0xFFA20097))
private val OceanColors = listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
private val DarkColors = listOf(Color(0xFF000000), Color(0xFF1C1C1E))

private fun colorsFor(style: WallpaperStyle): List<Color> = when (style) {
    WallpaperStyle.PURPLE_GRADIENT -> PurpleColors
    WallpaperStyle.BLUE_GRADIENT -> BlueColors
    WallpaperStyle.SUNSET_GRADIENT -> SunsetColors
    WallpaperStyle.OCEAN_GRADIENT -> OceanColors
    WallpaperStyle.DARK_GRADIENT -> DarkColors
}

@Composable
fun WallpaperBackground(
    style: WallpaperStyle = WallpaperStyle.PURPLE_GRADIENT,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = colorsFor(style)))
    ) {
        content()
    }
}
