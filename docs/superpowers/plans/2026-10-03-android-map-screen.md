# Android MapScreen Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Show the Earthquakes screen's loaded events as selectable pins on an edge-to-edge Android map framed to its selected country.

**Architecture:** The list controller publishes a shared immutable presentation snapshot. A common map controller owns selection, and a thin Android ViewModel exposes its state. The Map UI observes this state without networking and uses the existing detail destination.

**Tech Stack:** Kotlin Multiplatform, Android Compose, Navigation 3, Koin annotations/compiler, coroutines StateFlow, and `eu.buney.maps:kmp-maps-compose:0.6.1`; retain all catalog versions.

**Spec:** [Approved Android MapScreen design](../specs/2026-10-03-android-map-screen-design.md).

## Global Constraints

- Reuse the currently loaded dataset; entering Map does not fetch, refresh, or paginate.
- The map fills the screen without a top app bar or reserved header space; existing bottom navigation remains on Map overview.
- Domain and transport models remain internal; common presentation logic has no Compose/platform APIs.
- Preserve repository/use-case contracts, existing direct controller construction, native iOS UI, and list/detail behavior.
- Do not add or modify tests or test infrastructure. Existing checks and explicit manual acceptance checks replace test-writing steps in this plan, as required by AGENTS.md.
- Use the checked-in Gradle wrapper and JDK 17; use existing local maps configuration without exposing or committing secrets.
- Use emulator to check and confirm during implementation. Record results; leave emulator confirmation incomplete if unavailable.
- Preserve task-start changes; review only this task's delta. Never hand-edit generated outputs or upgrade dependencies.
- Read the approved spec before execution, establish a fresh working-tree baseline, and use the worktree skill at execution time when isolation is needed.

## Review Focus

Existing tests do not cover the new map. Add no test files; verify each condition manually in its owning task and repeat on a device during Task 5.

1. Rapid country/magnitude changes during a request: obsolete results cannot appear under a newer country's context (Task 1).
2. Pagination, tab changes, and Back from details: pins update without unwanted camera resets or additional requests (Tasks 3–5).
3. Global/world bounds and map initialization: no invalid longitude-span/polar fit or bounds update before layout (Task 3).
4. Empty/restored state and malformed coordinates: a useful map/empty state, no stuck spinner, and only valid pins (Tasks 1, 4–5).
5. Small screens, large text, dark mode, and bottom navigation: summary actions, recentering, and attribution remain visible and accessible (Tasks 3–5).

## File Responsibilities

- `feature/earthquake/src/commonMain/kotlin/com/sgmobile/earthquake/feature/earthquake/map/presentation/EarthquakeMapState.kt`: immutable snapshot, country/bounds, pin, and selection models.
- Same directory, `EarthquakeMapSnapshotStore.kt`: singleton state storage; `EarthquakeMapController.kt`: selection and observation; `EarthquakeMapMapping.kt`: internal domain-to-pin/country mapping.
- `feature/earthquake/src/commonMain/kotlin/com/sgmobile/earthquake/feature/earthquake/overview/presentation/EarthquakeOverviewController.kt`: publish list/filter/loading changes and coordinate requests.
- Same overview directory, `EarthquakeOverviewControllerFactory.kt`: explicit annotation-based production wiring without default-argument injection ambiguity.
- `feature/earthquake/src/androidMain/kotlin/com/sgmobile/earthquake/feature/earthquake/map/presentation/EarthquakeMapViewModel.kt`: lifecycle adapter.
- `feature/earthquake-ui/src/androidMain/kotlin/com/sgmobile/earthquake/feature/earthquake/navigation/EarthquakeRoutes.kt`: public detail-route factory.
- Existing earthquake UI `detail/presentation/components/MapNightStyle.kt`, `detail/presentation/extensions/MagnitudeThreshold.kt`, and `overview/presentation/components/EarthquakeTimestamp.kt`: narrowly expose reusable style, color, and timestamp helpers.
- `feature/map/build.gradle.kts`: existing-version module/library dependencies and preview tooling.
- `feature/map/src/androidMain/kotlin/com/sgmobile/earthquake/feature/map/presentation/MapScreen.kt`: route collection, edge-to-edge layout, overlay measurement, and detail navigation.
- Same map presentation directory, `components/EarthquakeOverviewMap.kt`: native map/camera; `components/EarthquakeMapPins.kt`: cached marker artwork; `components/EarthquakeMapSummary.kt`: selected-event card; `components/EarthquakeMapStatus.kt`: loading/empty overlays and previews.
- `core/resource/src/androidMain/composeResources/values/strings.xml`: localized map copy and semantic labels.

## Task 1: Publish a shared snapshot and implement common selection

**Files:** Create the four common map presentation files and `EarthquakeOverviewControllerFactory.kt` listed above; modify `EarthquakeOverviewController.kt`. Do not change repository contracts, tests, or existing list-item constructors.

**Interfaces:**
- `EarthquakeMapBounds(minLatitude: Double, minLongitude: Double, maxLatitude: Double, maxLongitude: Double)` and `EarthquakeMapCountry(code: String, name: String, bounds: EarthquakeMapBounds)`.
- `EarthquakeMapPin(earthquake: EarthquakeListItem, latitude: Double, longitude: Double)`; the existing list item supplies ID, place, magnitude, tier, date, and timestamp.
- `EarthquakeMapSnapshot(country: EarthquakeMapCountry?, selectedMagnitude: MagnitudeThreshold, pins: List<EarthquakeMapPin>, isLoading: Boolean)` with `INITIAL` using null country, TWO_PLUS, empty pins, and false loading.
- `EarthquakeMapState(snapshot: EarthquakeMapSnapshot, selectedEarthquakeId: String?)` with derived `selectedEarthquake: EarthquakeMapPin?` and `INITIAL`.
- Internal `@Single EarthquakeMapSnapshotStore`: internal `state: StateFlow<EarthquakeMapSnapshot>` and `publish(snapshot: EarthquakeMapSnapshot): Unit`.
- Public `@Factory EarthquakeMapController internal constructor(store: EarthquakeMapSnapshotStore)`: internal `state: StateFlow<EarthquakeMapState>`, public `currentState`, `observe(observer: (EarthquakeMapState) -> Unit): Observation`, `selectEarthquake(id: String): Unit`, `dismissSelection(): Unit`, and `close(): Unit`.
- Internal mapping functions `List<Earthquake>.toMapPins(): List<EarthquakeMapPin>` and `Country.toMapCountry(): EarthquakeMapCountry`.
- Internal annotated factory `provideEarthquakeOverviewController(refreshUsgsEarthquakesUseCase: RefreshUsgsEarthquakesUseCase, loadNextUsgsEarthquakesUseCase: LoadNextUsgsEarthquakesUseCase, getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase, getIsEndReachedFlowUseCase: GetIsEndReachedFlowUseCase, getCountriesUseCase: GetCountriesUseCase, mapSnapshotStore: EarthquakeMapSnapshotStore): EarthquakeOverviewController`.

- [x] **Step 1: Establish existing-controller baseline.** Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :feature:earthquake:testAndroidHostTest --console=plain`. Expected: BUILD SUCCESSFUL. Record pre-existing failures before editing.
- [x] **Step 2: Create models, mapping, and store.** Keep model types public only where the Android UI consumes them. Map display fields exactly as current list mapping does. Omit coordinates unless finite, latitude in -90..90, and longitude in -180..180; retain valid zero coordinates. Store publication replaces one immutable snapshot atomically.
- [x] **Step 3: Wire overview publication explicitly.** Append `private val mapSnapshotStore: EarthquakeMapSnapshotStore = EarthquakeMapSnapshotStore()` to the internal overview constructor. Remove its class-level `@Factory`; add the internal top-level `@Factory provideEarthquakeOverviewController` defined above in the new factory file. Pass every dependency explicitly so production uses the singleton while existing direct calls use an isolated default. Keep the existing component scan.
- [x] **Step 4: Publish coherent list/filter/loading transitions.** On every list repository emission, publish pins from the same current dataset. Capture country/magnitude per request; clear pins when filters change or refresh resets the list; retain them during pagination. Serialize repository mutations, cancel and join superseded requests before a replacement refresh, and prevent superseded `finally` blocks from clearing the newer loading state. Suppress prior-context pin publication during reset; publish the latest repository value for the active context when reset completes. Keep request coordination within the overview controller, preserve completion callbacks, and reset any stale snapshot before initial Global initialization.
- [x] **Step 5: Implement the common map controller.** Observe the store, retain selection only if the ID remains in the current filter context, and ignore unknown IDs. Match existing controller coroutine/Observation cleanup conventions. No refresh or pagination dependency is permitted here.
- [x] **Step 6: Verify and review.** Repeat Step 1 and run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :feature:earthquake:compileAndroidMain --console=plain`. Expected: BUILD SUCCESSFUL. Inspect reset/cancellation paths for Review Focus 1 and 4; device confirmation follows in Task 5. Confirm test files and repository interfaces are unchanged. Commit only these task files as `feat: share loaded earthquake map state`.

## Task 2: Add the Android adapter and reusable UI bridges

**Files:** Create `EarthquakeMapViewModel.kt`; modify `feature/map/build.gradle.kts`, `EarthquakeRoutes.kt`, `MapNightStyle.kt`, `MagnitudeThreshold.kt`, and the existing `EarthquakeTimestamp.kt` UI helper file.

**Interfaces:**
- `@KoinViewModel class EarthquakeMapViewModel(controller: EarthquakeMapController) : ViewModel`: public `uiState: StateFlow<EarthquakeMapState>`, `selectEarthquake(id: String): Unit`, and `dismissSelection(): Unit`; `onCleared` closes only its controller.
- Public `fun earthquakeDetailRoute(id: String): NavKey` returns the existing internal `EarthquakeRoutes.Detail(id)`.
- Public `fun earthquakeMapNightStyleJson(): String` returns existing `MAP_NIGHT_STYLE_JSON`.
- Make the existing `@Composable fun MagnitudeThreshold.toTierColor(): Color` public without changing its implementation.
- Public `@Composable fun EarthquakeTimestampLabel(value: EarthquakeTimestamp)` delegates to existing timestamp formatting/content helpers, leaving those internals private to the module.

- [x] **Step 1: Add the thin ViewModel.** Place it under the existing earthquake feature scan/package so the already-applied Koin compiler registers it. Delegate both actions and cleanup; add no selection or networking logic.
- [x] **Step 2: Add only map dependencies needed by this design.** Add `projects.feature.earthquake`, `projects.feature.earthquakeUi`, `libs.kmp.maps.compose`, `libs.androidx.lifecycle.runtimeCompose`, `libs.koin.compose`, and `libs.koin.compose.viewmodel` under map androidMain. Match minSdk 24 from the catalog and existing earthquake UI. Add `androidRuntimeClasspath(libs.compose.uiTooling)` for overlay previews. Do not add a map Koin compiler plugin because new annotated definitions live in `feature:earthquake`.
- [x] **Step 3: Add the route/style/timestamp factories and expose tier color.** Keep the detail route internal and reuse its provider/serializer/bottom-bar evaluator. These bridges preserve current Detail rendering and timestamp locale/12-hour behavior.
- [x] **Step 4: Verify compilation.** Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :feature:earthquake:compileAndroidMain :feature:earthquake-ui:compileAndroidMain :feature:map:compileAndroidMain --console=plain`. Expected: BUILD SUCCESSFUL, without new missing-Koin-definition errors. Commit these files as `feat: connect map to earthquake presentation and details`.

## Task 3: Render pins and frame the selected country

**Files:** Create `components/EarthquakeOverviewMap.kt` and `components/EarthquakeMapPins.kt` in map presentation; add camera/pin-control strings to resource `strings.xml`.

**Interfaces:**
- `@Composable internal fun EarthquakeOverviewMap(state: EarthquakeMapState, contentPadding: PaddingValues, onEarthquakeSelected: (String) -> Unit, onSelectionDismissed: () -> Unit, modifier: Modifier = Modifier)`.
- `@Composable @GoogleMapComposable internal fun EarthquakeMapPins(pins: List<EarthquakeMapPin>, selectedEarthquakeId: String?, onEarthquakeSelected: (String) -> Unit)`.
- Internal `fun countryCameraUpdate(country: EarthquakeMapCountry, paddingPx: Int): CameraUpdate`; Global returns target (0.0, 0.0), zoom 1f, zero bearing/tilt. Other countries use `LatLngBounds` and `newLatLngBounds`, with 24.dp converted to pixel padding.

- [x] **Step 1: Render stable selectable pins.** Use `key(pin.earthquake.id)` and `rememberUpdatedMarkerState`. Build one cached Compose bitmap per tier/selected combination keyed by resolved color and density, outside the per-pin loop. Use the existing detail marker's circular visual, add a distinct selected ring/size, increase selected zIndex, and consume marker clicks so the native info window does not compete with the summary.
- [x] **Step 2: Implement saved camera and readiness handling.** Use the pinned library's saveable `rememberCameraPositionState`, initialized to the world view. Save `lastFramedCountryCode` with `rememberSaveable`. Fit only when map-loaded, measured width/height are nonzero, and the country differs from the last-framed value. Do not key the fit effect by pin count, magnitude, selected ID, summary height, or loading state. Clamp camera-bound latitudes to -85.05112878..85.05112878. If bounds are nonfinite, latitude extent is zero/inverted, or longitude extent is zero or spans 360 degrees, use the world camera. Use explicit Global handling instead of a -180/+180 span fit.
- [x] **Step 3: Apply native map settings and recenter control.** Reuse dark styling, disable toolbar/location/zoom controls, and add no location permission. Pass measured content padding to GoogleMap. Add a 48.dp floating recenter control above bottom padding; respect motion duration scale with a 400 ms animation or an immediate move. Recenter only after readiness and preserve selection. Wire map-space taps to dismissal.
- [x] **Step 4: Add localized strings.** Add `center_map_on_country = "Center map on selected country"`, `map_earthquake_pin = "%1$s. Magnitude %2$s."`, and `map_selected_earthquake_pin = "Selected. %1$s. Magnitude %2$s."`. Resolve them through Res for control/marker content descriptions so the selected pin has an accessible selected label.
- [x] **Step 5: Verify and review.** Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :feature:map:compileAndroidMain :feature:map:lint --console=plain`. Expected: BUILD SUCCESSFUL with no new lint issues. Review Global, layout readiness, marker caching, and effect keys against Review Focus 2, 3, and 5; full device checks follow once integrated. Commit these files as `feat: render country-focused earthquake map pins`.

## Task 4: Integrate the edge-to-edge screen and summary overlays

**Files:** Modify `MapScreen.kt` and resource `strings.xml`; create `components/EarthquakeMapSummary.kt` and `components/EarthquakeMapStatus.kt`.

**Interfaces:**
- `@Composable internal fun MapScreen(viewModel: EarthquakeMapViewModel = koinViewModel())`.
- `@Composable internal fun MapContent(state: EarthquakeMapState, safePadding: PaddingValues, onEarthquakeSelected: (String) -> Unit, onSelectionDismissed: () -> Unit, onViewDetails: (String) -> Unit, modifier: Modifier = Modifier)`.
- `@Composable internal fun EarthquakeMapSummary(earthquake: EarthquakeListItem, onDismiss: () -> Unit, onViewDetails: () -> Unit, modifier: Modifier = Modifier)`.
- `@Composable internal fun EarthquakeMapStatus(snapshot: EarthquakeMapSnapshot, modifier: Modifier = Modifier)`.

- [x] **Step 1: Implement the summary card.** Show magnitude/place plus `EarthquakeTimestampLabel`; add dismiss and View details actions. Use theme colors/type and a 16.dp outer margin above bottom navigation. Limit card height to 45% of safe available height; keep actions reachable using scrollable text content at large font scale. Expose the full place in semantics if visually truncated.
- [x] **Step 2: Implement loading/empty overlays.** Use `SGLoading` for empty loading and a small surface-backed loading area above bottom navigation for pagination. For null country show `map_load_earthquakes = "Load earthquakes on the Earthquakes screen to see them on the map."`; for empty finished results show `map_no_loaded_earthquakes = "No earthquakes loaded for this view."` and `map_no_loaded_earthquakes_description = "Change filters or refresh on the Earthquakes screen."`. Do not mislabel empty loading as finished or add retry actions.
- [x] **Step 3: Add action text and summaries.** Add `view_earthquake_details = "View details"` and `dismiss_earthquake_summary = "Dismiss earthquake summary"` through Res. Remove `map_placeholder` only if no references remain. Display timestamp fallback using the existing helper.
- [x] **Step 4: Replace the placeholder route.** Collect ViewModel state with `collectAsStateWithLifecycle`. Set system-bar appearance following Detail, including resume handling. Use a full-size map without top/bottom layout padding; apply lateral safe insets as Detail does. Inset overlays with status-bar and `LocalNavScaffoldPadding` bottom values. Measure the summary and pass its occupied height plus navigation/margins to map content padding so attribution and recentering stay visible. Navigate with `LocalNavigator.current.navigate(earthquakeDetailRoute(id))`; Map composition, saved camera, and last-framed country retain state on Back.
- [x] **Step 5: Add focused overlay previews.** Use existing `SGPreview`/`PreviewThemes` for selected-summary, empty, initial-without-data, and loading overlays; include a fontScale 1.5 preview with a long place. Do not preview the native map or create test infrastructure.
- [x] **Step 6: Verify integration.** Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :feature:map:compileAndroidMain :feature:map:lint :feature:earthquake-ui:lint :androidApp:assembleDevDebug --console=plain`. Expected: BUILD SUCCESSFUL, with no new lint issues. Inspect preview layouts and verify the detail key remains registered/hides bottom navigation. Commit these files as `feat: add edge-to-edge map summary interactions`.
- [x] **Step 7: Confirm on the Android emulator.** Install DevDebug and verify pins, country focus, summary/dismissal, detail/Back, and edge-to-edge insets. Record results and repeat affected checks after fixes before completing this task.

## Task 5: Verify the complete feature and review the task delta

**Files:** No new application/test files; fix only defects found in the preceding task files. Record verification in the handoff and update this plan's checkboxes with evidence.

Run Steps 3–4 on the Android emulator and record confirmation; emulator checks are required in addition to previews, builds, and lint.

- [x] **Step 1: Run shared regression checks.** Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew allTests --console=plain`. Expected: BUILD SUCCESSFUL. The earlier focused tests need not be repeated unless implementation changed or a failure needs diagnosis.
- [x] **Step 2: Verify shared iOS resolution.** Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64 --console=plain`. Expected: BUILD SUCCESSFUL. Then run `JAVA_HOME=$(/usr/libexec/java_home -v 17) xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' ARCHS=arm64 ONLY_ACTIVE_ARCH=YES CODE_SIGNING_ALLOWED=NO build`. Expected: BUILD SUCCEEDED. Do not edit generated packages/frameworks; report unavailable Xcode/SDK/package prerequisites explicitly.
- [x] **Step 3: Run device acceptance from the spec.** Check country/magnitude dataset parity, loaded-page additions, rapid filter changes during loading, Global, recentering, pin/card dismissal, detail/Back/tab camera retention, failed/empty/restored states, and lack of map-owned requests. Inspect requests through existing logs or network inspection, without adding production debug code.
- [x] **Step 4: Check visual/accessibility conditions.** Verify dark/light, landscape, fontScale 1.5, long place, small viewport, TalkBack pin/action labels, 48.dp controls, and Google attribution clear of card/navigation. If no valid maps key or device is available, identify these checks as unverified, not passed.
- [x] **Step 5: Review the baseline delta.** Run `git diff --check` and inspect only task commits/current edits for generated files, secrets, test changes, unrelated formatting, and dependency upgrades. Confirm unchanged iOS UI and record any shared-platform verification limitations.
- [x] **Step 6: Complete the selected execution workflow's review and handoff.** Request its required independent review, resolve actionable feedback within this scope, and rerun only affected checks. Summarize behavior and exact check results; finish with Code changes grouped by file and actual added/removed lines from the implementation baseline. Follow the branch-finishing skill without merging/pushing unless authorized.

## Planning Evidence and Execution Gate

Planning used JDK 17 and successfully ran `./gradlew :feature:map:tasks --all :feature:earthquake:tasks --all :shared:tasks --all --console=plain`. This discovered `compileAndroidMain`, module `lint`, `testAndroidHostTest`, and `linkDebugFrameworkIosSimulatorArm64`; it did not compile or test application code.

Context7 resolution failed with an API-key error. The [official maps README](https://github.com/yankeppey/kmp-maps-compose) and locally cached 0.6.1 source confirm marker callbacks, content padding, and the camera saver. [Koin compiler transformation documentation](https://github.com/InsertKoinIO/koin-compiler-plugin/blob/main/docs/TRANSFORMATIONS.md) describes default-value skipping; this plan uses explicit factory wiring instead of changing global compiler options or assuming defaults are injected. Verify against the pinned compiler during Task 1; do not upgrade it.

All tasks form one sequential feature: Tasks 2–4 depend on Task 1's types, and final verification depends on integration. Recommend Native execution for this plan, with one independent final review, because repeated task-level agent contexts would revisit tightly coupled state/UI interfaces.

Implementation starts after the user reviews this written plan and selects Native or Subagent-driven execution.

## Implementation verification

- [x] Existing Android host tests and allTests passed.
- [x] Android map/earthquake UI lint and DevDebug assembly passed.
- [x] Shared iOS simulator framework and native arm64 simulator build passed.
- [x] Emulator confirmed country pins, pagination, summary/details/Back, camera retention, empty/offline states, accessibility labels, dark mode, and large-text landscape controls.
- [x] Independent final review; the refresh-completion race was fixed and existing checks passed afterward.

The initial baseline overview-controller test run hung before source edits; later focused and full runs passed. The generic iOS build requested unsupported ios_x64; the arm64 simulator command passed. New test files were not added. Malformed-coordinate injection and spoken TalkBack output were not exercised.

The final review found a refresh callback that could be skipped when a queued job was canceled before starting. Completion now observes the dispatch job lifecycle while awaiting the request job. Existing completion tests, allTests, Android lint/build, and the shared iOS framework passed after the correction.
