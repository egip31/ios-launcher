# iOS Launcher for Android

A native Android home-screen replacement that mimics the look and gestures of **iOS 18**, built with **Kotlin** and **Jetpack Compose**.

> ⚠️ This is a UI clone — not a port of iOS. Some iOS features (Dynamic Island, Face ID, system-level Control Center toggles) cannot be fully replicated on Android without root or system-level permissions.

## Features

- 🏠 **iOS-style home screen** with a 4-column grid of app icons across multiple pages.
- ⛴️ **Glassy bottom dock** with the four most common system apps (Phone, Messages, Browser, Settings) and a translucent rounded background.
- 📚 **App Library** — swipe left from the last home page to reveal a searchable, alphabetical list of every installed app.
- 🟪 **iOS-like wallpaper** — vertical purple→blue gradient as the default backdrop.
- 🟫 **Squircle icons** — every app icon is masked into the iOS rounded-square shape so the home screen feels visually consistent.
- 🔘 **Page indicator dots** above the dock.
- 📱 **Edge-to-edge** layout that respects status / navigation bar insets.
- 🏷️ **Registers as a real Android launcher** via the `android.intent.category.HOME` intent filter, so it can be set as the default home app from system settings.

## Tech stack

| Component | Version |
|-----------|---------|
| Kotlin | 1.9.22 |
| Android Gradle Plugin | 8.2.2 |
| Compose BOM | 2024.02.01 |
| Compose Compiler | 1.5.8 |
| compileSdk / targetSdk | 34 |
| minSdk | 26 (Android 8.0) |
| JDK | 17 |

## Build

You need:

- JDK 17
- Android SDK with `platforms;android-34` and `build-tools;34.0.0` (the `local.properties` file should point `sdk.dir` at your Android SDK).

Debug APK:

```bash
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk` (~15 MB).

Release APK (unsigned by default — sign with your own keystore for production):

```bash
./gradlew assembleRelease
```

## Install

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Then go to **Settings → Apps → Default apps → Home app** and pick **iOS Launcher**, or simply press the Home button and choose this launcher when prompted.

## Project structure

```
app/src/main/kotlin/com/egip31/ioslauncher/
├── MainActivity.kt              — single ComponentActivity, hosts the Compose UI
├── data/
│   ├── AppInfo.kt               — installed-app data class
│   └── AppRepository.kt         — queries PackageManager + renders squircle icons
└── ui/
    ├── LauncherViewModel.kt     — loads apps, picks dock items, paginates the grid
    ├── LauncherRoot.kt          — outer 2-page pager (Home ⇄ App Library)
    ├── HomeScreen.kt            — paginated grid + dock + page indicator
    ├── AppLibraryScreen.kt      — searchable app drawer
    ├── components/
    │   ├── AppIcon.kt
    │   ├── Dock.kt
    │   ├── PageIndicator.kt
    │   └── Wallpaper.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Roadmap

- [ ] Real iOS-style blur (RenderEffect) on the dock and Control Center.
- [ ] Pull-down Notification Center / Control Center sheet.
- [ ] Folders (drag two icons together to group).
- [ ] Widgets (battery, weather, calendar) using AppWidgetHost.
- [ ] Spotlight-style search (system + web).
- [ ] iOS-accurate haptics.
- [ ] Long-press to enter "jiggle mode" with reorderable icons.

## License

MIT — do whatever you want with it. This project is not affiliated with or endorsed by Apple Inc.
