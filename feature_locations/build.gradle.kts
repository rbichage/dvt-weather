plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
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

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":core_database"))
    implementation(project(":core_data"))
    implementation(project(":core_ui"))
    implementation(project(":core_common"))

    implementation(libs.bundles.androidx.ui)

    implementation(libs.hilt.android)
    kapt(libs.hilt.kapt)

    implementation(libs.timber)
    debugImplementation(libs.leakcanary)

    implementation(libs.bundles.lifecycle)
    kapt(libs.lifecycle.common.java8)

    implementation(libs.bundles.navigation)
    implementation(libs.bundles.google.services)
}