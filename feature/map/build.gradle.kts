import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.koin.compiler)
}

kotlin {
    android {
        namespace = "com.sgmobile.earthquake.feature.map"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.presentation)
            implementation(libs.kotlin.stdlib)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            api(libs.koin.annotations)
        }

        androidMain.dependencies {
            implementation(projects.core.navigation)
            implementation(projects.core.resource)
            implementation(projects.core.ui)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.koin.core.viewmodel)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.components.uiToolingPreview)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.navigation3)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kmp.maps.compose)
        }
    }
}

composeCompiler {
    // Compose UI lives in androidMain only; iOS targets compile the shared controller.
    targetKotlinPlatforms.set(setOf(KotlinPlatformType.androidJvm))
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
