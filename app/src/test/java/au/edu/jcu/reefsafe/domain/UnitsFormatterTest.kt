package au.edu.jcu.reefsafe.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UnitsFormatterTest {

    @Test fun temperature_metric() {
        assertEquals("26.0 °C", formatTemperature(26.0, Units.METRIC))
    }

    @Test fun temperature_imperial() {
        assertEquals("78.8 °F", formatTemperature(26.0, Units.IMPERIAL))
    }

    @Test fun temperature_null_returns_dash() {
        assertEquals("—", formatTemperature(null, Units.METRIC))
    }

    @Test fun wave_height_metric() {
        assertEquals("0.40 m", formatWaveHeight(0.4, Units.METRIC))
    }

    @Test fun wave_height_imperial() {
        assertEquals("1.3 ft", formatWaveHeight(0.4, Units.IMPERIAL))
    }

    @Test fun wave_height_null_returns_dash() {
        assertEquals("—", formatWaveHeight(null, Units.METRIC))
    }

    @Test fun wind_speed_metric() {
        assertEquals("12 km/h", formatWindSpeed(12.0, Units.METRIC))
    }

    @Test fun wind_speed_imperial() {
        assertEquals("7 mph", formatWindSpeed(12.0, Units.IMPERIAL))
    }

    @Test fun wind_speed_null_returns_dash() {
        assertEquals("—", formatWindSpeed(null, Units.METRIC))
    }

    @Test fun visibility_metric() {
        assertEquals("12 km", formatVisibility(12.0, Units.METRIC))
    }

    @Test fun visibility_imperial() {
        assertEquals("7 mi", formatVisibility(12.0, Units.IMPERIAL))
    }

    @Test fun visibility_null_returns_dash() {
        assertEquals("—", formatVisibility(null, Units.METRIC))
    }

    @Test fun sun_time_24h() {
        assertEquals("06:42", formatSunTime("2026-06-18T06:42", use24Hour = true))
    }

    @Test fun sun_time_12h_morning() {
        assertEquals("6:42 am", formatSunTime("2026-06-18T06:42", use24Hour = false))
    }

    @Test fun sun_time_12h_afternoon() {
        assertEquals("6:15 pm", formatSunTime("2026-06-18T18:15", use24Hour = false))
    }

    @Test fun sun_time_12h_noon() {
        assertEquals("12:00 pm", formatSunTime("2026-06-18T12:00", use24Hour = false))
    }

    @Test fun sun_time_12h_midnight() {
        assertEquals("12:00 am", formatSunTime("2026-06-18T00:00", use24Hour = false))
    }

    @Test fun sun_time_drops_seconds_when_present() {
        assertEquals("06:42", formatSunTime("2026-06-18T06:42:30", use24Hour = true))
    }

    @Test fun sun_time_null_returns_null() {
        assertNull(formatSunTime(null, use24Hour = true))
    }
}
