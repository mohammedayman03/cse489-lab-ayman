# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project overview

This repository contains the **CSE489 Parking App**, an Android application written in **Kotlin** using **Jetpack Compose**. The app manages parking locations (landmarks) via a REST API and displays nearby parking on a map.

Key technologies:
- Kotlin + Android Gradle Plugin
- Jetpack Compose + Material 3 theming
- Retrofit + OkHttp (with logging) + Gson for networking
- OSMDroid for map rendering
- Basic JUnit unit tests and Android instrumentation tests

## How to build and run

All Gradle commands are intended to be run from the repository root using the provided wrapper:

- **Assemble debug APK for the app module**
  - `./gradlew :app:assembleDebug`

- **Install debug build on a connected device/emulator**
  - `./gradlew :app:installDebug`

- **Clean build outputs**
  - `./gradlew clean`

Android Studio can also be used to run the `app` configuration directly; the entry point is `MainActivity` in the `com.example.cse489labapp` package.

## Lint and static checks

- **Run Android Lint for the debug variant**
  - `./gradlew :app:lintDebug`

- **Run all standard Android Lint tasks**
  - `./gradlew lint`

## Testing

Tests live under `app/src/test` (local unit tests) and `app/src/androidTest` (instrumentation tests).

Common commands:

- **Run all unit tests for the app module**
  - `./gradlew :app:testDebugUnitTest`

- **Run all Android instrumentation tests on a connected device/emulator**
  - `./gradlew :app:connectedDebugAndroidTest`

- **Run a single unit test class** (example uses the default sample test)
  - `./gradlew :app:testDebugUnitTest --tests "com.example.cse489labapp.ExampleUnitTest"`

## High-level architecture

### Modules

- **Root project**
  - `build.gradle.kts` defines common plugin aliases (Android application, Kotlin Android, Kotlin Compose) via the version catalog (`libs`).
  - `settings.gradle.kts` includes the `app` module.

- **`app` module**
  - Android application module with Compose UI, navigation, networking, and map integration.

### Entry point and theming

- **Entry point**: `MainActivity` (`app/src/main/java/com/example/cse489labapp/MainActivity.kt`)
  - Extends `ComponentActivity` and calls `setContent { CSE489LabAppTheme { App() } }`.
  - The `App()` composable sets up a `Scaffold` with a bottom navigation bar and hosts the navigation graph.

- **Theming**: `app/src/main/java/com/example/cse489labapp/ui/theme/`
  - `CSE489LabAppTheme` wraps Material 3 `MaterialTheme` with light/dark color schemes and optional dynamic color on Android 12+.
  - `Color.kt` and `Type.kt` define the color palette and typography used across the app.

### Navigation and screen structure

- **Navigation host**: `AppNavigation` (`app/src/main/java/com/example/cse489labapp/ui/AppNavigation.kt`)
  - Uses `NavHost` with a `NavHostController` from `MainActivity`.
  - Defines three top-level destinations by simple string routes:
    - `"overview"`
    - `"records"`
    - `"form"`

- **Bottom navigation** (in `App()` within `MainActivity`)
  - Three `NavigationBarItem`s correspond 1:1 with the routes: Overview, Records, and New Entry (Form).
  - `currentBackStackEntryAsState()` is used to highlight the currently selected tab.

- **Screens** (all under `app/src/main/java/com/example/cse489labapp/ui/screens/`)
  - `overview/OverviewScreen.kt`
    - Hosts an OSMDroid `MapView` via `AndroidView`.
    - Configures tile source, multi-touch support, default zoom, and centers the map on a fixed `GeoPoint` (currently BRACU’s coordinates).
  - `records/RecordsScreen.kt`
    - Placeholder Compose screen; currently displays a simple `"Records Screen"` text.
  - `form/FormScreen.kt`
    - Placeholder Compose screen; currently displays a simple `"Form Screen"` text.

Navigation responsibilities are split so that `MainActivity` owns the `NavController` and bottom bar, while `AppNavigation` and the individual screen composables own route-level content only.

### Networking and data layer

Networking is kept in top-level `api` and `model` packages under `app/src/main/java/`:

- **Model**: `model/Landmark.kt`
  - `data class Landmark` represents a landmark from the REST API, with fields `id`, `title`, `lat`, `lon`, and `image` (string URL/base64) annotated using Gson’s `@SerializedName`.

- **Retrofit API interface**: `api/LandmarkApi.kt`
  - `getLandmarks()` (GET `api.php?action=get`) returns a `Response<List<Landmark>>`.
  - `createLandmark(...)` (multipart POST `api.php?action=create`) uploads metadata and an image.
  - `updateLandmark(...)` (multipart POST `api.php?action=update`) updates an existing landmark, with an optional image part.
  - `deleteLandmark(id)` (form-encoded POST `api.php?action=delete`) deletes a landmark by ID.

- **Retrofit setup**: `api/RetrofitClient.kt`
  - Configures a singleton Retrofit instance with:
    - Base URL: `https://labs.anontech.info/cse489/t3/`.
    - `OkHttpClient` including a `HttpLoggingInterceptor` set to `BODY` for verbose request/response logging.
    - `GsonConverterFactory` for JSON serialization.
  - Exposes a lazily initialized `api: LandmarkApi` property.

- **Repository abstraction**: `api/LandmarkRepository.kt`
  - Wraps `RetrofitClient.api` and exposes suspend functions for high-level operations:
    - `getAllLandmarks()` → `Response<List<Landmark>>`.
    - `createLandmark(title, lat, lon, imagePart)` → converts primitives to `RequestBody`s and delegates to `createLandmark` on the API.
    - `updateLandmark(id, title, lat, lon, imagePart?)` → builds appropriate `RequestBody`s and delegates to `updateLandmark`.
    - `deleteLandmark(id)` → delegates to the API’s delete endpoint.

Currently there is no dedicated ViewModel layer; consumers (such as the `App()` composable) can directly create and invoke `LandmarkRepository`.

### Map integration

- OSMDroid is integrated only in the `OverviewScreen` for now:
  - `Configuration.getInstance().load(...)` is called before creating the `MapView` to set up OSMDroid configuration using shared preferences.
  - `MapView` is wrapped in `AndroidView`, allowing it to be used inside Compose UI.
  - Zoom level and map center (BRACU) are hard-coded; there is no dynamic binding to API data yet.

### API smoke test in the UI

- Within the `App()` composable (in `MainActivity.kt`), a `LaunchedEffect(Unit)` block performs a one-time API smoke test:
  - Instantiates `LandmarkRepository` and calls `getAllLandmarks()`.
  - Logs either the retrieved landmarks or an error code, and catches/logs exceptions.

This side-effect is useful for quick verification that the backend API is reachable when the app starts, and is the main place where network I/O is currently triggered from the UI.
