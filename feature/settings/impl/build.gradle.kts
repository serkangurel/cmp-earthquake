plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    android {
        namespace = "com.sgmobile.earthquake.feature.settings.impl"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(projects.feature.settings.api)
            implementation(projects.core.navigation.impl)
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
            implementation(libs.koin.core)
            implementation(libs.koin.compose.navigation3)
        }
    }
}
