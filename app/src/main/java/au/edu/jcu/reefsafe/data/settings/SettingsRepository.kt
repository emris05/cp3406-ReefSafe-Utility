package au.edu.jcu.reefsafe.data.settings

import au.edu.jcu.reefsafe.domain.Settings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SettingsRepository @Inject constructor() {

    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    fun update(transform: (Settings) -> Settings) {
        _settings.value = transform(_settings.value)
    }
}
