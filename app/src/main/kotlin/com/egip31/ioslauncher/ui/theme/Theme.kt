package com.egip31.ioslauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val IosDark = darkColorScheme(
    primary = IosBlue,
    background = WallpaperTop,
    surface = WallpaperTop,
)

private val IosLight = lightColorScheme(
    primary = IosBlue,
    background = WallpaperTop,
    surface = WallpaperTop,
)

@Composable
fun IosLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) IosDark else IosLight,
        typography = IosTypography,
        content = content,
    )
}
