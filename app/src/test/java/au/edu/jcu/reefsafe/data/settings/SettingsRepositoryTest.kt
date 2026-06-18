package au.edu.jcu.reefsafe.data.settings

import au.edu.jcu.reefsafe.domain.Activity
import au.edu.jcu.reefsafe.domain.Locations
import au.edu.jcu.reefsafe.domain.Settings
import au.edu.jcu.reefsafe.domain.Units
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsRepositoryTest {

    @Test
    fun default_state_is_townsville_snorkel_metric() {
        val repo = SettingsRepository()
        val s = repo.settings.value
        assertEquals(Locations.DEFAULT, s.location)
        assertEquals(Activity.SNORKEL, s.activity)
        assertEquals(Units.METRIC, s.units)
        assertEquals(true, s.showWaveCard)
        assertEquals(true, s.use24HourTime)
    }

    @Test
    fun update_location_emits_new_settings() {
        val repo = SettingsRepository()
        repo.update { it.copy(location = Locations.MAGNETIC_ISLAND) }
        assertEquals(Locations.MAGNETIC_ISLAND, repo.settings.value.location)
    }

    @Test
    fun update_only_changes_targeted_field() {
        val repo = SettingsRepository()
        repo.update { it.copy(activity = Activity.SCUBA) }
        val s = repo.settings.value
        assertEquals(Activity.SCUBA, s.activity)
        assertEquals(Locations.DEFAULT, s.location)
        assertEquals(Units.METRIC, s.units)
    }

    @Test
    fun update_units_does_not_toggle_other_fields() {
        val repo = SettingsRepository()
        repo.update { it.copy(units = Units.IMPERIAL, showWaveCard = false) }
        val s = repo.settings.value
        assertEquals(Units.IMPERIAL, s.units)
        assertEquals(false, s.showWaveCard)
        assertEquals(true, s.use24HourTime)
        assertEquals(Activity.SNORKEL, s.activity)
    }
}
