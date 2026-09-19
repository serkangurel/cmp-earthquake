# AGENTS.md

## Scope

These instructions apply to the entire repository. If a nested directory later contains its own
`AGENTS.md`, the more specific file takes precedence for work in that subtree.

## Project overview

This is a Kotlin Multiplatform earthquake application with shared Compose UI for Android and iOS,
plus a small Ktor server. The project uses Gradle Kotlin DSL, a centralized version catalog, Koin,
Ktor, Kotlin serialization, Navigation 3, and Compose Multiplatform resources.

The main modules are:

- `androidApp`: Android application entry point, product flavors, manifest, and Android packaging.
- `iosApp`: SwiftUI/Xcode application entry point and Swift Package integration.
- `shared`: shared application composition, root Compose UI, and dependency-injection bootstrap.
- `core:network`: shared HTTP client configuration and network DI.
- `core:navigation`: shared navigation state, navigator, host, and navigation contracts.
- `core:resource`: shared Compose resources. Its generated `Res` class is public.
- `core:ui`: reusable UI components, previews, and theming.
- `feature:earthquake`: earthquake overview/detail data, domain, presentation, and navigation.
- `feature:map` and `feature:settings`: self-contained feature UI, DI, and navigation.
- `server`: JVM Ktor server.

Treat `settings.gradle.kts` as the source of truth for included modules and
`gradle/libs.versions.toml` as the source of truth for dependency and plugin versions.

## Repository workflow

- Inspect the current working tree before editing. Preserve unrelated staged, unstaged, and
  untracked changes; never discard or rewrite user work.
- Keep changes narrowly scoped. Do not perform drive-by formatting or broad dependency upgrades.
- Use the checked-in Gradle wrapper (`./gradlew`), not a system Gradle installation.
- Use JDK 17 for Gradle and Android builds.
- Do not hand-edit generated output under `build/`, `.gradle/`, `.kotlin/`, or SwiftPM/Xcode derived
  data. Update source configuration and regenerate artifacts with the owning tool when needed.
- Do not commit `local.properties`, signing material, API keys, database credentials, tokens, or
  other secrets. Read runtime secrets from environment variables or local untracked configuration.

## Architecture and implementation conventions

### Kotlin Multiplatform

- Put portable production code in `src/commonMain`; use `androidMain` or `iosMain` only for genuine
  platform integration.
- Put portable tests in `src/commonTest`. Android JVM tests belong in `androidHostTest`, and
  emulator/device tests belong in `androidDeviceTest` where that source set is configured.
- Keep platform APIs out of `commonMain`. Prefer small platform-specific implementations behind a
  common contract when behavior differs by target.
- Keep package names under `com.sgmobile.earthquake` and follow the existing module/package layout.

### Features and state

- Preserve the existing feature layering: data sources and repository implementations in `data`,
  contracts/models/use cases in `domain`, and UI state, view models, intents, view objects, and
  composables in `presentation`.
- View models expose immutable observable state and accept user actions through the feature's
  intent type. Launch asynchronous work in `viewModelScope` and keep mutable flows private.
- Keep transport/domain models out of composables. Map them to presentation models at the
  presentation boundary.
- Add Koin registrations using the existing annotation-based module/component scan. Navigation
  providers are registered through each feature's explicit navigation module and included from
  `shared`.

### Compose UI and resources

- Keep route-level composables responsible for collecting state and wiring navigation; pass plain
  state and callbacks into reusable/content composables.
- Use lifecycle-aware state collection where available and keep side effects in the appropriate
  Compose effect APIs.
- Reuse components and theme values from `core:ui`; do not duplicate app-wide colors, typography,
  loading UI, app bars, or preview wrappers inside features.
- Put user-visible strings and other shared assets in `core/resource/src/commonMain/composeResources`
  and access them through the generated `Res` API. Do not hard-code display text in production UI.
- Add focused previews for meaningful UI states when changing a composable.
- Preserve accessibility: add semantic labels/content descriptions where appropriate and keep
  touch targets usable.

### Navigation, networking, and server code

- Define feature destinations in that feature's navigation package and keep route arguments
  serializable. Navigate through the shared `Navigator`/composition locals rather than introducing
  a second navigation mechanism.
- Reuse `core:network` for shared client configuration. Keep wire models and API mapping inside the
  feature data layer.
- Keep server configuration separate from application logic. Credentials and deployment-specific
  values must come from environment/configuration, never string literals in source.

## Code style

- Follow Kotlin's official style (`kotlin.code.style=official`) with four-space indentation.
- Prefer clear Kotlin names and small, single-purpose functions. Use `internal` unless a declaration
  must cross a module boundary.
- Follow the local file's established import ordering, trailing-comma usage, and expression style.
- Avoid suppressions and opt-ins at broad scope unless the entire file/module genuinely requires
  them. Explain non-obvious workarounds with a short comment focused on why.
- No repository-wide formatter or static-analysis tool beyond Android lint is configured. Do not
  claim ktlint, detekt, or Spotless validation unless one is added to the build.

## Build and verification

Choose the smallest commands that cover the change, then broaden verification for cross-cutting
work. Useful commands from the repository root are:

```sh
# Discover available tasks
./gradlew tasks --all

# Fast Android development build
./gradlew :androidApp:assembleDevDebug

# All multiplatform unit tests (also used by CI)
./gradlew allTests

# Earthquake feature Android/JVM tests
./gradlew :feature:earthquake:testAndroidHostTest

# Android lint
./gradlew lint

# Device tests; requires a running emulator/device
./gradlew connectedAndroidTest

# Run the Ktor server
./gradlew :server:run
```

For a localized module change, prefer its compile/test/lint task before running the repository-wide
equivalent. Android release builds require local signing properties. Map rendering requires a local
`MAPS_API_KEY`. Do not add placeholder secrets to tracked files merely to make a build pass.

For iOS changes, also build the `iosApp` scheme in Xcode (or with `xcodebuild`) on macOS. When Gradle
or Swift package configuration changes, verify that the Kotlin framework/package still resolves
before editing generated package artifacts manually.

## Testing expectations

- Add or update tests for behavior changes and regressions. Prefer deterministic fakes over live
  network, map, clock, database, or service dependencies.
- Match the test to the layer: use-case/repository behavior in `commonTest`, Android-only JVM logic
  in `androidHostTest`, and Compose interaction/rendering in `androidDeviceTest`.
- Use `kotlin.test` for portable tests and follow the existing Given/When/Then organization where it
  improves readability.
- Report exactly which checks ran and whether they passed. If a check cannot run because an emulator,
  Xcode, signing configuration, API key, network service, or another prerequisite is unavailable,
  state that explicitly.

## Documentation lookup

When work depends on current library, framework, SDK, API, CLI, or cloud-service behavior, use
Context7 documentation first: resolve the library ID, then query the relevant concept with the full
question. Use version-specific documentation when the repository pins a version. Do not rely on
memory for current API syntax or migration guidance.

## Definition of done

Before handing off a change:

1. Review the diff for accidental generated files, credentials, unrelated formatting, and debug
   code.
2. Run the narrowest relevant tests/build/lint checks, expanding to `allTests` for cross-module or
   shared behavior when practical.
3. Confirm Android and iOS behavior for shared UI or platform integration changes, or clearly note
   which platform was not verified.
4. Summarize changed behavior, list verification performed, and call out any remaining risk or
   prerequisite.
5. End every task with a `Code changes` section. For tasks that edit files, show a concise diff
   grouped by affected file and include only changes made for that task, excluding unrelated
   working-tree changes. For tasks without file edits, explicitly state that there were no code
   changes.
