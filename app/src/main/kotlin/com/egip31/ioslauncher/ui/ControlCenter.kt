package com.egip31.ioslauncher.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egip31.ioslauncher.ui.components.HomeIndicator

/**
 * iOS-style Control Center sheet. Looks like iOS, but the toggles that need privileged
 * permissions (Wi-Fi, Bluetooth, Brightness, etc.) just open the corresponding Android
 * settings panel — so the underlying state changes happen via real Android Settings.
 */
@Composable
fun ControlCenter(
    onDismiss: () -> Unit,
    onChangeWallpaper: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    fun openSettings(action: String) {
        runCatching {
            ctx.startActivity(Intent(action).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Top row: connectivity tile (4 toggles) + audio tile (1 big)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Tile(modifier = Modifier.weight(1f).height(168.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircleToggle(Icons.Filled.AirplanemodeActive, Color(0xFFF95F2A)) {
                                openSettings(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                            }
                            CircleToggle(Icons.Filled.NetworkCell, Color(0xFF34C759)) {
                                openSettings(Settings.ACTION_DATA_ROAMING_SETTINGS)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircleToggle(Icons.Filled.Wifi, Color(0xFF007AFF)) {
                                openSettings(Settings.ACTION_WIFI_SETTINGS)
                            }
                            CircleToggle(Icons.Filled.Bluetooth, Color(0xFF007AFF)) {
                                openSettings(Settings.ACTION_BLUETOOTH_SETTINGS)
                            }
                        }
                    }
                }

                Tile(modifier = Modifier.weight(1f).height(168.dp)) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Icon(Icons.Filled.MusicNote, null, tint = Color.White.copy(alpha = 0.85f))
                        Column {
                            Text("Now Playing", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(
                                "—",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }

            // Slider tiles row: brightness + volume
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SliderTile(
                    icon = Icons.Filled.BrightnessHigh,
                    onClick = { openSettings(Settings.ACTION_DISPLAY_SETTINGS) },
                    modifier = Modifier.weight(1f),
                )
                SliderTile(
                    icon = Icons.Filled.VolumeUp,
                    onClick = { openSettings(Settings.ACTION_SOUND_SETTINGS) },
                    modifier = Modifier.weight(1f),
                )
            }

            // Bottom row of single-icon tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IconOnlyTile(Icons.Filled.RotateLeft, "Orientation") {
                    openSettings(Settings.ACTION_DISPLAY_SETTINGS)
                }
                IconOnlyTile(Icons.Filled.FlashOn, "Flashlight") {
                    openSettings(Settings.ACTION_SETTINGS)
                }
                IconOnlyTile(Icons.Filled.Wallpaper, "Wallpaper") {
                    onDismiss()
                    onChangeWallpaper()
                }
            }

            HomeIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
private fun Tile(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.18f)),
    ) { content() }
}

@Composable
private fun CircleToggle(icon: ImageVector, tint: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(50))
            .background(tint)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = Color.White)
    }
}

@Composable
private fun SliderTile(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(168.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // Pseudo-slider visual — a vertical track with the icon at the bottom.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.25f)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.White.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = Color.Black.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun IconOnlyTile(icon: ImageVector, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = label, tint = Color.White)
    }
}
