@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.buildkonfig)
}

val localProperties = gradleLocalProperties(rootDir, providers)

kotlin {
    android {
        namespace = "com.sgmobile.earthquake.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(projects.core.navigation.api)
            export(projects.feature.settings.api)
            export(projects.feature.earthquake.api)
            export(projects.feature.map.api)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.network)
            api(projects.core.navigation.api)
            implementation(projects.core.navigation.impl)
            api(projects.feature.settings.api)
            api(projects.feature.earthquake.api)
            api(projects.feature.map.api)
            implementation(projects.feature.earthquake.impl)
            implementation(projects.feature.map.impl)
            implementation(libs.koin.core)
            api(libs.koin.annotations)
            implementation(libs.napier)
        }
    }
}

buildkonfig {
    packageName = "com.sgmobile.earthquake"
    defaultConfigs {
        buildConfigField(STRING, "MAPS_API_KEY", localProperties.getProperty("MAPS_API_KEY") ?: "")
    }
}
