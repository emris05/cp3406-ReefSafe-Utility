package au.edu.jcu.reefsafe.data.api

import au.edu.jcu.reefsafe.data.dto.MarineResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoMarineService {
    @GET("marine")
    suspend fun getMarine(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String
    ): MarineResponse
}
