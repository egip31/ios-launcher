package com.egip31.ioslauncher.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.egip31.ioslauncher.data.AppInfo
import com.egip31.ioslauncher.data.AppRepository
import com.egip31.ioslauncher.data.LauncherPreferences
import com.egip31.ioslauncher.data.WallpaperStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Scene { LOCK, HOME, SPOTLIGHT }

enum class Overlay { NONE, CONTROL_CENTER, NOTIFICATION_CENTER }

data class LauncherState(
    val loading: Boolean = true,
    val apps: List<AppInfo> = emptyList(),
    val dock: List<AppInfo> = emptyList(),
    val homePages: List<List<AppInfo>> = emptyList(),
    val scene: Scene = Scene.LOCK,
    val overlay: Overlay = Overlay.NONE,
    val contextMenuApp: AppInfo? = null,
    val jiggleMode: Boolean = false,
    val wallpaper: WallpaperStyle = WallpaperStyle.PURPLE_GRADIENT,
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AppRepository(application)
    private val prefs = LauncherPreferences(application)

    private val _state = MutableStateFlow(LauncherState())
    val state: StateFlow<LauncherState> = _state.asStateFlow()

    init {
        // Restore wallpaper choice + lock-screen preference, then load apps.
        _state.value = _state.value.copy(
            wallpaper = prefs.wallpaper,
            scene = if (prefs.showLockScreen) Scene.LOCK else Scene.HOME,
        )
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            val apps = withContext(Dispatchers.IO) { repo.loadApps() }

            val dockHints = listOf(
                "com.android.dialer", "com.google.android.dialer",
                "com.android.messaging", "com.google.android.apps.messaging",
                "com.android.chrome", "com.google.android.googlequicksearchbox",
                "com.android.settings",
            )
            val dock = dockHints
                .mapNotNull { hint -> apps.firstOrNull { it.packageName == hint } }
                .take(4)
                .ifEmpty { apps.take(4) }

            val rest = apps - dock.toSet()
            val pages = rest.chunked(PAGE_SIZE)

            _state.value = _state.value.copy(
                loading = false,
                apps = apps,
                dock = dock,
                homePages = if (pages.isEmpty()) listOf(emptyList()) else pages,
            )
        }
    }

    fun launchApp(app: AppInfo) {
        runCatching { repo.launch(app) }
    }

    fun unlock() {
        _state.value = _state.value.copy(scene = Scene.HOME)
    }

    fun showLock() {
        _state.value = _state.value.copy(scene = Scene.LOCK, overlay = Overlay.NONE)
    }

    fun showSpotlight() {
        _state.value = _state.value.copy(scene = Scene.SPOTLIGHT)
    }

    fun hideSpotlight() {
        _state.value = _state.value.copy(scene = Scene.HOME)
    }

    fun showControlCenter() {
        _state.value = _state.value.copy(overlay = Overlay.CONTROL_CENTER)
    }

    fun showNotificationCenter() {
        _state.value = _state.value.copy(overlay = Overlay.NOTIFICATION_CENTER)
    }

    fun dismissOverlay() {
        _state.value = _state.value.copy(overlay = Overlay.NONE)
    }

    fun showContextMenu(app: AppInfo) {
        _state.value = _state.value.copy(contextMenuApp = app, jiggleMode = true)
    }

    fun hideContextMenu() {
        _state.value = _state.value.copy(contextMenuApp = null)
    }

    fun exitJiggleMode() {
        _state.value = _state.value.copy(jiggleMode = false, contextMenuApp = null)
    }

    fun uninstallApp(app: AppInfo) {
        runCatching {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:${app.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            getApplication<Application>().startActivity(intent)
        }
    }

    fun setWallpaper(style: WallpaperStyle) {
        prefs.wallpaper = style
        _state.value = _state.value.copy(wallpaper = style)
    }

    companion object {
        const val PAGE_SIZE = 24

        fun factory(app: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer { LauncherViewModel(app) }
        }
    }
}
