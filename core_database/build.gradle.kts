plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")

}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 23
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":core_data"))

    implementation(libs.kotlin.core)

    implementation(libs.hilt.android)
    kapt(libs.hilt.kapt)

    implementation(libs.bundles.room)
    kapt(libs.room.compiler)

    testImplementation(libs.android.testing.archcore)
    testImplementation(libs.androidx.testing.junit)
    testImplementation(libs.kotlin.testing.coroutines)
    testImplementation(libs.testing.mockk)
    testImplementation(libs.testing.robolectric)
    testImplementation(libs.google.truth)
}