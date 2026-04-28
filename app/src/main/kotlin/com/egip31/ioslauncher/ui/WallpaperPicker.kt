package com.egip31.ioslauncher.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egip31.ioslauncher.data.WallpaperStyle

@Composable
fun WallpaperPicker(
    current: WallpaperStyle,
    onSelect: (WallpaperStyle) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(top = 44.dp, bottom = 24.dp)
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF2C2C2E))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Pilih Wallpaper",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WallpaperStyle.values().forEach { style ->
                    WallpaperSwatch(
                        style = style,
                        selected = current == style,
                        onClick = {
                            onSelect(style)
                            onDismiss()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun WallpaperSwatch(
    style: WallpaperStyle,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val (base, blobs) = swatchPalette(style)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(base))
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) Color.White else Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onClick),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                blobs.forEach { (cx, cy, r, color) ->
                    val rPx = r * size.minDimension
                    val center = Offset(cx * size.width, cy * size.height)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(color, color.copy(alpha = 0f)),
                            center = center,
                            radius = rPx,
                        ),
                        radius = rPx,
                        center = center,
                    )
                }
            }
        }
        Text(
            style.label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

private data class Blob(val cx: Float, val cy: Float, val r: Float, val color: Color)

private fun swatchPalette(style: WallpaperStyle): Pair<List<Color>, List<Blob>> = when (style) {
    WallpaperStyle.PURPLE_GRADIENT -> Pair(
        listOf(Color(0xFF150823), Color(0xFF180A2B)),
        listOf(
            Blob(0.2f, 0.2f, 0.6f, Color(0xFFB14CFF).copy(alpha = 0.6f)),
            Blob(0.8f, 0.7f, 0.55f, Color(0xFFFF66C4).copy(alpha = 0.55f)),
        ),
    )
    WallpaperStyle.BLUE_GRADIENT -> Pair(
        listOf(Color(0xFF000A1F), Color(0xFF010E22)),
        listOf(
            Blob(0.25f, 0.25f, 0.6f, Color(0xFF2EA3FF).copy(alpha = 0.6f)),
            Blob(0.75f, 0.7f, 0.55f, Color(0xFF1E3A8A).copy(alpha = 0.6f)),
        ),
    )
    WallpaperStyle.SUNSET_GRADIENT -> Pair(
        listOf(Color(0xFF1A0014), Color(0xFF1A0014)),
        listOf(
            Blob(0.25f, 0.25f, 0.55f, Color(0xFFFF8A4C).copy(alpha = 0.65f)),
            Blob(0.7f, 0.7f, 0.55f, Color(0xFFFF3E78).copy(alpha = 0.6f)),
        ),
    )
    WallpaperStyle.OCEAN_GRADIENT -> Pair(
        listOf(Color(0xFF001528), Color(0xFF000F1F)),
        listOf(
            Blob(0.25f, 0.25f, 0.6f, Color(0xFF26D0CE).copy(alpha = 0.55f)),
            Blob(0.75f, 0.7f, 0.6f, Color(0xFF1A2980).copy(alpha = 0.65f)),
        ),
    )
    WallpaperStyle.DARK_GRADIENT -> Pair(
        listOf(Color(0xFF000000), Color(0xFF000000)),
        listOf(
            Blob(0.3f, 0.3f, 0.5f, Color(0xFF333333).copy(alpha = 0.55f)),
        ),
    )
}
