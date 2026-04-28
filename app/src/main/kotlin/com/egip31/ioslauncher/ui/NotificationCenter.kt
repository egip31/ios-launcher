package com.egip31.ioslauncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Battery6Bar
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egip31.ioslauncher.ui.components.HomeIndicator
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Notification Center / Today View — large clock + a few static widgets. Real notifications
 * require NotificationListenerService permission, which is intentionally left as a follow-up.
 */
@Composable
fun NotificationCenter(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(15_000L)
        }
    }
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFmt = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = dateFmt.format(Date(now)),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = timeFmt.format(Date(now)),
                color = Color.White,
                fontSize = 72.sp,
                fontWeight = FontWeight.Light,
            )

            Spacer(Modifier.height(8.dp))

            // Today widgets row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WidgetCard(
                    icon = Icons.Filled.WbSunny,
                    title = "Weather",
                    value = "—",
                    subtitle = "Cuaca",
                    modifier = Modifier.weight(1f),
                )
                WidgetCard(
                    icon = Icons.Filled.CalendarMonth,
                    title = "Calendar",
                    value = SimpleDateFormat("d", Locale.getDefault()).format(Date(now)),
                    subtitle = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date(now)),
                    modifier = Modifier.weight(1f),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WidgetCard(
                    icon = Icons.Filled.AccessTime,
                    title = "Up Next",
                    value = "—",
                    subtitle = "No events",
                    modifier = Modifier.weight(1f),
                )
                WidgetCard(
                    icon = Icons.Filled.Battery6Bar,
                    title = "Battery",
                    value = "—",
                    subtitle = "Status",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.weight(1f))

            // Hint that real notifications need a permission grant
            Text(
                text = "Notifikasi belum diaktifkan. Berikan akses Notification Listener untuk menampilkan notifikasi asli.",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            )

            HomeIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
private fun WidgetCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(icon, null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(16.dp))
            Text(title, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
        }
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
        Text(subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}
