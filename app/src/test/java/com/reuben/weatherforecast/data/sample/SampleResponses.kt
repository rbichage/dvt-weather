package com.dvt.weatherforecast.data.sample

import android.location.Location
import com.reuben.core_data.models.weather.Clouds
import com.reuben.core_data.models.weather.Coord
import com.reuben.core_data.models.weather.CurrentWeatherResponse
import com.reuben.core_data.models.weather.Daily
import com.reuben.core_data.models.weather.FeelsLike
import com.reuben.core_data.models.weather.Main
import com.reuben.core_data.models.weather.OneShotForeCastResponse
import com.reuben.core_data.models.weather.Temp
import com.reuben.core_data.models.weather.Weather
import com.reuben.core_data.models.weather.Wind
import com.dvt.weatherforecast.data.models.db.LocationEntity


object SampleResponses {

    val fakeForeCasts = OneShotForeCastResponse(
            lat = -1.3177,
            lon = 36.8441,
            timezone = "Africa/Nairobi",
            timezoneOffset = 10800,
            daily = listOf(
                    Daily(clouds = 66, dewPoint = 9.41, dt = 1627894800,
                            feelsLike = FeelsLike(day = 21.73, eve = 17.4, morn = 11.24, night = 16.81),
                            humidity = 44, moonPhase = 0.8, moonrise = 1627856640, moonset = 1627900860,
                            pop = 0.34, pressure = 1018, sunrise = 1627875422, sunset = 1627918838,
                            temp = Temp(day = 22.3, eve = 17.67, max = 23.02, min = 11.74, morn = 11.74, night = 17.16),
                            uvi = 11.87,
                            weather = listOf(
                                    Weather(description = "light rain", icon = "10d", id = 500, main = "Rain")),
                            windDeg = 150, windGust = 5.3, windSpeed = 3.59)
            )
    )

    val testLocation = Location(javaClass.name).apply {
        latitude = -1.2907344085176307
        longitude = 36.82093485505406
    }

    val fakeWeatherResponse = CurrentWeatherResponse(
            base = "stations",
            clouds = Clouds(all = 75),
            cod = 200,
            coord = Coord(lat = -1.3177, lon = 36.8441),
            dt = 1627922227,
            id = 184736,
            main =
            Main(feelsLike = 19.8, humidity = 48, pressure = 1024, temp = 20.45, tempMax = 20.85, tempMin = 17.99),
            name = "Nairobi South",
            timezone = 10800,
            visibility = 10000,
            weather = listOf(
                    Weather(description = "broken clouds", icon = "04 n", id = 803, main = "Clouds")
            ),
            wind = Wind(speed = 3.6))


    val fakeLocations = listOf(
            LocationEntity(
                    "Nairobi",
                    "-1.2907344085176307, 36.82093485505406",
                    -1.2907344085176307,
                    36.82093485505406,
                    20,
                    25,
                    20,
                    System.currentTimeMillis(),
                    true,
                    "800",
                    "Clear",

            ),
            LocationEntity(
                    "Mombasa",
                    "-1.2907344085176307, 36.82093485505406",
                    -1.2907344085176307,
                    36.82093485505406,
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
                    "-1.2907344085176307, 36.82093485505406",
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
                    "-1.2907344085176307, 36.82093485505406",
                    -1.2907344085176307, 36.82093485505406,
                    20, 25, 20,
                    System.currentTimeMillis(),
                    false, "800", "Clear"

            ),

            LocationEntity(
                    "Makueni",
                    "-1.2907344085176307, 36.82093485505406",
                    -1.2907344085176307, 36.82093485505406,
                    20, 25, 20,
                    System.currentTimeMillis(),
                    false, "800", "Clear"

            )

    )
}

