plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 23
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    implementation(project(":core_database"))
    implementation(project(":core_network"))
    implementation(libs.kotlin.core)

    implementation(libs.androidx.testing.junit)

    implementation(libs.android.testing.archcore)

    implementation(libs.kotlin.testing.coroutines)

    implementation(libs.testing.mockk)
    implementation(libs.testing.robolectric)

    implementation(libs.google.truth)

    implementation(libs.mockwebserver)

    implementation(libs.testing.livedata)

    implementation(libs.moshi)
    kapt(libs.moshi.codegen)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.bundles.room)
    kapt(libs.room.compiler)


}