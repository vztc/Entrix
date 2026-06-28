plugins {
    id("entrix.android.library.compose")
}

android {
    namespace = "com.apexbytestudio.entrix.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material.icons.extended)
}
