package com.egip31.ioslauncher.ui

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egip31.ioslauncher.ui.components.HomeIndicator
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Soft "lock screen" — not a real OS lock screen. Acts like a welcome curtain shown when
 * the launcher is opened from the HOME button; user swipes up to reveal the home grid.
 */
@Composable
fun LockScreen(
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var dragOffset by remember { mutableStateOf(0f) }

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1_000L)
        }
    }

    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFmt = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (dragOffset < -120f) onUnlock()
                        dragOffset = 0f
                    },
                    onDragCancel = { dragOffset = 0f },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        if (dragAmount < 0f) dragOffset += dragAmount
                    },
                )
            }
            .padding(top = 44.dp, bottom = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Spacer(Modifier.height(80.dp))
            Text(
                text = dateFmt.format(Date(now)),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = timeFmt.format(Date(now)),
                color = Color.White,
                fontSize = 88.sp,
                fontWeight = FontWeight.Light,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = "Swipe up to unlock",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(8.dp))
        }

        HomeIndicator(modifier = Modifier.align(Alignment.BottomCenter))
    }
}
