package com.egip31.ioslauncher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egip31.ioslauncher.data.AppInfo

@Composable
fun AppIcon(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    iconSize: androidx.compose.ui.unit.Dp = 56.dp,
) {
    val bitmap = remember(app.id) { app.icon.asImageBitmap() }
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = app.label,
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(iconSize * 0.22f)),
        )
        if (showLabel) {
            Text(
                text = app.label,
                color = Color.White,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
