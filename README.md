# Reef Safe

A utility app for snorkellers, free divers and scuba divers around Townsville and Magnetic Island. Open it and, at a glance, see whether the reef is worth driving out for today — sea temperature, wave height, wind, visibility, UV, sunrise / sunset, and a single 0–100 score with a verdict.

Built for **CP3406 Assessment 1** with Kotlin, Jetpack Compose, and Material Design 3.

---

## Features

- **Single 0–100 snorkel score** with a colour-coded verdict (Glass calm / Solid / Workable / Skip it)
- **Activity-aware weighting** — different weights for snorkelling, free diving and scuba diving
- **Location picker** — Townsville or Magnetic Island
- **Metric / Imperial** for temperature, wind, wave height and visibility
- **Display toggles** — hide the wave card, switch between 12- and 24-hour sunrise / sunset
- **Refresh** via a small FAB that re-fetches the latest data
- **Two screens only** — Utility (the score and the conditions) and Settings (the controls)

---

## Screens

### Utility

A scrolling column with:

- A score card at the top — big 0–100 number, verdict pill, "Location · Activity" subtitle
- A list of conditions — sea temperature, wave height (with period and direction), wind (with direction), visibility, UV index, air temperature, cloud cover, sunrise and sunset
- A small refresh FAB in the bottom-right

States:

- **Loading** — centred spinner with "Checking the reef…"
- **Error** — error message and a Retry button
- **Success** — the score and the conditions

### Settings

- **Location** — radio rows for Townsville and Magnetic Island
- **Activity** — segmented buttons for Snorkel / Free dive / Scuba
- **Units** — segmented buttons for Metric / Imperial
- **Display** — switches for "Show wave card" and "Use 24-hour time"

All settings are applied immediately to the Utility screen.

---

## Architecture

```
UI (Compose)  →  ViewModel  →  Repository  →  Retrofit
                                     ↓
                                 Open-Meteo
```

- **UI** is a function of state. The Utility screen collects a sealed `UtilityUiState` (`Loading` / `Success` / `Error`) and the current `Settings`.
- **ViewModels** combine the settings flow with the conditions flow into a single UI state using `combine { … }.stateIn(viewModelScope, …)`.
- **Repositories** own the data. `ReefRepository` calls both Open-Meteo endpoints and merges the responses into a `ReefConditions` value. `SettingsRepository` holds the current `Settings` in an in-memory `MutableStateFlow`.
- **DI** is provided by Hilt. `NetworkModule` exposes the two Retrofit services, the Moshi instance and an OkHttp client with a logging interceptor. `ReefSafeApp` is the `@HiltAndroidApp`; `MainActivity` is the `@AndroidEntryPoint`.

The package layout mirrors the layers:

```
au.edu.jcu.reefsafe/
├── MainActivity.kt              ← @AndroidEntryPoint, hosts UtilityApp()
├── ReefSafeApp.kt               ← @HiltAndroidApp
├── data/
│   ├── api/                     ← Retrofit services
│   ├── dto/                     ← Moshi response shapes
│   ├── model/                   ← ReefConditions
│   ├── repository/              ← ReefRepository
│   └── settings/                ← SettingsRepository
├── di/NetworkModule.kt          ← Retrofit, Moshi, OkHttp providers
├── domain/
│   ├── Settings.kt              ← Settings, Locations, Units, Activity
│   ├── SnorkelScore.kt          ← scoreConditions(), Verdict
│   └── UnitsFormatter.kt        ← metric/imperial + sun-time formatters
└── ui/
    ├── settings/                ← SettingsScreen + ViewModel
    ├── theme/                   ← Material 3 theme + reef palette
    └── utility/                 ← UtilityScreen + ViewModel
```

---

## Tech stack

| Area        | Library                                                |
|-------------|--------------------------------------------------------|
| Language    | Kotlin 2.0.21                                          |
| UI          | Jetpack Compose (BOM 2024.09.00), Material 3           |
| State       | StateFlow + `viewModelScope`                           |
| DI          | Hilt 2.52                                              |
| Networking  | Retrofit 2.11.0, Moshi 1.15.1, OkHttp 4.12.0           |
| Concurrency | kotlinx.coroutines 1.9.0                               |
| Build       | Gradle 8.x with Kotlin DSL, AGP 8.11.2, KSP            |

The default Material 3 palette was replaced with a teal / coral / sand ocean palette (`ReefTeal*`, `Coral*`, `Sand*` in `ui/theme/Color.kt`).

---

## Key concepts covered

| Week | Concept                                | Used in                                                                  |
|------|----------------------------------------|--------------------------------------------------------------------------|
| 1    | Kotlin + Android Studio                | All `.kt` files                                                          |
| 2    | Jetpack Compose layouts                | `UtilityScreen`, `SettingsScreen`, `UtilityApp`                          |
| 3    | Material Design 3                      | `ui/theme/Theme.kt`, `ui/theme/Color.kt`, M3 components throughout      |
| 4    | App architecture (VM, DI, Repository)  | `UtilityViewModel`, `SettingsViewModel`, `ReefRepository`, Hilt modules |
| 5    | Web APIs using Retrofit                | `OpenMeteoForecastService`, `OpenMeteoMarineService`, `NetworkModule`    |

---

## Getting started

### Requirements

- Android Studio (Hedgehog 2023.1.1 or newer)
- An emulator or physical device running **API 24+**

### Run

1. Clone the repo and open the project in Android Studio.
2. Let Gradle sync (the dependencies are declared in `gradle/libs.versions.toml`).
3. Pick a device and press **Run**.

The first launch fetches live data from Open-Meteo, so the device needs internet access.

---

## API

The app uses two free, no-key endpoints from [Open-Meteo](https://open-meteo.com/):

- **Forecast** — `GET https://api.open-meteo.com/v1/forecast` with the location's lat/lon, the `current` block requesting temperature, wind, visibility, UV and cloud cover, and the `daily` block for sunrise and sunset.
- **Marine** — `GET https://marine-api.open-meteo.com/v1/marine` with the same lat/lon and the `current` block requesting wave height, wave period, wave direction and sea surface temperature.

The two responses are merged into a `ReefConditions` data class used by both the score function and the conditions list.

---

## How the score is calculated

`scoreConditions(conditions, activity)` in `domain/SnorkelScore.kt` returns a `SnorkelScore` (an `Int` 0–100 plus a `Verdict`).

Each factor — waves, wind, visibility, sea-surface temperature, UV — is normalised to a 0–1 score and then weighted by activity:

| Activity    | Waves | Wind | Visibility | SST  | UV   |
|-------------|-------|------|------------|------|------|
| Snorkel     | 0.40  | 0.20 | 0.15       | 0.10 | 0.15 |
| Free dive   | 0.45  | 0.15 | 0.20       | 0.10 | 0.10 |
| Scuba       | 0.20  | 0.15 | 0.25       | 0.15 | 0.25 |

The raw weighted sum is scaled to 0–100 and bucketed:

- **Glass calm** — ≥ 80
- **Solid** — ≥ 60
- **Workable** — ≥ 40
- **Skip it** — < 40

Missing values from the API are treated as a neutral 0.5 so a single null doesn't tank the score.

---

## Tests

Two JUnit 4 test classes cover the pure-domain logic:

- `domain/UnitsFormatterTest` — metric / imperial conversions and the sun-time formatter
- `domain/SnorkelScoreTest` — verdict buckets, per-activity weights, null handling

Run with:

```
./gradlew :app:testDebugUnitTest
```

---

## Notes

- Settings are **in-memory only**, by design. The assignment brief notes that persistence is not required.
- The app is **two screens with bottom navigation**, as per the starter template.
- No third-party APIs beyond Open-Meteo are used.
- Run the app on a device or emulator to see the UI — no screenshots are included in this README.

---

## License

Educational use, CP3406.
