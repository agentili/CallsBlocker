# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run all unit tests (JVM, Robolectric)
./gradlew test

# Run a single test class
./gradlew test --tests "com.callsblocker.util.NumberMatcherTest"

# Run a single test method
./gradlew test --tests "com.callsblocker.util.NumberMatcherTest.testNormalizeInternational"

# Run all checks (lint + tests)
./gradlew check

# Run UI/instrumented tests (requires emulator or device API 29+)
./gradlew connectedAndroidTest

# Install debug APK on connected device
./gradlew installDebug
```

## Architecture

**Stack:** Kotlin 2.1 · Jetpack Compose + Material3 · Hilt · Room 2.7 · Coroutines + StateFlow · DataStore Preferences · minSdk 29 · targetSdk 36

**Package structure:**
- `data/` — Room entities (`BlockedEntry`, `BlockedCallLog`), DAOs (`BlockedEntryDao`, `BlockedCallLogDao`), `BlockedEntryRepository`, `CallAction` enum, `AppDatabase` (v2, `fallbackToDestructiveMigration`)
- `di/` — `AppModule` (Hilt `@Provides` for DB, `BlockedEntryDao`, and `BlockedCallLogDao`)
- `service/` — `CallBlockerService` (the actual call interceptor)
- `ui/` — `MainActivity`, `OnboardingActivity`, `CallLogPickerActivity`, `MainViewModel`, `OnboardingViewModel`, screens (`HomeScreen`, `SettingsScreen`, `InfoScreen`, `BlockedCallsScreen`), components (`StatusBanner`, `BlockedEntryItem`, `EntryBottomSheet`)
- `util/` — `NumberNormalizer`, `NumberMatcher`, `CsvManager`, `PrefsManager`, `AppRoleManager`, `BatteryOptimizationManager`, `NotificationHelper`

**UI language:** All user-facing strings are in Italian.

## Critical: How Call Blocking Works

The entire blocking chain lives in `CallBlockerService.onScreenCall()`:
1. Reads **all entries fresh from DB** on every call via `repository.getAllSync()` — there is intentionally no static cache
2. Normalizes the incoming number with `NumberNormalizer.normalize()`
3. Matches against each entry via `NumberMatcher.matches()` (exact `==` or prefix `startsWith()` per `isPrefix`)
4. Returns `CallResponse` based on matched `CallAction`:
   - **BLOCK** → `setDisallowCall(true)` + `setRejectCall(true)` (call rejected)
   - **SILENCE** → `setDisallowCall(true)` + `setRejectCall(false)` (call silenced, still rings silently)
   - **ALLOW** (or no match) → default `CallResponse` (call goes through normally)
5. **Always calls `respondToCall()`** — skipping it causes the system to allow the call after ~5 seconds
6. On match: logs to `BlockedCallLog` and shows a notification (if enabled in `PrefsManager`)

`runBlocking` in `onScreenCall()` is intentional — the callback is not inside a coroutine scope.

## Critical: Manifest Requirements

For `CallBlockerService` to be invoked by the telecom system, the manifest declaration must have all three:
- `android:permission="android.permission.BIND_SCREENING_SERVICE"`
- `android:exported="true"`
- `<intent-filter><action android:name="android.telecom.CallScreeningService"/>`

`BIND_SCREENING_SERVICE` must **not** appear in `<uses-permission>` — it is a system-only permission declared only on the `<service>` element.

## Number Normalization and Matching (two-stage process)

**Stage 1 — `NumberNormalizer.normalize()`:** Strips spaces, hyphens, parentheses, and the `+` sign. Then handles `00` international prefixes: `0039...` → `39...`, any `00XX...` → `XX...`. After normalization, numbers never start with `+` or `00`.

**Stage 2 — `NumberMatcher.matches()`:** After normalizing both incoming number and pattern, performs cross-prefix matching for the Italian country code `39`. If the incoming number starts with `39` but the pattern doesn't (or vice versa), it strips the `39` prefix and tries again. This means a pattern `333123456` matches both `333123456` and `39333123456` (and `+39333123456`).

Then applies either exact match (`==`) or prefix match (`startsWith()`) depending on `entry.isPrefix`.

## State and Navigation

`MainActivity` checks `PrefsManager.onboardingCompleted` on start; if false, it redirects to `OnboardingActivity` and calls `finish()`. After onboarding, `MainActivity` sets up a `NavHost` with routes `home`, `settings`, `info`, and `logs`.

`MainViewModel.UiState` holds four fields: `entries` (reactive Flow from Room), `callLogs` (reactive Flow from Room), `isScreeningActive` (checks `RoleManager`), `isBatteryOptimized`. The `StatusBanner` component reflects this state prominently in both `HomeScreen` and `SettingsScreen`.

## Onboarding Flow

`OnboardingActivity` runs a 5-step wizard: (1) Welcome, (2) READ_CALL_LOG permission, (3) ROLE_CALL_SCREENING role request, (4) Battery optimization exemption (skippable), (5) Completion. Steps advance via `OnboardingViewModel.nextStep()`. Each step proceeds regardless of whether the user grants or denies the permission/role.

## Call Logging

When a call matches a blacklist entry, it is logged to the `BlockedCallLog` table with the phone number, timestamp, action taken, and optional contact label — regardless of whether the action is BLOCK, SILENCE, or ALLOW. The `BlockedCallsScreen` (route `logs`) displays these logs ordered by most recent. Logs are persisted indefinitely unless manually cleared by the user.

## Dependency Injection

`AppModule` (SingletonComponent) provides `AppDatabase`, `BlockedEntryDao`, and `BlockedCallLogDao`. `BlockedEntryRepository` uses `@Inject constructor` with both DAOs. The service uses `@AndroidEntryPoint` — if `repository.isInitialized` is false at call time, it responds with a default allow response and returns early.

**Note on `PrefsManager`:** It is `@Singleton @Inject`-able via Hilt, but `MainActivity` creates it manually with `PrefsManager(this)` in `onCreate()` (before Hilt injection runs) to check onboarding status early.

## Database

Room database `callblocker.db` at version 2, using `fallbackToDestructiveMigration()`. Adding a new entity or changing the schema requires bumping the version. The destructive fallback means existing data is wiped on version mismatch — this is acceptable for the current user base but should be reconsidered before production release with real users.

## CSV Import/Export

`CsvManager` exports entries as `pattern,isPrefix,label,action`. Import accepts `,`, `;`, or tab as delimiters and auto-detects headers (`pattern` or `numero`). Missing fields default to `isPrefix=false`, empty label, `action=SILENCE`. Patterns must contain at least one digit.

## App Icon

Launcher icons are located in `app/src/main/res/mipmap-anydpi-v26/` (adaptive icons for Android 8+). The `AndroidManifest.xml` references the icon via `android:icon="@mipmap/ic_launcher"`. **Important:** XML declaration `<?xml version="1.0"?>` must appear **first** in adaptive icon XML files, before any comments or other content (XML standard requirement).

## Testing

- **Unit tests** (`app/src/test/`) use Robolectric + Turbine (Flow assertions) + MockK (mocking)
  - MockK provides `mockk<T>`, `coEvery`, `coVerify` for mocking and stubbing
  - Use `flowOf()` from coroutines-test for testing Flow-returning functions
  - `isReturnDefaultValues = true` in `testOptions` means unmocked calls return defaults (0, false, null)
- **UI/instrumented tests** (`app/src/androidTest/`) use Compose Testing framework + Espresso + MockK
  - `composeTestRule` from `createComposeRule()` manages Compose test lifecycle
  - Requires emulator or connected device with API 29+
  - Use `MockK for androidTest` to mock dependencies in integration scenarios
- **Key test suites:**
  - `ManifestValidationTest` — validates `CallBlockerService` manifest declaration
  - `MainViewModelTest`, `MainViewModelUpdateTest` — ViewModel state and updates
  - `NavigationTest` — navigation flow between screens
  - `HomeScreenTest`, `HomeScreenAddEntryTest`, `SettingsScreenTest`, `InfoScreenTest` — individual screen UI verification
  - `MainActivityUiTest`, `OnboardingActivityTest` — activity lifecycle and integration
  - Utility tests: `NumberNormalizerTest`, `NumberMatcherTest`, `CsvManagerTest`, `PrefsManagerTest`, `AppRoleManagerTest`
