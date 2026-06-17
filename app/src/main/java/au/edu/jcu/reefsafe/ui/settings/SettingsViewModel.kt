package au.edu.jcu.reefsafe.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.reefsafe.data.settings.SettingsRepository
import au.edu.jcu.reefsafe.domain.Activity
import au.edu.jcu.reefsafe.domain.NamedLocation
import au.edu.jcu.reefsafe.domain.Settings
import au.edu.jcu.reefsafe.domain.Units
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<Settings> = settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = settingsRepository.settings.value
    )

    fun setLocation(location: NamedLocation) = update { it.copy(location = location) }

    fun setActivity(activity: Activity) = update { it.copy(activity = activity) }

    fun setUnits(units: Units) = update { it.copy(units = units) }

    fun setShowWaveCard(show: Boolean) = update { it.copy(showWaveCard = show) }

    fun setShowTideCard(show: Boolean) = update { it.copy(showTideCard = show) }

    fun setUse24HourTime(use: Boolean) = update { it.copy(use24HourTime = use) }

    private fun update(transform: (Settings) -> Settings) {
        settingsRepository.update(transform)
    }
}
