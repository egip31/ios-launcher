package com.egip31.ioslauncher.ui

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egip31.ioslauncher.data.AppInfo

/**
 * iOS-style long-press context menu shown over a dimmed home screen.
 * Action button look mirrors iOS's "Edit Home Screen / Share App / Remove App / Info" sheet.
 */
@Composable
fun AppContextMenu(
    app: AppInfo,
    onDismiss: () -> Unit,
    onUninstall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .width(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2C2C2E)),
        ) {
            MenuRow(
                label = app.label,
                icon = Icons.Filled.Info,
                onClick = {
                    runCatching {
                        ctx.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.parse("package:${app.packageName}")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                    }
                    onDismiss()
                },
            )
            Divider()
            MenuRow(
                label = "Share App",
                icon = Icons.Filled.Share,
                onClick = {
                    val playUrl = "https://play.google.com/store/apps/details?id=${app.packageName}"
                    runCatching {
                        ctx.startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, playUrl)
                                },
                                "Share ${app.label}"
                            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                        )
                    }
                    onDismiss()
                },
            )
            Divider()
            MenuRow(
                label = "View in Store",
                icon = Icons.Filled.ShoppingCart,
                onClick = {
                    runCatching {
                        ctx.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=${app.packageName}")
                            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                        )
                    }
                    onDismiss()
                },
            )
            Divider()
            MenuRow(
                label = "Remove App",
                icon = Icons.Filled.Delete,
                tint = Color(0xFFFF453A),
                onClick = {
                    onUninstall()
                    onDismiss()
                },
            )
        }
    }
}

@Composable
private fun MenuRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    tint: Color = Color.White,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = tint, fontSize = 16.sp)
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(Color.White.copy(alpha = 0.15f))
    )
}
