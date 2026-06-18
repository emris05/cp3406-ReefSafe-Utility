package au.edu.jcu.reefsafe.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import au.edu.jcu.reefsafe.domain.Activity
import au.edu.jcu.reefsafe.domain.Locations
import au.edu.jcu.reefsafe.domain.NamedLocation
import au.edu.jcu.reefsafe.domain.Settings
import au.edu.jcu.reefsafe.domain.Units
import au.edu.jcu.reefsafe.ui.theme.ReefSafeTheme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsState()
    SettingsContent(
        settings = settings,
        onLocation = viewModel::setLocation,
        onActivity = viewModel::setActivity,
        onUnits = viewModel::setUnits,
        onShowWaveCard = viewModel::setShowWaveCard,
        onUse24HourTime = viewModel::setUse24HourTime
    )
}

@Composable
private fun SettingsContent(
    settings: Settings,
    onLocation: (NamedLocation) -> Unit,
    onActivity: (Activity) -> Unit,
    onUnits: (Units) -> Unit,
    onShowWaveCard: (Boolean) -> Unit,
    onUse24HourTime: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Section(title = "Location") {
            Locations.ALL.forEach { loc ->
                LocationRow(
                    location = loc,
                    selected = settings.location == loc,
                    onSelect = { onLocation(loc) }
                )
            }
        }

        Section(title = "Activity") {
            SegmentedChoice(
                options = Activity.entries.toList(),
                labels = Activity.entries.map { it.label() },
                selected = settings.activity,
                onSelect = onActivity
            )
        }

        Section(title = "Units") {
            SegmentedChoice(
                options = Units.entries.toList(),
                labels = Units.entries.map { it.label() },
                selected = settings.units,
                onSelect = onUnits
            )
        }

        Section(title = "Display") {
            ToggleRow(
                label = "Show wave card",
                checked = settings.showWaveCard,
                onCheckedChange = onShowWaveCard
            )
            ToggleRow(
                label = "Use 24-hour time",
                checked = settings.use24HourTime,
                onCheckedChange = onUse24HourTime
            )
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@Composable
private fun LocationRow(location: NamedLocation, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Spacer(Modifier.width(8.dp))
        Text(location.name, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun <T> SegmentedChoice(
    options: List<T>,
    labels: List<String>,
    selected: T,
    onSelect: (T) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                )
            ) {
                Text(labels[index])
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

private fun Activity.label(): String = when (this) {
    Activity.SNORKEL -> "Snorkel"
    Activity.FREE_DIVE -> "Free dive"
    Activity.SCUBA -> "Scuba"
}

private fun Units.label(): String = when (this) {
    Units.METRIC -> "Metric"
    Units.IMPERIAL -> "Imperial"
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    ReefSafeTheme {
        SettingsContent(
            settings = Settings(
                location = Locations.MAGNETIC_ISLAND,
                activity = Activity.SCUBA,
                units = Units.IMPERIAL,
                showWaveCard = false,
                use24HourTime = false
            ),
            onLocation = {},
            onActivity = {},
            onUnits = {},
            onShowWaveCard = {},
            onUse24HourTime = {}
        )
    }
}
