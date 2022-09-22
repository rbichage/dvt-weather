buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.android.gradle)
        classpath(libs.google.gmsservices)
        classpath(libs.hilt.gradle)
        classpath(libs.navigation.safeargs.gradle)
        classpath(libs.kotlin.gradle)
        classpath(libs.google.secrets.gradle)
    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}