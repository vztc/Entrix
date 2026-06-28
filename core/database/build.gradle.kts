plugins {
    id("entrix.android.library")
    id("entrix.android.room")
}

android {
    namespace = "com.apexbytestudio.entrix.core.database"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
