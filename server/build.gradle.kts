plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
    application
}

group = "com.sgmobile.earthquake"
version = "1.0.0"
application {
    mainClass.set("com.sgmobile.earthquake.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(libs.logback)
    implementation(libs.bundles.ktor.server)

    implementation(platform(libs.mongodb.driver.bom))
    implementation(libs.mongodb.driver.kotlin.coroutine)
    implementation(libs.mongodb.bson.kotlinx)

//    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}