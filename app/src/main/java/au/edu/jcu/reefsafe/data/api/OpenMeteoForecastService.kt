package au.edu.jcu.reefsafe.data.api

import au.edu.jcu.reefsafe.data.dto.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoForecastService {
    @GET("forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String,
        @Query("daily") daily: String? = null,
        @Query("forecast_days") forecastDays: Int = 1,
        @Query("timezone") timezone: String = "auto"
    ): ForecastResponse
}
