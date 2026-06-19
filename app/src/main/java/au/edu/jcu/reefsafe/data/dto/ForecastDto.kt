package au.edu.jcu.reefsafe.data.dto

import com.squareup.moshi.Json

data class ForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val current: ForecastCurrent,
    val daily: ForecastDaily?
)

data class ForecastCurrent(
    val time: String?,
    @Json(name = "temperature_2m") val temperature2m: Double?,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double?,
    @Json(name = "wind_direction_10m") val windDirection10m: Double?,
    val visibility: Double?,
    @Json(name = "uv_index") val uvIndex: Double?,
    @Json(name = "cloud_cover") val cloudCover: Double?
)

data class ForecastDaily(
    val sunrise: List<String>?,
    val sunset: List<String>?
)
