package com.egip31.ioslauncher.data

import android.content.Context
import android.content.SharedPreferences

enum class WallpaperStyle(val label: String) {
    PURPLE_GRADIENT("Purple"),
    BLUE_GRADIENT("Blue"),
    SUNSET_GRADIENT("Sunset"),
    OCEAN_GRADIENT("Ocean"),
    DARK_GRADIENT("Dark"),
}

/** Tiny SharedPreferences-backed store for launcher options. */
class LauncherPreferences(context: Context) {

    private val sp: SharedPreferences =
        context.getSharedPreferences("launcher_prefs", Context.MODE_PRIVATE)

    var wallpaper: WallpaperStyle
        get() = runCatching {
            WallpaperStyle.valueOf(
                sp.getString(KEY_WALLPAPER, WallpaperStyle.PURPLE_GRADIENT.name)
                    ?: WallpaperStyle.PURPLE_GRADIENT.name
            )
        }.getOrDefault(WallpaperStyle.PURPLE_GRADIENT)
        set(value) { sp.edit().putString(KEY_WALLPAPER, value.name).apply() }

    var showLockScreen: Boolean
        get() = sp.getBoolean(KEY_LOCK_SCREEN, true)
        set(value) { sp.edit().putBoolean(KEY_LOCK_SCREEN, value).apply() }

    companion object {
        private const val KEY_WALLPAPER = "wallpaper"
        private const val KEY_LOCK_SCREEN = "lock_screen"
    }
}
