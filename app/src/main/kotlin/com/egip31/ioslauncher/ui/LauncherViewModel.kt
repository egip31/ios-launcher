package com.egip31.ioslauncher.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.egip31.ioslauncher.data.AppInfo
import com.egip31.ioslauncher.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LauncherState(
    val loading: Boolean = true,
    val apps: List<AppInfo> = emptyList(),
    val dock: List<AppInfo> = emptyList(),
    val homePages: List<List<AppInfo>> = emptyList(),
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AppRepository(application)

    private val _state = MutableStateFlow(LauncherState())
    val state: StateFlow<LauncherState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            val apps = withContext(Dispatchers.IO) { repo.loadApps() }

            // Pick four "dock" apps using common system package hints; fall back to the
            // first four apps alphabetically if not present.
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

            // Remaining apps populate home pages, 24 per page (iOS-like 4 cols x 6 rows).
            val rest = apps - dock.toSet()
            val pages = rest.chunked(PAGE_SIZE)

            _state.value = LauncherState(
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

    companion object {
        const val PAGE_SIZE = 24

        fun factory(app: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer { LauncherViewModel(app) }
        }
    }
}
