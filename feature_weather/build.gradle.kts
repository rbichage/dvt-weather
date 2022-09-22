plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 23
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
//            isIncludeAndroidResources = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            isMinifyEnabled = false
            isTestCoverageEnabled = true
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

    implementation(project(":core_data"))
    implementation(project(":core_database"))
    implementation(project(":core_common"))
    implementation(project(":core_ui"))
    implementation(project(":core_network"))
    implementation(project(":core_navigation"))
    implementation(project(":core_di"))
    implementation(project(":core_testing"))

    testImplementation(libs.testing.junit)

    debugImplementation(libs.androidx.testing.fragment)
    testImplementation(libs.androidx.testing.junit)
    androidTestImplementation(libs.androidx.testing.runner)
    androidTestImplementation(libs.androidx.testing.core)
    androidTestImplementation(libs.androidx.testing.espresso.core)
    androidTestImplementation(libs.androidx.testing.espresso.contrib)

    testImplementation(libs.android.testing.archcore)

    testImplementation(libs.kotlin.testing.coroutines)
    testImplementation("app.cash.turbine:turbine:0.9.0")

    testImplementation(libs.testing.mockk)
    testImplementation(libs.testing.robolectric)

    testImplementation(libs.google.truth)

    testImplementation(libs.mockwebserver)

    testImplementation(libs.testing.livedata)

    implementation(libs.bundles.androidx.ui)

    implementation(libs.bundles.lifecycle)
    kapt(libs.lifecycle.common.java8)

    implementation(libs.moshi)
    kapt(libs.moshi.codegen)

    implementation(libs.okhttp.logging)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)

    implementation(libs.hilt.android)
    kapt(libs.hilt.kapt)

    implementation(libs.timber)

    implementation(libs.bundles.navigation)

    implementation(libs.bundles.room)
    kapt(libs.room.compiler)

}