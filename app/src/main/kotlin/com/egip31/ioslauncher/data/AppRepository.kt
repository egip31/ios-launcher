package com.egip31.ioslauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap

/** Reads the list of launchable apps and renders their icons as iOS-style squircles. */
class AppRepository(private val context: Context) {

    private val pm: PackageManager = context.packageManager

    /** Query installed launchable apps. Suspendable because this can take >100 ms. */
    fun loadApps(): List<AppInfo> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)

        return resolveInfos
            .asSequence()
            .filter { it.activityInfo.packageName != context.packageName }
            .map { ri ->
                val label = ri.loadLabel(pm)?.toString().orEmpty()
                val icon = renderSquircle(ri.loadIcon(pm))
                AppInfo(
                    packageName = ri.activityInfo.packageName,
                    activityName = ri.activityInfo.name,
                    label = label.ifEmpty { ri.activityInfo.packageName },
                    icon = icon,
                )
            }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    /** Build a launch intent for an app and start it. */
    fun launch(app: AppInfo) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setClassName(app.packageName, app.activityName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        context.startActivity(intent)
    }

    /**
     * Render any Drawable into a fixed-size, rounded-square ("squircle") bitmap so all
     * icons share the iOS look-and-feel regardless of their original shape.
     */
    private fun renderSquircle(src: Drawable, sizePx: Int = ICON_SIZE_PX): Bitmap {
        val source = src.toBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)

        val output = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Draw squircle mask
        val path = squirclePath(sizePx.toFloat())
        canvas.drawPath(path, paint)

        // Source-in: keep only the parts of the icon inside the squircle
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(source, 0f, 0f, paint)
        paint.xfermode = null

        return output
    }

    private fun squirclePath(size: Float): Path {
        // Approximation of an iOS squircle using a rounded rect with ~22% radius.
        val radius = size * 0.22f
        val rect = RectF(0f, 0f, size, size)
        return Path().apply { addRoundRect(rect, radius, radius, Path.Direction.CW) }
    }

    companion object {
        private const val ICON_SIZE_PX = 168
    }
}
