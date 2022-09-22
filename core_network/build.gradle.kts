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

    implementation(project(":core_common"))
    implementation(project(":core_data"))

    implementation(libs.kotlin.core)
    implementation(libs.moshi)
    kapt(libs.moshi.codegen)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.timber)

    implementation(libs.kotlin.coroutines)

    implementation(libs.hilt.android)
    kapt(libs.hilt.kapt)
}