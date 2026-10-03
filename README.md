# CMP Earthquake

CMP Earthquake is a Kotlin Multiplatform application that shares data, domain, and presentation
logic while keeping each platform's UI native:

- Android renders the existing Compose UI and uses Navigation 3.
- iOS renders a native SwiftUI hierarchy and embeds the Google Maps iOS SDK directly.
- Kotlin shared code owns Ktor networking, serialization, repositories, use cases, filtering,
  pagination, display-ready models, and overview/detail state controllers.
- The Ktor server remains an independent JVM application.

## Module boundaries

- `shared` builds the small `Shared` Apple framework entry point. It starts the shared Koin graph
  and exposes factories for the public earthquake screen controllers.
- `core:network` contains platform-independent HTTP client configuration.
- `core:domain` contains earthquake models, repository contracts, and use cases shared by every tab.
- `core:data` contains the USGS API, country data source, and repository implementations.
- `core:presentation` contains display models and the earthquake feed shared between tabs.
- Each tab is one feature module: `feature:earthquake` (list and detail), `feature:map`, and
  `feature:settings`. Shared controllers and state live in `commonMain`; Android Compose screens,
  view models, and navigation live in `androidMain`.
- `androidApp`, `core:navigation`, `core:resource`, and `core:ui` contain Android-only Compose UI
  and navigation.
- `iosApp` contains the SwiftUI root, views, thin observable adapters, native localizations, and
  Google Maps integration.
- `server` contains the standalone JVM Ktor server.

The country bounds JSON is owned by `core:data` as shared data. Android packages it as an
asset and iOS embeds the same source file as a bundle resource. Display strings and visual assets
are platform-native resources.

## Build

Use JDK 17 and the checked-in Gradle wrapper:

```sh
./gradlew :feature:earthquake:testAndroidHostTest
./gradlew allTests
./gradlew :androidApp:assembleDevDebug
./gradlew lint
./gradlew :server:build
```

Build iOS with the `iosApp` scheme in Xcode. Its build phase links the simulator or device variant
of the `Shared` framework before Swift compilation.

`MAPS_API_KEY` remains optional at compile time and is read from untracked `local.properties` for
both Android and iOS. A real key is required only to render Google Maps at runtime.
