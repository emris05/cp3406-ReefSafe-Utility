package au.edu.jcu.reefsafe.domain

import au.edu.jcu.reefsafe.data.model.ReefConditions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SnorkelScoreTest {

    @Test
    fun all_ideal_conditions_give_glass_calm() {
        val score = scoreConditions(
            conditions(wave = 0.0, wind = 0.0, visibility = 30.0, sst = 27.0, uv = 5.0),
            Activity.SNORKEL
        )
        assertEquals(100, score.value)
        assertEquals(Verdict.GLASS_CALM, score.verdict)
    }

    @Test
    fun all_bad_conditions_give_skip_it() {
        val score = scoreConditions(
            conditions(wave = 2.0, wind = 40.0, visibility = 5.0, sst = 20.0, uv = 11.0),
            Activity.SNORKEL
        )
        assertEquals(Verdict.SKIP_IT, score.verdict)
        assertTrue("expected low score but got ${score.value}", score.value < 40)
    }

    @Test
    fun all_null_conditions_score_50_and_workable() {
        val score = scoreConditions(allNull(), Activity.SNORKEL)
        assertEquals(50, score.value)
        assertEquals(Verdict.WORKABLE, score.verdict)
    }

    @Test
    fun activity_weights_change_score_for_same_conditions() {
        val conds = conditions(wave = 1.5, wind = 10.0, visibility = 25.0, sst = 27.0, uv = 3.0)
        val snorkel = scoreConditions(conds, Activity.SNORKEL)
        val scuba = scoreConditions(conds, Activity.SCUBA)
        assertTrue(
            "expected different scores for different activities, got snorkel=${snorkel.value} scuba=${scuba.value}",
            snorkel.value != scuba.value
        )
        assertTrue(
            "scuba weights visibility/uv more heavily, so should score higher here",
            scuba.value > snorkel.value
        )
    }

    @Test
    fun score_is_coerced_into_0_to_100_range() {
        val ideal = conditions(wave = 0.0, wind = 0.0, visibility = 30.0, sst = 27.0, uv = 5.0)
        assertEquals(100, scoreConditions(ideal, Activity.SNORKEL).value)

        val extreme = conditions(wave = 100.0, wind = 200.0, visibility = -50.0, sst = 0.0, uv = 50.0)
        assertEquals(0, scoreConditions(extreme, Activity.SNORKEL).value)
    }

    private fun conditions(
        wave: Double?,
        wind: Double?,
        visibility: Double?,
        sst: Double?,
        uv: Double?
    ) = ReefConditions(
        seaSurfaceTempC = sst,
        waveHeightM = wave,
        wavePeriodS = null,
        waveDirectionDeg = null,
        windSpeedKmh = wind,
        windDirectionDeg = null,
        visibilityKm = visibility,
        uvIndex = uv,
        cloudCoverPct = null,
        airTempC = null,
        sunrise = null,
        sunset = null
    )

    private fun allNull() = ReefConditions(
        seaSurfaceTempC = null,
        waveHeightM = null,
        wavePeriodS = null,
        waveDirectionDeg = null,
        windSpeedKmh = null,
        windDirectionDeg = null,
        visibilityKm = null,
        uvIndex = null,
        cloudCoverPct = null,
        airTempC = null,
        sunrise = null,
        sunset = null
    )
}
