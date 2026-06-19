package au.edu.jcu.reefsafe.ui.utility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.reefsafe.data.model.ReefConditions
import au.edu.jcu.reefsafe.data.repository.ReefRepository
import au.edu.jcu.reefsafe.data.settings.SettingsRepository
import au.edu.jcu.reefsafe.domain.Settings
import au.edu.jcu.reefsafe.domain.SnorkelScore
import au.edu.jcu.reefsafe.domain.scoreConditions
import au.edu.jcu.reefsafe.util.Result as ReefResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UtilityUiState {
    data object Loading : UtilityUiState
    data class Success(
        val conditions: ReefConditions,
        val score: SnorkelScore,
        val refreshing: Boolean = false
    ) : UtilityUiState
    data class Error(val message: String) : UtilityUiState
}

@HiltViewModel
class UtilityViewModel @Inject constructor(
    private val reefRepository: ReefRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val conditions = MutableStateFlow<ReefConditions?>(null)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val refreshing = MutableStateFlow(false)

    val uiState: StateFlow<UtilityUiState> = combine(
        conditions, errorMessage, refreshing, settingsRepository.settings
    ) { conditions, error, refreshing, settings ->
        when {
            error != null -> UtilityUiState.Error(error)
            conditions != null -> {
                val score = scoreConditions(conditions, settings.activity)
                UtilityUiState.Success(conditions = conditions, score = score, refreshing = refreshing)
            }
            else -> UtilityUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UtilityUiState.Loading
    )

    val settings: StateFlow<Settings> = settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = settingsRepository.settings.value
    )

    init {
        refresh()
        viewModelScope.launch {
            settingsRepository.settings
                .map { it.location }
                .distinctUntilChanged()
                .drop(1)
                .collect { refresh() }
        }
    }

    fun refresh() {
        val location = settingsRepository.settings.value.location
        refreshing.value = true
        viewModelScope.launch {
            when (val result = reefRepository.getReefConditions(location.latitude, location.longitude)) {
                is ReefResult.Success -> {
                    conditions.value = result.data
                    errorMessage.value = null
                }
                is ReefResult.Error -> {
                    errorMessage.value = result.exception.message ?: "Unknown error"
                }
            }
            refreshing.value = false
        }
    }
}
