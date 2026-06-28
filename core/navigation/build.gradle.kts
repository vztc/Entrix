plugins {
    id("entrix.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.apexbytestudio.entrix.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
}
