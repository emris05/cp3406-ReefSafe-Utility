package au.edu.jcu.reefsafe.data.repository

import au.edu.jcu.reefsafe.data.api.OpenMeteoForecastService
import au.edu.jcu.reefsafe.data.api.OpenMeteoMarineService
import au.edu.jcu.reefsafe.data.model.ReefConditions
import au.edu.jcu.reefsafe.util.Result
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class ReefRepository @Inject constructor(
    private val forecastService: OpenMeteoForecastService,
    private val marineService: OpenMeteoMarineService
) {
    suspend fun getReefConditions(
        latitude: Double,
        longitude: Double
    ): Result<ReefConditions> = withContext(Dispatchers.IO) {
        try {
            val forecast = forecastService.getForecast(
                latitude = latitude,
                longitude = longitude,
                current = "temperature_2m,wind_speed_10m,wind_direction_10m,visibility,uv_index,cloud_cover",
                daily = "sunrise,sunset"
            )
            val marine = marineService.getMarine(
                latitude = latitude,
                longitude = longitude,
                current = "wave_height,wave_direction,wave_period,sea_surface_temperature"
            )
            Result.Success(
                ReefConditions(
                    seaSurfaceTempC = marine.current.seaSurfaceTemperature,
                    waveHeightM = marine.current.waveHeight,
                    wavePeriodS = marine.current.wavePeriod,
                    waveDirectionDeg = marine.current.waveDirection,
                    windSpeedKmh = forecast.current.windSpeed10m,
                    windDirectionDeg = forecast.current.windDirection10m,
                    visibilityKm = forecast.current.visibility?.div(1000.0),
                    uvIndex = forecast.current.uvIndex,
                    cloudCoverPct = forecast.current.cloudCover,
                    airTempC = forecast.current.temperature2m,
                    sunrise = forecast.daily?.sunrise?.firstOrNull(),
                    sunset = forecast.daily?.sunset?.firstOrNull()
                )
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
