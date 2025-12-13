package com.ucb.morfeo.features.settings.presentation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* 
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ucb.morfeo.R
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar
import com.ucb.morfeo.navigation.Screen
import com.ucb.morfeo.navigation.buttonNavBar.presentation.ButtomNavBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showSleepTimeDialog by remember { mutableStateOf(false) }
    var showWakeTimeDialog by remember { mutableStateOf(false) }

    if (showSleepTimeDialog) {
        TimePickerDialog(
            title = stringResource(id = R.string.settings_sleep_time_dialog_title),
            onDismiss = { showSleepTimeDialog = false },
            onConfirm = { timepickerState ->
                showSleepTimeDialog = false
                val selectedTime = String.format("%02d:%02d", timepickerState.hour, timepickerState.minute)
                viewModel.updateSleepTime(timepickerState.hour, timepickerState.minute)
                Toast.makeText(
                    context,
                    context.getString(R.string.settings_sleep_time_saved_message, selectedTime),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
    if (showWakeTimeDialog) {
        TimePickerDialog(
            title = stringResource(id = R.string.settings_wake_time_dialog_title),
            onDismiss = { showWakeTimeDialog = false },
            onConfirm = { timepickerState ->
                showWakeTimeDialog = false
                val selectedTime = String.format("%02d:%02d", timepickerState.hour, timepickerState.minute)
                viewModel.updateWakeupTime(timepickerState.hour, timepickerState.minute)
                Toast.makeText(
                    context,
                    context.getString(R.string.settings_wake_time_saved_message, selectedTime),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopNavBar(true, stringResource(R.string.settings_screen_name), onNavigateTo = onNavigate)
        },
        bottomBar = {
            ButtomNavBar(Screen.Settings.route, onRouteSelected = onNavigate)
        },
        containerColor = colorResource(R.color.firefly)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Correct scrolling modifier
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ... existing code ...
            Text(
                modifier = Modifier.padding(vertical = 16.dp)
                    .align(alignment = Alignment.Start),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                text = "Permisos",
                fontSize = 20.sp
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .clickable { onNavigate(Screen.Permissions.route) },
                colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "Permisos de monitoreo",
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                    Switch(
                        checked = uiState.arePermissionsGranted,
                        onCheckedChange = null, // Navigation is handled by the Card's click
                        modifier = Modifier
                            .width(80.dp)
                            .height(30.dp)
                            .padding(end = 16.dp),
                        colors = SwitchDefaults.colors().copy(
                            checkedThumbColor = colorResource(R.color.dodger_blue),
                            checkedTrackColor = colorResource(R.color.firefly),
                            uncheckedThumbColor = colorResource(R.color.firefly),
                            uncheckedTrackColor = colorResource(R.color.bar_color_day)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.settings_logout_button), color = Color.White) // Ensure text is visible
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (TimePickerState) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = 22,
        initialMinute = 0,
        is24Hour = true,
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                TimePicker(state = timePickerState)
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.dialog_cancel_button))
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { onConfirm(timePickerState) }) {
                        Text(stringResource(id = R.string.dialog_save_button))
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}
