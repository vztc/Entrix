plugins {
    id("entrix.android.library")
}

android {
    namespace = "com.apexbytestudio.entrix.core.datastore"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
}
