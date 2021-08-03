# Weather Fore Cast


## Pre Requisities

- [A valid Google Maps Api Key](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
- [A valid Open Weather API Key](https://openweathermap.org/appid)

## Setup
 - Add `GOOGLE_MAPS_KEY` to your `local.properties` file
 - Paster your open weather API key to ``api_key`` on `strings.xml`
 - Build the project



To inspect the local db/cache, run the project on a device with Android 8+

### Background

Develop an application that:

* gets current weather forecast of a location and displays forecast over the next week.
* Shows a list of locations, and the same on a map.
* Add a new location using Google Places API


## Tech-stack

* Tech-stack
    * [Kotlin](https://kotlinlang.org/) - a cross-platform, statically typed, general-purpose programming language with type inference.
    * [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - perform background operations.
    * [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - handle the stream of data asynchronously that executes sequentially.
    * [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Hilt is a dependency injection library for Android that reduces the boilerplate of doing manual dependency injection in your project.
    * [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android.
    * [Jetpack](https://developer.android.com/jetpack)
        * [Room](https://developer.android.com/topic/libraries/architecture/room) - a persistence library provides an abstraction layer over SQLite.
        * [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - is an observable data holder.
        * [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) - perform action when lifecycle state changes.
        * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - store and manage UI-related data in a lifecycle conscious way.
        * [Jetpack Navigation](https://developer.android.com/guide/navigation/navigation-getting-started) -  Implement navigation, from simple button clicks to more complex patterns, such as app bars and the navigation drawer.

    * [Leak Canary](https://github.com/square/leakcanary) - a memory leak detection library for Android.

* Architecture
    * MVVM - Model View View Model
* Tests
    * [Unit Tests](https://en.wikipedia.org/wiki/Unit_testing) ([JUnit](https://junit.org/junit4/)) - a simple framework to write repeatable tests.
    * [MockK](https://github.com/mockk) - mocking library for Kotlin
    * [Kluent](https://github.com/MarkusAmshove/Kluent) - Fluent Assertion-Library for Kotlin
    * [Kakao](https://github.com/agoda-com/Kakao) - Nice and simple DSL for Espresso in Kotlin

    * Plugins
        * [Detekt](https://github.com/detekt/detekt) - a static code analysis tool for the Kotlin programming language.
      
* CI/CD
    * Github Actions



# Screenshots of the App: 
![](https://user-images.githubusercontent.com/17246592/127883474-fdf83d0c-fd39-4305-992b-36633983fbfc.jpg)
![](https://user-images.githubusercontent.com/17246592/127883500-612aa0f7-6a41-4aa2-bc8b-9fe858b91ed6.jpg)
![](https://user-images.githubusercontent.com/17246592/127883510-296329aa-59ec-43fa-ab2a-38e13c750bd0.jpg)
![](https://user-images.githubusercontent.com/17246592/127883537-7f4ccdd3-d9d2-43c3-af58-d80f233d5e27.jpg)


# Screenshot of Tests running
![ezgif com-gif-maker](https://user-images.githubusercontent.com/17246592/127883854-a922e2a5-1ace-4e36-ba7e-e636ad53a54e.gif)

# Building
To build this project, ensure you are running at least [Android Studio](https://developer.android.com/studio) ```Arctic Fox``` or An equivalent [Android Studio Canary/Beta](https://developer.android.com/studio/preview) version.


# Happy Coding!

