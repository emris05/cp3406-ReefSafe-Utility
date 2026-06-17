package au.edu.jcu.reefsafe.domain

import au.edu.jcu.reefsafe.data.model.ReefConditions

enum class Activity { SNORKEL, FREE_DIVE, SCUBA }

enum class Verdict { GLASS_CALM, SOLID, WORKABLE, SKIP_IT }

data class SnorkelScore(val value: Int, val verdict: Verdict)

private data class Weights(
    val wave: Double,
    val wind: Double,
    val visibility: Double,
    val sst: Double,
    val uv: Double
)

fun scoreConditions(
    conditions: ReefConditions,
    activity: Activity = Activity.SNORKEL
): SnorkelScore {
    val weights = when (activity) {
        Activity.SNORKEL   -> Weights(wave = 0.40, wind = 0.20, visibility = 0.15, sst = 0.10, uv = 0.15)
        Activity.FREE_DIVE -> Weights(wave = 0.45, wind = 0.15, visibility = 0.20, sst = 0.10, uv = 0.10)
        Activity.SCUBA     -> Weights(wave = 0.20, wind = 0.15, visibility = 0.25, sst = 0.15, uv = 0.25)
    }

    val raw =
        scoreWave(conditions.waveHeightM) * weights.wave +
            scoreWind(conditions.windSpeedKmh) * weights.wind +
            scoreVisibility(conditions.visibilityKm) * weights.visibility +
            scoreSeaSurfaceTemp(conditions.seaSurfaceTempC) * weights.sst +
            scoreUv(conditions.uvIndex) * weights.uv

    val value = (raw * 100.0).toInt().coerceIn(0, 100)
    val verdict = when {
        value >= 80 -> Verdict.GLASS_CALM
        value >= 60 -> Verdict.SOLID
        value >= 40 -> Verdict.WORKABLE
        else -> Verdict.SKIP_IT
    }
    return SnorkelScore(value, verdict)
}

private fun scoreWave(heightM: Double?): Double =
    if (heightM == null) 0.5 else (1.0 - heightM / 2.0).coerceIn(0.0, 1.0)

private fun scoreWind(kmh: Double?): Double =
    if (kmh == null) 0.5 else (1.0 - kmh / 40.0).coerceIn(0.0, 1.0)

private fun scoreVisibility(km: Double?): Double =
    if (km == null) 0.5 else ((km - 5.0) / 25.0).coerceIn(0.0, 1.0)

private fun scoreSeaSurfaceTemp(c: Double?): Double {
    if (c == null) return 0.5
    val drift = when {
        c < 25.0 -> 25.0 - c
        c > 28.0 -> c - 28.0
        else -> 0.0
    }
    return (1.0 - drift / 10.0).coerceIn(0.0, 1.0)
}

private fun scoreUv(uv: Double?): Double =
    if (uv == null) 0.5 else (1.0 - (uv - 5.0) / 6.0).coerceIn(0.0, 1.0)
