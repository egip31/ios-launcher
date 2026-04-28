package com.egip31.ioslauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.egip31.ioslauncher.data.rememberSystemStatus
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * iOS-style status bar overlay. Live values:
 * - **Time** updates every 15s.
 * - **Cellular signal** (0..4 bars) — best-effort; hidden if unknown.
 * - **Wi-Fi** — connected vs not.
 * - **Battery** — level + charging state.
 *
 * Drawn entirely in Compose so we don't rely on Material icons that don't match iOS shapes.
 */
@Composable
fun StatusBar(modifier: Modifier = Modifier) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(15_000L)
        }
    }
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val time = timeFmt.format(Date(now))

    val status = rememberSystemStatus()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(STATUS_BAR_HEIGHT)
            .padding(horizontal = 24.dp),
    ) {
        // Time on the left.
        Text(
            text = time,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterStart),
        )

        // Right cluster: signal · wifi · battery.
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (status.cellularLevel >= 0) SignalBars(level = status.cellularLevel)
            WifiGlyph(connected = status.wifiConnected)
            BatteryGlyph(percent = status.batteryPct, charging = status.charging)
        }
    }
}

val STATUS_BAR_HEIGHT = 44.dp

@Composable
private fun SignalBars(level: Int) {
    Canvas(modifier = Modifier.size(width = 18.dp, height = 12.dp)) {
        val barCount = 4
        val gap = size.width * 0.12f
        val barWidth = (size.width - gap * (barCount - 1)) / barCount
        for (i in 0 until barCount) {
            val active = i < level + 1
            val barHeight = size.height * (0.35f + 0.22f * i)
            val left = i * (barWidth + gap)
            drawRoundRect(
                color = if (active) Color.White else Color.White.copy(alpha = 0.35f),
                topLeft = androidx.compose.ui.geometry.Offset(left, size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth * 0.5f, barWidth * 0.5f),
            )
        }
    }
}

@Composable
private fun WifiGlyph(connected: Boolean) {
    val tint = if (connected) Color.White else Color.White.copy(alpha = 0.35f)
    Canvas(modifier = Modifier.size(width = 16.dp, height = 12.dp)) {
        // Three concentric arcs + dot — classic Wi-Fi.
        val cx = size.width / 2f
        val cy = size.height
        val arcThickness = size.height * 0.12f
        for (i in 0..2) {
            val r = (i + 1) * (size.width * 0.18f)
            drawArc(
                color = tint.copy(alpha = if (i == 2) 1f else 0.85f),
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                style = Stroke(width = arcThickness),
                topLeft = androidx.compose.ui.geometry.Offset(cx - r, cy - r),
                size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
            )
        }
        drawCircle(
            color = tint,
            radius = size.width * 0.08f,
            center = androidx.compose.ui.geometry.Offset(cx, cy - size.width * 0.08f),
        )
    }
}

@Composable
private fun BatteryGlyph(percent: Int, charging: Boolean) {
    val pct = (percent.coerceIn(0, 100)) / 100f
    val baseColor = when {
        charging -> Color(0xFF34C759)
        percent <= 20 -> Color(0xFFFF453A)
        else -> Color.White
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$percent",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(end = 4.dp),
        )
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(11.dp)
                .clip(RoundedCornerShape(3.dp))
                .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(3.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(pct)
                    .height(11.dp)
                    .padding(1.dp)
                    .background(baseColor, RoundedCornerShape(2.dp)),
            )
        }
        // Battery cap (small nub on the right).
        Spacer(modifier = Modifier.width(1.dp))
        Box(
            modifier = Modifier
                .height(5.dp)
                .width(2.dp)
                .clip(RoundedCornerShape(topEnd = 1.dp, bottomEnd = 1.dp))
                .background(Color.White.copy(alpha = 0.7f)),
        )
    }
}
