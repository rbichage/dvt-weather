enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Weather Forecast"
include(":app")
include(":core_navigation")
include(":feature_weather")
include(":core_database")
include(":core_data")
include(":core_di")
include(":core_common")
include(":core_network")
include(":core_ui")
include(":feature_locations")
include(":feature_search")
include(":core_testing")
