plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("io.gitlab.arturbosch.detekt")

}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.reuben.weatherforecast"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
//            isIncludeAndroidResources = true
        }
    }

    buildTypes {
        val release by getting {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        val debug by getting {
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

    kotlinOptions.jvmTarget = "1.8"
}

dependencies {

    implementation(project(":core_database"))
    implementation(project(":core_data"))
    implementation(project(":core_ui"))
    implementation(project(":core_navigation"))
    implementation(project(":feature_weather"))
    implementation(project(":feature_locations"))
    implementation(project(":feature_search"))
    implementation(project(":feature_weather"))


    testImplementation(libs.testing.junit)

    debugImplementation(libs.androidx.testing.fragment)
    testImplementation(libs.androidx.testing.junit)
    androidTestImplementation(libs.androidx.testing.runner)
    androidTestImplementation(libs.androidx.testing.core)
    androidTestImplementation(libs.androidx.testing.espresso.core)
    androidTestImplementation(libs.androidx.testing.espresso.contrib)

    testImplementation(libs.android.testing.archcore)

    testImplementation(libs.kotlin.testing.coroutines)

    testImplementation(libs.testing.mockk)
    testImplementation(libs.testing.robolectric)

    testImplementation(libs.google.truth)

    testImplementation(libs.mockwebserver)

    testImplementation(libs.testing.livedata)

    implementation(libs.bundles.androidx.ui)

    implementation(libs.hilt.android)
    kapt(libs.hilt.kapt)

    implementation(libs.moshi)
    kapt(libs.moshi.codegen)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.bundles.lifecycle)
    kapt(libs.lifecycle.common.java8)

    implementation(libs.bundles.navigation)
    implementation(libs.bundles.google.services)


    implementation(libs.timber)
    debugImplementation(libs.leakcanary)

    implementation(libs.dexter)

    implementation(libs.bundles.room)
    kapt(libs.room.compiler)

}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("${project.rootDir}/detekt.yml")
    baseline = file("${project.rootDir}/detekt-baseline.xml")

    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }

}



