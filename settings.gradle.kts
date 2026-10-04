rootProject.name = "cmp-earthquake"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":androidApp")
include(":shared")
include(":server")
include(":core:network")
include(":core:ui")
include(":core:resource")
include(":core:navigation:api")
include(":core:navigation:impl")
include(":feature:earthquake:api")
include(":feature:earthquake:impl")
include(":feature:map:api")
include(":feature:map:impl")
include(":feature:settings:api")
include(":feature:settings:impl")
