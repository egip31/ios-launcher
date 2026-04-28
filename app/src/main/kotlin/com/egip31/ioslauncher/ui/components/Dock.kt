package com.egip31.ioslauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.egip31.ioslauncher.data.AppInfo
import com.egip31.ioslauncher.ui.theme.DockBorder
import com.egip31.ioslauncher.ui.theme.DockGlass

@Composable
fun Dock(
    apps: List<AppInfo>,
    onLaunchApp: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
    onLongClickApp: ((AppInfo) -> Unit)? = null,
    jiggle: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(DockGlass)
            .border(width = 1.dp, color = DockBorder, shape = RoundedCornerShape(28.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        apps.forEach { app ->
            AppIcon(
                app = app,
                onClick = { onLaunchApp(app) },
                onLongClick = onLongClickApp?.let { handler -> { handler(app) } },
                showLabel = false,
                iconSize = 56.dp,
                jiggle = jiggle,
            )
        }
    }
}
