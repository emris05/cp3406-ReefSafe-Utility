package au.edu.jcu.reefsafe.domain

import java.util.Locale

private const val PLACEHOLDER = "—"

fun formatTemperature(c: Double?, units: Units): String {
    if (c == null) return PLACEHOLDER
    return when (units) {
        Units.METRIC -> String.format(Locale.US, "%.1f °C", c)
        Units.IMPERIAL -> String.format(Locale.US, "%.1f °F", c * 9.0 / 5.0 + 32.0)
    }
}

fun formatWaveHeight(m: Double?, units: Units): String {
    if (m == null) return PLACEHOLDER
    return when (units) {
        Units.METRIC -> String.format(Locale.US, "%.2f m", m)
        Units.IMPERIAL -> String.format(Locale.US, "%.1f ft", m * 3.28084)
    }
}

fun formatWindSpeed(kmh: Double?, units: Units): String {
    if (kmh == null) return PLACEHOLDER
    return when (units) {
        Units.METRIC -> String.format(Locale.US, "%.0f km/h", kmh)
        Units.IMPERIAL -> String.format(Locale.US, "%.0f mph", kmh * 0.621371)
    }
}

fun formatVisibility(km: Double?, units: Units): String {
    if (km == null) return PLACEHOLDER
    return when (units) {
        Units.METRIC -> String.format(Locale.US, "%.0f km", km)
        Units.IMPERIAL -> String.format(Locale.US, "%.0f mi", km * 0.621371)
    }
}

fun formatSunTime(iso: String?, use24Hour: Boolean): String? {
    if (iso == null) return null
    val tIdx = iso.indexOf('T')
    if (tIdx < 0) return iso
    val rest = iso.substring(tIdx + 1)
    val time = rest.takeWhile { it != '+' && it != '-' && it != 'Z' }
    val parts = time.split(':')
    if (parts.size < 2) return time
    val h = parts[0].toIntOrNull() ?: return time
    val m = parts[1].padStart(2, '0')
    return if (use24Hour) {
        "%02d:%s".format(h, m)
    } else {
        val period = if (h < 12) "am" else "pm"
        val h12 = when {
            h == 0 -> 12
            h > 12 -> h - 12
            else -> h
        }
        "$h12:$m $period"
    }
}
