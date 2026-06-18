package au.edu.jcu.reefsafe.ui.utility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import au.edu.jcu.reefsafe.data.model.ReefConditions
import au.edu.jcu.reefsafe.domain.Activity
import au.edu.jcu.reefsafe.domain.Settings
import au.edu.jcu.reefsafe.domain.SnorkelScore
import au.edu.jcu.reefsafe.domain.Verdict
import au.edu.jcu.reefsafe.domain.formatSunTime
import au.edu.jcu.reefsafe.domain.formatTemperature
import au.edu.jcu.reefsafe.domain.formatVisibility
import au.edu.jcu.reefsafe.domain.formatWaveHeight
import au.edu.jcu.reefsafe.domain.formatWindSpeed
import au.edu.jcu.reefsafe.ui.theme.ReefSafeTheme
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun UtilityScreen(viewModel: UtilityViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            UtilityUiState.Loading -> LoadingState()
            is UtilityUiState.Error -> ErrorState(state.message, onRetry = viewModel::refresh)
            is UtilityUiState.Success -> SuccessState(state, settings)
        }
        if (uiState is UtilityUiState.Success) {
            val refreshing = (uiState as UtilityUiState.Success).refreshing
            SmallFloatingActionButton(
                onClick = viewModel::refresh,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                if (refreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text("Checking the reef…", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Couldn't load conditions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun SuccessState(state: UtilityUiState.Success, settings: Settings) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScoreCard(state.score, settings)
        Spacer(Modifier.height(16.dp))
        ConditionListCard(state.conditions, settings)
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun ScoreCard(score: SnorkelScore, settings: Settings) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Snorkel Score", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                score.value.toString(),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            VerdictPill(score.verdict)
            Spacer(Modifier.height(12.dp))
            Text(
                "${settings.location.name} · ${settings.activity.label()}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun VerdictPill(verdict: Verdict) {
    val container = when (verdict) {
        Verdict.GLASS_CALM -> Color(0xFF2E7D32)
        Verdict.SOLID -> Color(0xFF1976D2)
        Verdict.WORKABLE -> Color(0xFFE65100)
        Verdict.SKIP_IT -> Color(0xFFC62828)
    }
    Surface(
        color = container,
        contentColor = Color.White,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            verdict.label(),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private data class Condition(val label: String, val value: String)

@Composable
private fun ConditionListCard(conditions: ReefConditions, settings: Settings) {
    val items = buildConditions(conditions, settings)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            items.forEachIndexed { index, item ->
                ConditionRow(item.label, item.value)
                if (index != items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun ConditionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun buildConditions(conditions: ReefConditions, settings: Settings): List<Condition> = buildList {
    add(Condition("Sea Temperature", formatTemperature(conditions.seaSurfaceTempC, settings.units)))
    if (settings.showWaveCard) {
        val wave = formatWaveHeight(conditions.waveHeightM, settings.units)
        val parts = buildList {
            if (wave != "—") add(wave)
            conditions.wavePeriodS?.let { add("${(it * 10).roundToInt() / 10.0}s") }
            conditions.waveDirectionDeg?.let { add(cardinal(it)) }
        }
        add(Condition("Wave Height", parts.joinToString(" · ").ifEmpty { "—" }))
    }
    val wind = formatWindSpeed(conditions.windSpeedKmh, settings.units)
    val windParts = buildList {
        if (wind != "—") add(wind)
        conditions.windDirectionDeg?.let { add(cardinal(it)) }
    }
    add(Condition("Wind", windParts.joinToString(" · ").ifEmpty { "—" }))
    add(Condition("Visibility", formatVisibility(conditions.visibilityKm, settings.units)))
    add(Condition(
        "UV Index",
        conditions.uvIndex?.let { "%.1f".format(Locale.US, it) } ?: "—"
    ))
    add(Condition("Air Temperature", formatTemperature(conditions.airTempC, settings.units)))
    add(Condition(
        "Cloud Cover",
        conditions.cloudCoverPct?.let { "${it.roundToInt()}%" } ?: "—"
    ))
    val sunrise = formatSunTime(conditions.sunrise, settings.use24HourTime)
    val sunset = formatSunTime(conditions.sunset, settings.use24HourTime)
    val sun = when {
        sunrise != null && sunset != null -> "$sunrise · $sunset"
        else -> sunrise ?: sunset ?: "—"
    }
    add(Condition("Sunrise / Sunset", sun))
}

private fun Verdict.label(): String = when (this) {
    Verdict.GLASS_CALM -> "Glass calm"
    Verdict.SOLID -> "Solid"
    Verdict.WORKABLE -> "Workable"
    Verdict.SKIP_IT -> "Skip it"
}

private fun Activity.label(): String = when (this) {
    Activity.SNORKEL -> "Snorkel"
    Activity.FREE_DIVE -> "Free Dive"
    Activity.SCUBA -> "Scuba"
}

private fun cardinal(deg: Double?): String? {
    if (deg == null) return null
    val dirs = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val normalized = ((deg % 360) + 360) % 360
    val idx = ((normalized / 45.0) + 0.5).toInt() % 8
    return dirs[idx]
}

@Preview(showBackground = true)
@Composable
private fun UtilityScreenLoadingPreview() {
    ReefSafeTheme { LoadingState() }
}

@Preview(showBackground = true)
@Composable
private fun UtilityScreenErrorPreview() {
    ReefSafeTheme { ErrorState("Network unavailable", onRetry = {}) }
}

@Preview(showBackground = true)
@Composable
private fun UtilityScreenSuccessPreview() {
    ReefSafeTheme {
        SuccessState(
            state = UtilityUiState.Success(
                conditions = ReefConditions(
                    seaSurfaceTempC = 26.0,
                    waveHeightM = 0.4,
                    wavePeriodS = 7.2,
                    waveDirectionDeg = 90.0,
                    windSpeedKmh = 12.0,
                    windDirectionDeg = 135.0,
                    visibilityKm = 15.0,
                    uvIndex = 7.5,
                    cloudCoverPct = 40.0,
                    airTempC = 28.5,
                    sunrise = "2026-06-18T06:42",
                    sunset = "2026-06-18T18:15"
                ),
                score = SnorkelScore(85, Verdict.GLASS_CALM)
            ),
            settings = Settings()
        )
    }
}
