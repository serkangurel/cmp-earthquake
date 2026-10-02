# Android MapScreen design

## Intent and approved experience

The Android Map tab shows the earthquakes already loaded on the Earthquakes screen as pins. It focuses on that screen's selected country and follows its magnitude filter and loaded pages. Selecting a pin shows a compact summary with an action to open the existing earthquake detail screen.

The map fills the screen without a top app bar or reserved header space, following the Detail screen's edge-to-edge treatment. The existing bottom navigation remains available on the Map overview. Floating controls and the summary card respect system insets and bottom navigation.

## Scope

- Reuse the currently loaded dataset; entering Map does not fetch, refresh, or paginate.
- Mirror country, magnitude, refresh, and pagination changes made on Earthquakes.
- Frame the selected country, display selectable pins, and provide country recentering.
- Show a selected-earthquake summary and open the existing detail destination.
- Add localized Android text, accessibility labels, and previews for map overlays.
- Preserve the native iOS UI and existing list/detail behavior.

Independent map filters, location permission, user-location controls, viewport searches, automatic loading of every page, clustering, and pulse animations are outside this change. Loaded pages are the complete map dataset for this iteration.

## Existing constraints

`EarthquakeRepositoryImpl` is a singleton containing the loaded domain earthquakes, including coordinates. `EarthquakeOverviewController` owns country selection, magnitude selection, loading, and requests. Its list presentation items omit coordinates, and its selected country bounds are private. Instantiating another overview controller for Map would initiate a refresh and could replace the shared dataset.

`feature:map` currently contains a placeholder screen and its top-level navigation destination. The detail map already uses `eu.buney.maps:kmp-maps-compose:0.6.1`. `EarthquakeRoutes.Detail` is internal to `feature:earthquake-ui`; its provider registers the detail serializer and hides bottom navigation on details.

## Architecture and data flow

Use a shared presentation snapshot rather than sharing an overview controller or issuing separate map requests.

Add a small annotation-registered singleton snapshot store in `feature:earthquake` common presentation code. It exposes immutable state containing:

- Selected country code/name and presentation bounds, or no country before initialization.
- Selected magnitude threshold, identifying the active filter context.
- Pins with earthquake ID, place, formatted magnitude, magnitude tier, timestamp, latitude, and longitude.
- Loading status covering initial loading, refresh, and pagination.

Domain and transport models remain internal. The map consumes presentation models, including a presentation bounds type rather than exposing domain `CountryBounds`. Map pins use the same display formatting as list items. Coordinates must be finite and within latitude/longitude ranges; invalid coordinates are omitted from pins without changing the list.

The existing overview controller publishes snapshots from the same repository emissions that update the list, preserving the coordinates while mapping display fields. It also publishes filter and loading transitions. The store has no networking responsibilities and does not own the overview controller's lifetime.

Snapshots publish country context and pins together. On country or magnitude changes, clear the preceding pins and selected map item before publishing results for the new context. Refresh also follows the list's dataset reset. Obsolete requests must not repopulate the map under a newer country's label; coordinate request transitions narrowly within the existing controller if required. Pagination retains existing pins and adds the loaded page.

Keep repository/use-case contracts and existing direct controller construction compatible. A new snapshot-store dependency should have an isolated default for existing direct callers while production Koin supplies the singleton. Existing tests must not require edits or additional infrastructure.

A small common presentation map controller observes the store and owns the selected earthquake ID and selection transitions. It clears selection when the ID disappears or the filter context changes. An Android lifecycle-scoped map ViewModel delegates to this controller. The route collects state with lifecycle awareness and wires navigation; content composables receive plain state and callbacks. Closing the Map ViewModel closes only its map controller, not the Earthquakes controller or shared snapshot.

The store is an in-memory view of the loaded list, not persisted data. After process recreation it starts empty and follows the list's normal initialization. A restored Map tab must handle this empty state without starting its own request. Recreating the overview controller must not briefly pair a previous dataset with its initial Global context.

## Camera behavior

- On first map entry with a country available, fit that country's existing bounds with padding for visible overlays.
- When the country changes, clear selection and frame the new country once the map is ready.
- Global uses an explicit world camera. Do not construct a bounds fit spanning both -180 and +180 longitude, and do not fit polar coordinates through Mercator bounds.
- Magnitude changes, refreshes, appended pages, and selecting pins do not automatically reset the camera.
- The recenter control returns to the selected country's view and does not fetch data.
- Preserve the camera when opening/closing the summary, switching tabs, and returning from details. Save the camera and last-framed country with the Map navigation entry so recreating its composition does not repeat the initial fit.
- Coordinate camera updates with map readiness and measured content padding. Avoid fitting unmeasured bounds or launching repeated fits from unrelated state emissions.

## Map and interaction layout

Use a full-size map behind the status bar and existing bottom navigation, without an app bar, country header, or reserved top strip. Follow the Detail screen's system-bar appearance and dark map styling. Keep map attribution visible above bottom navigation and the summary card using map content padding.

Pins use magnitude-tier colors matching the existing earthquake UI. Provide a visible selected treatment that does not depend only on color. Cache reusable marker artwork by magnitude tier/selection rather than creating a unique bitmap on every state update. Stable earthquake IDs identify pins.

Tapping a pin selects it and replaces any previous selection. A compact bottom card shows place, magnitude, date/time, a dismiss action, and a localized **View details** action. Position it above bottom navigation. Cap its height on small screens and accommodate large text so actions remain reachable. Selecting a pin leaves the current camera unchanged; account for the card in map content padding.

Tapping unoccupied map space or the card's dismiss action clears selection. The country-recenter control remains reachable when the card is open. All controls have usable touch targets and semantic labels; pin descriptions include place and magnitude. Add focused light/dark and large-text previews of summary, empty, and loading overlays; the native map itself is not previewable.

## Navigation and module boundaries

Expose a narrowly scoped public Android detail-route factory from the earthquake UI navigation package while keeping the existing route implementation internal. Add the required `feature:earthquake-ui` dependency to `feature:map` to use this factory; there is no reverse dependency. The map also depends on `feature:earthquake` for shared presentation state.

The Map route calls the existing `Navigator` with the returned earthquake detail key. This pushes details on the Map back stack, reuses the registered screen/serializer, and preserves the existing detail behavior of hiding bottom navigation. Back returns to Map. No duplicate detail destination or changes to generic navigation infrastructure are needed.

Use the existing lifecycle, Koin, Compose, and maps dependencies at their pinned versions. Add only the module dependencies and compiler configuration needed for the new Android map ViewModel. Reuse existing map style/magnitude conventions through small shared UI helpers where necessary, avoiding a broad refactor.

## Loading, empty, and failure handling

While the list is refreshing or initially loading with no pins, show the shared loading component over the map. Pagination loading keeps the existing pins visible and uses unobtrusive loading feedback.

When loading has finished with no valid pins, show localized copy explaining that no earthquakes are loaded for this view and that users can change filters or refresh on Earthquakes. Keep the selected country's map visible. Before a snapshot exists, show the world map and a message to load earthquakes on Earthquakes.

The current list does not expose a distinct error state. This change does not add map-owned retries or a new global error system. If a request fails, follow the loaded list's resulting data/loading state; do not invent a successful result or leave a permanent loading overlay. Map SDK authorization or connectivity problems remain SDK/environment prerequisites and must be reported during verification.

## Verification and acceptance

Do not add or modify tests or test infrastructure. Run existing checks with the checked-in Gradle wrapper and JDK 17. Discover the precise module compile/lint tasks when preparing the implementation plan; run the narrow map and earthquake checks, `:feature:earthquake:testAndroidHostTest`, and `:androidApp:assembleDevDebug`. Expand to `allTests` for the shared presentation change when practical. Compile/link the shared iOS simulator framework and build the native iOS scheme where available to catch shared API/DI regressions. Record exact commands, results, and unavailable prerequisites.

Manual Android acceptance checks, when a device/emulator and a valid local maps key are available:

1. Select a country and magnitude on Earthquakes, then open Map: the valid-coordinate pins match the loaded list and the camera frames that country.
2. Load another page on Earthquakes: Map gains those pins without resetting a previously explored camera.
3. Change country: old pins and selection disappear, the new country is framed, and only its new loaded results appear.
4. Change magnitude or refresh: Map follows list replacement and loading without an automatic country-camera reset.
5. Select Global: the world view works without invalid bounds or polar camera errors.
6. Select/dismiss a pin: the correct summary appears; selecting another replaces it; map-space taps dismiss it.
7. Open details and go Back: the existing detail screen opens and returns to Map with its camera preserved.
8. Check empty and restored-without-data states, invalid-coordinate handling, and request failure without stuck loading.
9. Check dark mode, large text, landscape, edge-to-edge insets, bottom navigation, visible attribution, and accessible controls.
10. Confirm tab changes and camera interactions do not generate additional earthquake requests.

## Documentation and next gate

The [official maps library documentation](https://github.com/yankeppey/kmp-maps-compose) confirms selectable markers, Compose marker artwork, and camera state/bounds support. Context7 was attempted first but returned an API-key error; use official documentation/source for any remaining API questions if it remains unavailable.

This document records the approved design. Implementation begins only after the user reviews this written specification, then reviews an implementation plan and chooses its execution method, as required by the invoked brainstorming skill.
