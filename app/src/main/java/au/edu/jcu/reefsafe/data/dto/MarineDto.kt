package au.edu.jcu.reefsafe.data.dto

import com.squareup.moshi.Json

data class MarineResponse(
    val latitude: Double,
    val longitude: Double,
    val current: MarineCurrent
)

data class MarineCurrent(
    val time: String?,
    val waveHeight: Double?,
    @Json(name = "wave_direction") val waveDirection: Double?,
    @Json(name = "wave_period") val wavePeriod: Double?,
    @Json(name = "sea_surface_temperature") val seaSurfaceTemperature: Double?
)
