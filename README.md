# Weather Fore Cast


## Pre Requisities

- [A valid Google Maps Api Key](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
- [A valid Open Weather API Key](https://openweathermap.org/appid)

## Setup
 - Add `GOOGLE_MAPS_KEY` to your `local.properties` file
 - Add ``api_key`` to `strings.xml`
 - Build the project

## Design
This project follows [MVVM architecture](https://developer.android.com/jetpack/guide) a.k.a Jetpack Components. 
Sample screenshot of architecture.

![android jetpack](https://user-images.githubusercontent.com/17246592/119951852-12bfc980-bfa5-11eb-89a5-0e9ad996f58d.png)

# Screenshots of the App: 
![](https://user-images.githubusercontent.com/17246592/127883474-fdf83d0c-fd39-4305-992b-36633983fbfc.jpg)
![](https://user-images.githubusercontent.com/17246592/127883500-612aa0f7-6a41-4aa2-bc8b-9fe858b91ed6.jpg)
![](https://user-images.githubusercontent.com/17246592/127883510-296329aa-59ec-43fa-ab2a-38e13c750bd0.jpg)
![](https://user-images.githubusercontent.com/17246592/127883537-7f4ccdd3-d9d2-43c3-af58-d80f233d5e27.jpg)

## Unit and UI tests
Unit and UI tests can be found under androidTest and test sub modules 

# Screenshot of Tests running
![ezgif com-gif-maker](https://user-images.githubusercontent.com/17246592/127883854-a922e2a5-1ace-4e36-ba7e-e636ad53a54e.gif)



## Tools and Libraries
- [detekt](https://detekt.github.io/detekt/) for static analysis
- [Moshi]https://github.com/square/moshi) for Json to data class conversations.
- [Retrofit](https://square.github.io/retrofit/) for network calls.
- [OKhttp](https://square.github.io/okhttp/) for network and logging.
- [Hilt](https://dagger.dev/hilt/) for dependency injection.
- [Material](https://material.io/design) for UI design.
- [Leakacanary](https://github.com/square/leakcanary) for Memory leak detection.
- [Timber](https://github.com/JakeWharton/timber) for logging.
- [Android architecture](https://developer.android.com/jetpack/guide) components for architecture design.

# Building
To build this project, ensure you are running at least [Android Studio](https://developer.android.com/studio) ```Arctic Fox``` or An equivalent [Android Studio Canary/Beta](https://developer.android.com/studio/preview) version.


# Happy Coding!

