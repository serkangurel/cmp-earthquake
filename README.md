# CMP Earthquake

CMP Earthquake is a Kotlin Multiplatform application that shares data, domain, and presentation
logic while keeping each platform's UI native:

- Android renders the existing Compose UI and uses Navigation 3.
- iOS renders a native SwiftUI hierarchy and embeds the Google Maps iOS SDK directly.
- Kotlin shared code owns Ktor networking, serialization, repositories, use cases, filtering,
  pagination, display-ready models, and overview/detail state controllers.
- The Ktor server remains an independent JVM application.

## Module boundaries

Each tab owns an `api` and an `impl` module:

- `feature:earthquake:api` exposes list/detail state and controller contracts, country search,
  neutral earthquake dataset models/interfaces, and portable destination models.
- `feature:earthquake:impl` owns repositories, data sources, use cases, the shared dataset,
  list/detail controller implementations, Android view models, Compose UI, navigation, and DI.
- `feature:map:api` exposes map presentation models, its controller interface, and portable destinations.
- `feature:map:impl` owns the map controller, dataset-to-map mapping, Android view model/UI, and DI.
  It depends on `feature:earthquake:api`, never earthquake implementation or UI.
- `feature:settings:api` exposes preference models, the read-only `SettingsSource`, the settings
  controller contract, and portable destinations. `feature:settings:impl` owns DataStore persistence,
  the settings controller, Android UI, and DI. It depends on `feature:earthquake:api` only.
- `shared` assembles shared implementation DI and exposes controller factories through the single
  `Shared` Apple framework. It exports navigation and all feature APIs, not implementation modules.
- `androidApp` assembles the Android implementation/navigation modules.
- `core:network` owns HTTP client configuration. `core:navigation:api` exposes portable route/state
  contracts; `core:navigation:impl` owns shared navigation logic and the Android host/adapters.
  `core:resource` and `core:ui` own reusable Android resources and UI (including timestamp rendering).
- `iosApp` owns native SwiftUI views, thin observable adapters, localizations, and Google Maps.
- `server` remains an independent JVM Ktor application.

API modules contain models and interfaces; services, controllers, mapping, DI, and Compose
components belong in implementations. Cross-feature dependencies target APIs only. Portable
logic and destination models live in `commonMain`; Android UI lives in `androidMain`.

Earthquake implementation publishes the existing loaded dataset and active filters through
`EarthquakeDatasetSource`. Map maps that dataset to its own pins and selection state. The existing
list-controller loading lifecycle is preserved: map displays the dataset loaded by overview.

Both platforms consume the same navigator and feature-owned destinations. Shared navigation owns
selected tab, independent tab stacks, push/back decisions, and tab-bar visibility. Shared composition
configures the three roots. Android renders that state through Navigation 3 and persists it through
`rememberSaveable`; SwiftUI binds `TabView` selection and each `NavigationStack` path to a thin
observable adapter. Native back gestures update shared state; the detail back button calls the
shared navigator. iOS retains navigation during the current app session; durable iOS navigation
restoration is not configured. Country-picker sheets remain native presentations.

Settings are stored with DataStore Preferences in shared code. Each platform passes an app-private
directory to `startSharedApplication`. The overview applies the stored default magnitude, country,
and time range on launch and again whenever one changes. The theme is either Light or Dark; until the
user picks one, both platforms follow the device appearance.

The country bounds JSON is owned by `feature:earthquake:impl`. Android packages it as an asset
and iOS embeds the same source file as a bundle resource. Display resources remain platform-native.

## iOS source layout

Under `iosApp/iosApp`, `App` contains the application entry point and tab composition.
`Core/Navigation` contains the shared-navigation adapter and native back-gesture integration;
`Core/UI` contains reusable components, map marker rendering, timestamp rendering, and theme colors.
`Features/Earthquake` groups overview and detail views with their observable adapters;
`Features/Map` owns the map tab, and `Features/Settings` owns the settings screens and their adapter.
`Resources` contains localizations, asset catalogs, and preview assets. Xcode groups mirror these folders.
Build configuration and `Info.plist` live in `iosApp/Configuration`; the linked Kotlin package
and Maestro flows remain in their existing dedicated directories.

## Build

Use JDK 17 and the checked-in Gradle wrapper:

```sh
./gradlew :feature:earthquake:impl:testAndroidHostTest
./gradlew allTests
./gradlew :androidApp:assembleDevDebug
./gradlew lint
./gradlew :server:build
```

Build iOS with the `iosApp` scheme in Xcode. Its build phase links the simulator or device variant
of the `Shared` framework before Swift compilation.

`MAPS_API_KEY` remains optional at compile time and is read from untracked `local.properties` for
both Android and iOS. A real key is required only to render Google Maps at runtime.
