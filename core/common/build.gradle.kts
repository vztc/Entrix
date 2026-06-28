plugins {
    id("entrix.android.library")
}

android {
    namespace = "com.apexbytestudio.entrix.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
}
