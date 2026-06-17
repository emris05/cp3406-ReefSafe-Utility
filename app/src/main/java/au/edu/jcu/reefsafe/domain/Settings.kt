package au.edu.jcu.reefsafe.domain

data class NamedLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

object Locations {
    val TOWNSVILLE = NamedLocation("Townsville", -19.2590, 146.8169)
    val MAGNETIC_ISLAND = NamedLocation("Magnetic Island", -19.1300, 146.8500)
    val ALL = listOf(TOWNSVILLE, MAGNETIC_ISLAND)
    val DEFAULT = TOWNSVILLE
}

enum class Units { METRIC, IMPERIAL }

data class Settings(
    val location: NamedLocation = Locations.DEFAULT,
    val activity: Activity = Activity.SNORKEL,
    val units: Units = Units.METRIC,
    val showWaveCard: Boolean = true,
    val showTideCard: Boolean = true,
    val use24HourTime: Boolean = true
)
