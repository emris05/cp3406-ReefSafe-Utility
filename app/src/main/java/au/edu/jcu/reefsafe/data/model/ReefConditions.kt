package au.edu.jcu.reefsafe.data.model

data class ReefConditions(
    val seaSurfaceTempC: Double?,
    val waveHeightM: Double?,
    val wavePeriodS: Double?,
    val waveDirectionDeg: Double?,
    val windSpeedKmh: Double?,
    val windDirectionDeg: Double?,
    val visibilityKm: Double?,
    val uvIndex: Double?,
    val cloudCoverPct: Double?,
    val airTempC: Double?,
    val sunrise: String?,
    val sunset: String?
)
