plugins {
    id("entrix.android.library")
}

android {
    namespace = "com.apexbytestudio.entrix.core.location"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
}
