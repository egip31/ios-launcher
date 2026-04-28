package com.egip31.ioslauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.egip31.ioslauncher.data.WallpaperStyle

/**
 * Each wallpaper style is rendered as a base linear gradient with a stack of soft
 * radial "bokeh" blobs on top, mimicking the look of iOS 18's default wallpapers.
 */
private data class Blob(val centerX: Float, val centerY: Float, val radius: Float, val color: Color)

private fun palette(style: WallpaperStyle): Pair<List<Color>, List<Blob>> = when (style) {
    WallpaperStyle.PURPLE_GRADIENT -> Pair(
        listOf(Color(0xFF150823), Color(0xFF2B0A4E), Color(0xFF180A2B)),
        listOf(
            Blob(0.2f, 0.18f, 0.55f, Color(0xFFB14CFF).copy(alpha = 0.55f)),
            Blob(0.85f, 0.32f, 0.45f, Color(0xFFFF66C4).copy(alpha = 0.45f)),
            Blob(0.5f, 0.85f, 0.6f, Color(0xFF6A2EFF).copy(alpha = 0.55f)),
        ),
    )
    WallpaperStyle.BLUE_GRADIENT -> Pair(
        listOf(Color(0xFF000A1F), Color(0xFF062446), Color(0xFF010E22)),
        listOf(
            Blob(0.18f, 0.22f, 0.55f, Color(0xFF2EA3FF).copy(alpha = 0.55f)),
            Blob(0.82f, 0.5f, 0.5f, Color(0xFF00E5FF).copy(alpha = 0.45f)),
            Blob(0.55f, 0.85f, 0.55f, Color(0xFF1E3A8A).copy(alpha = 0.6f)),
        ),
    )
    WallpaperStyle.SUNSET_GRADIENT -> Pair(
        listOf(Color(0xFF1A0014), Color(0xFF3F0A2E), Color(0xFF1A0014)),
        listOf(
            Blob(0.2f, 0.22f, 0.55f, Color(0xFFFF8A4C).copy(alpha = 0.6f)),
            Blob(0.78f, 0.4f, 0.45f, Color(0xFFFF3E78).copy(alpha = 0.55f)),
            Blob(0.5f, 0.85f, 0.6f, Color(0xFFAA0066).copy(alpha = 0.5f)),
        ),
    )
    WallpaperStyle.OCEAN_GRADIENT -> Pair(
        listOf(Color(0xFF001528), Color(0xFF003D5C), Color(0xFF000F1F)),
        listOf(
            Blob(0.25f, 0.2f, 0.55f, Color(0xFF26D0CE).copy(alpha = 0.55f)),
            Blob(0.8f, 0.45f, 0.5f, Color(0xFF1A2980).copy(alpha = 0.6f)),
            Blob(0.45f, 0.85f, 0.55f, Color(0xFF00B4D8).copy(alpha = 0.5f)),
        ),
    )
    WallpaperStyle.DARK_GRADIENT -> Pair(
        listOf(Color(0xFF000000), Color(0xFF0A0A0A), Color(0xFF000000)),
        listOf(
            Blob(0.3f, 0.25f, 0.5f, Color(0xFF333333).copy(alpha = 0.55f)),
            Blob(0.75f, 0.7f, 0.5f, Color(0xFF1C1C1E).copy(alpha = 0.7f)),
        ),
    )
}

@Composable
fun WallpaperBackground(
    style: WallpaperStyle = WallpaperStyle.PURPLE_GRADIENT,
    content: @Composable () -> Unit,
) {
    val (base, blobs) = palette(style)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = base))
    ) {
        // Render each blob as a radial gradient that fades to transparent. Stacking them
        // produces the soft, blurred-light look of iOS 18's gradient wallpapers without
        // needing actual blur (which is API 31+).
        Canvas(modifier = Modifier.fillMaxSize()) {
            blobs.forEach { blob ->
                val r = blob.radius * size.minDimension * 1.2f
                val c = Offset(blob.centerX * size.width, blob.centerY * size.height)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(blob.color, blob.color.copy(alpha = 0f)),
                        center = c,
                        radius = r,
                    ),
                    radius = r,
                    center = c,
                )
            }
        }
        content()
    }
}
