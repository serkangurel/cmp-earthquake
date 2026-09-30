plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    android {
        namespace = "com.sgmobile.earthquake.feature.earthquake.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            multidex {
                enable = true
            }
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(projects.feature.earthquake)
            implementation(projects.core.navigation)
            implementation(projects.core.resource)
            implementation(projects.core.ui)
            implementation(libs.kotlin.stdlib)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.components.uiToolingPreview)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kmp.maps.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.test.junit)
                implementation(libs.compose.ui.test)
                implementation(libs.compose.ui.test.junit4)
            }
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
