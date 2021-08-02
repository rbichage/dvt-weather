package com.dvt.weatherforecast.data.sample

import com.dvt.weatherforecast.data.models.db.LocationEntity

object SamplePayLoads {
    val sampleLocation = LocationEntity(
            "Nairobi",
            -1.2907344085176307, 36.82093485505406,
            20,
            25,
            20,
            System.currentTimeMillis(),
            true,
            "800",
            "Clear"

    )
}