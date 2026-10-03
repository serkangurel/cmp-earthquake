# AGENTS.md

## Scope

These instructions apply to the entire repository. If a nested directory later contains its own
`AGENTS.md`, the more specific file takes precedence for work in that subtree.

## Project overview

This is a Kotlin Multiplatform earthquake application with an Android Compose UI, a native SwiftUI
iOS UI, shared application logic, and a small Ktor server. The project uses Gradle Kotlin DSL, a
centralized version catalog, Koin, Ktor, Kotlin serialization, Android Navigation 3, and Android
Compose resources.

The main modules are:

- `androidApp`: Android application entry point, product flavors, manifest, and Android packaging.
- `iosApp`: native SwiftUI application, observable adapters, localizations, and Google Maps SDK UI.
- `shared`: UI-free Apple framework entry point and dependency-injection bootstrap.
- `core:network`: shared HTTP client configuration and network DI.
- `core:domain`: shared earthquake models, repository contracts, and use cases.
- `core:data`: shared USGS API, country data source, and repository implementations.
- `core:presentation`: shared display models and the earthquake feed shared between tabs.
- `core:navigation`: Android-only Compose navigation state, navigator, host, and contracts.
- `core:resource`: Android-only Compose resources. Its generated `Res` class is public.
- `core:ui`: reusable Android Compose components, previews, and theming.
- `feature:earthquake`, `feature:map`, `feature:settings`: one module per tab. Shared controllers
  and state live in `commonMain`; Android Compose screens, view models, and navigation live in
  `androidMain`. Feature modules do not depend on each other.
- `server`: JVM Ktor server.

Treat `settings.gradle.kts` as the source of truth for included modules and
`gradle/libs.versions.toml` as the source of truth for dependency and plugin versions.

## Repository workflow

- Inspect the current working tree before editing. Preserve unrelated staged, unstaged, and
  untracked changes; never discard or rewrite user work.
- At the start of every task, establish a working-tree baseline. When reviewing, displaying, or
  summarizing changes, include only the delta introduced by the current task. Exclude pre-existing
  modifications and changes from earlier completed tasks unless the user explicitly asks for the
  complete working-tree diff. If a file was already modified at task start, use the baseline to
  isolate the current task's edits instead of presenting the file's entire Git diff.
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

- Preserve the existing layering: data sources and repository implementations in `core:data`,
  contracts/models/use cases in `core:domain`, and UI state, view models, intents, view objects,
  and composables in each feature's `presentation` package. State or display models shared by
  several tabs belong in `core:presentation`, not in another feature module.
- Shared screen controllers expose immutable presentation state and own platform-independent state
  transitions. Platform view models or observable adapters remain thin and lifecycle-scoped.
- Keep transport/domain models out of Compose and SwiftUI. Map them to presentation models at the
  shared presentation boundary.
- Add shared Koin registrations using the existing annotation-based module/component scan. Android
  navigation providers are registered through their feature modules' `androidMain` code.

### Compose UI and resources

- Keep route-level composables responsible for collecting state and wiring navigation; pass plain
  state and callbacks into reusable/content composables.
- Use lifecycle-aware state collection where available and keep side effects in the appropriate
  Compose effect APIs.
- Reuse components and theme values from `core:ui`; do not duplicate app-wide colors, typography,
  loading UI, app bars, or preview wrappers inside features.
- Put Android display strings and visual assets in `core:resource/src/androidMain/composeResources`
  and access them through the generated `Res` API. Put iOS strings and visual assets in native
  bundle resources. Do not hard-code display text in production UI.
- Add focused previews for meaningful UI states when changing a composable.
- Preserve accessibility: add semantic labels/content descriptions where appropriate and keep
  touch targets usable.

### Navigation, networking, and server code

- Define Android feature destinations in the feature module's `androidMain` navigation package and
  keep route arguments serializable. Routes that other tabs open (such as `EarthquakeDetailRoute`)
  live in `core:navigation`. Android navigates through `Navigator`/composition locals; iOS uses
  native SwiftUI navigation values and containers.
- Reuse `core:network` for shared client configuration. Keep wire models and API mapping inside
  `core:data`.
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

- Do not add new tests, test files, or test infrastructure during or after implementation work unless
  the user explicitly requests them.
- Verify implementation changes with existing tests, build/lint checks, and manual or emulator checks
  as appropriate.
- When the user explicitly requests test changes, prefer deterministic fakes over live network,
  map, clock, database, or service dependencies.
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
5. End every task with a `Code changes` section. For tasks that edit files, group changes by file
   and show the actual added and removed lines in concise `diff` code blocks; a file link or
   summary alone is not sufficient. Include only the task-baseline delta, never the whole file,
   pre-existing working-tree changes, or changes from earlier completed tasks. For tasks without
   file edits, explicitly state that there were no code changes.
