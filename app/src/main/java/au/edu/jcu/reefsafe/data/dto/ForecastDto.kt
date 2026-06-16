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
    val temperature2m: Double?,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double?,
    @Json(name = "wind_direction_10m") val windDirection10m: Double?,
    val visibility: Double?,
    val uvIndex: Double?,
    val cloudCover: Double?
)

data class ForecastDaily(
    val sunrise: List<String>?,
    val sunset: List<String>?
)
