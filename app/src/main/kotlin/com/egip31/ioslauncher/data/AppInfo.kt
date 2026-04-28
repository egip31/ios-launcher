package com.egip31.ioslauncher.data

import android.graphics.Bitmap

/** Lightweight representation of an installed launchable app. */
data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Bitmap,
) {
    val id: String get() = "$packageName/$activityName"
}
