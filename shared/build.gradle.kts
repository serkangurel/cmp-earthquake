@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
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
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(libs.calf.ui)
        }
    }

    swiftPMDependencies {
        iosMinimumDeploymentTarget.set("16.0")

        swiftPackage(
            url = url("https://github.com/googlemaps/ios-maps-sdk"),
            version = exact("10.8.0"),
            products = listOf(
                product(
                    name = "GoogleMaps",
                    platforms = setOf(iOS())
                )
            )
        )
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.resource)
            implementation(projects.core.ui)
            implementation(projects.core.network)
            implementation(projects.core.navigation)
            implementation(projects.feature.earthquake)
            implementation(projects.feature.map)
            implementation(projects.feature.settings)

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.components.uiToolingPreview)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)
            api(libs.calf.ui)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.annotations)

            implementation(libs.napier)
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
        }
        iosMain.dependencies {
            implementation(libs.kmp.maps.compose)
        }
    }
}

compose.resources {
    publicResClass = false
    generateResClass = never
}

buildkonfig {
    packageName = "com.sgmobile.earthquake"
    defaultConfigs {
        buildConfigField(STRING, "MAPS_API_KEY", localProperties.getProperty("MAPS_API_KEY") ?: "")
    }
}
