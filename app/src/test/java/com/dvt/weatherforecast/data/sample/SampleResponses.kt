package com.dvt.weatherforecast.data.sample

import com.dvt.weatherforecast.data.models.*
import com.dvt.weatherforecast.data.models.db.LocationEntity


object SampleResponses {
    val fakeWeatherResponse = CurrentWeatherResponse(
            base = "Random",
            clouds = Clouds(1),
            cod = 100,
            coord = Coord(-1.2907344085176307, 36.82093485505406),
            dt = 100,
            id = 800,
            main = Main(
                    feelsLike = 21.5,
                    humidity = 5,
                    pressure = 10,
                    temp = 21.0,
                    tempMax = 22.5,
                    tempMin = 20.0
            ),
            name = "Random Name",
            timezone = 3,
            visibility = 1,
            weather = listOf(
                    Weather(
                            description = "Cloudy with a lot of clouds lol",
                            icon = "dt4",
                            id = 800,
                            main = "Clouds"
                    )
            ),
            wind = Wind(20.0)
    )


    val fakeLocations = listOf(
            LocationEntity(
                    "Nairobi",
                    -1.2907344085176307, 36.82093485505406,
                    20,
                    25,
                    20,
                    System.currentTimeMillis(),
                    true,
                    "800",
                    "Clear"

            ),
            LocationEntity(
                    "Mombasa",
                    -1.2907344085176307, 36.82093485505406,
                    20,
                    25,
                    20,
                    System.currentTimeMillis(),
                    false,
                    "800",
                    "Clear"

            ),

            LocationEntity(
                    "Athi River",
                    -1.2907344085176307, 36.82093485505406,
                    20,
                    25,
                    20,
                    System.currentTimeMillis(),
                    false,
                    "800",
                    "Clear"

            ),

            LocationEntity(
                    "New York",
                    -1.2907344085176307, 36.82093485505406,
                    20, 25, 20,
                    System.currentTimeMillis(),
                    false, "800", "Clear"

            ),

            LocationEntity(
                    "Makueni",
                    -1.2907344085176307, 36.82093485505406,
                    20, 25, 20,
                    System.currentTimeMillis(),
                    false, "800", "Clear"

            )

    )
}

