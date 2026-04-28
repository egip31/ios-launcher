package com.egip31.ioslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                .systemBarsPadding()
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
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
    val colors = when (style) {
        WallpaperStyle.PURPLE_GRADIENT -> listOf(Color(0xFF1B3A5B), Color(0xFF8E2DE2))
        WallpaperStyle.BLUE_GRADIENT -> listOf(Color(0xFF0F2027), Color(0xFF2C5364))
        WallpaperStyle.SUNSET_GRADIENT -> listOf(Color(0xFFFF512F), Color(0xFFA20097))
        WallpaperStyle.OCEAN_GRADIENT -> listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
        WallpaperStyle.DARK_GRADIENT -> listOf(Color(0xFF000000), Color(0xFF1C1C1E))
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(colors))
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) Color.White else Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onClick)
        )
        Text(
            style.label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
