package com.egip31.ioslauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Static, cosmetic Dynamic Island pill. Real Dynamic Island lives at the system level
 * on iPhone hardware — on Android we draw a pill that only mimics the look.
 */
@Composable
fun DynamicIsland(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
        )
    }
}
