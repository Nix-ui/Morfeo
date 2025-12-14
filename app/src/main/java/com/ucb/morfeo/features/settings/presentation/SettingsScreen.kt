package com.ucb.morfeo.features.settings.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* 
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ucb.morfeo.R
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar
import com.ucb.morfeo.navigation.Screen
import com.ucb.morfeo.navigation.buttonNavBar.presentation.ButtomNavBar
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current

    var showSleepTimeDialog by remember { mutableStateOf(false) }
    var showWakeTimeDialog by remember { mutableStateOf(false) }
    var showSleepGoalDialog by remember { mutableStateOf(false) }
    var showDeleteDataDialog by remember { mutableStateOf(false) }

    val sleepTime by viewModel.sleepTime.collectAsState()
    val wakeupTime by viewModel.wakeUpTime.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()
    val sleepGoal by viewModel.sleepGoal.collectAsState()

    if (showSleepTimeDialog) {
        TimePickerDialog(
            title = stringResource(id = R.string.settings_sleep_time_dialog_title),
            onDismiss = { showSleepTimeDialog = false },
            onConfirm = { timepickerState ->
                showSleepTimeDialog = false
                val selectedTime = String.format("%02d:%02d", timepickerState.hour, timepickerState.minute)
                viewModel.updateSleepTime(timepickerState.hour, timepickerState.minute)
                showToast(context, context.getString(R.string.settings_sleep_time_saved_message, selectedTime))
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
                showToast(context, context.getString(R.string.settings_wake_time_saved_message, selectedTime))
            }
        )
    }

    if (showSleepGoalDialog) {
        SleepGoalDialog(
            initialValue = sleepGoal,
            onDismiss = { showSleepGoalDialog = false },
            onConfirm = { newGoal ->
                showSleepGoalDialog = false
                viewModel.updateSleepGoal(newGoal)
                showToast(context, "Meta de sueño guardada: $newGoal horas")
            }
        )
    }

    if (showDeleteDataDialog) {
        DeleteDataConfirmationDialog(
            onDismiss = { showDeleteDataDialog = false },
            onConfirm = {
                showDeleteDataDialog = false
                viewModel.deleteAllData()
                showToast(context, "Todos los datos locales han sido borrados")
            }
        )
    }

    Scaffold(
        topBar = {
            TopNavBar(
                isBackEnable = true, 
                currentScreenName = stringResource(R.string.settings_screen_name), 
                onNavigateTo = onNavigate,
                onBackScreen = { onNavigate(Screen.Home.route) } 
            )
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp) // Adjusted spacing
        ) {
            SettingsSectionTitle(stringResource(id = R.string.settings_sleep_schedule_title))

            SettingCardButton(
                label = stringResource(id = R.string.settings_sleep_time_label),
                value = String.format("%02d:%02d", sleepTime.first, sleepTime.second),
                onClick = { showSleepTimeDialog = true }
            )

            SettingCardButton(
                label = stringResource(id = R.string.settings_wake_time_label),
                value = String.format("%02d:%02d", wakeupTime.first, wakeupTime.second),
                onClick = { showWakeTimeDialog = true }
            )

            SettingCardButton(
                label = "Meta de horas de sueño",
                value = "$sleepGoal horas",
                onClick = { showSleepGoalDialog = true }
            )

            SettingsSectionTitle(stringResource(id = R.string.settings_notifications_title))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp) // Adjusted padding
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_notifications_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = viewModel::toggleNotifications,
                        colors = SwitchDefaults.colors().copy(
                            checkedThumbColor = colorResource(R.color.dodger_blue),
                            checkedTrackColor = colorResource(R.color.firefly),
                            uncheckedThumbColor = colorResource(R.color.firefly),
                            uncheckedTrackColor = colorResource(R.color.bar_color_day)
                        )
                    )
                }
            }

            SettingsSectionTitle(text = "Permisos")

            SettingCardNavigation(
                label = "Permisos de monitoreo",
                value = if (permissionsGranted) "Activado" else "Desactivado",
                valueColor = if (permissionsGranted) colorResource(id = R.color.bar_color_day) else Color.Gray,
                onClick = { onNavigate(Screen.Permissions.route) }
            )

            SettingsSectionTitle(stringResource(id = R.string.settings_appearance_title))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_dark_mode_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                    Switch(
                        checked = themeMode == 1,
                        onCheckedChange = { viewModel.updateThemeMode(if (it) 1 else 0) },
                        colors = SwitchDefaults.colors().copy(
                            checkedThumbColor = colorResource(R.color.dodger_blue),
                            checkedTrackColor = colorResource(R.color.firefly),
                            uncheckedThumbColor = colorResource(R.color.firefly),
                            uncheckedTrackColor = colorResource(R.color.bar_color_day)
                        )
                    )
                }
            }

            SettingsSectionTitle(text = "Privacidad")

            SettingCardNavigation(
                label = "Exportar mis datos",
                onClick = { onNavigate(Screen.ExportData.route) }
            )

            SettingCardNavigation(
                label = "Borrar todos los datos locales",
                onClick = { showDeleteDataDialog = true }
            )

            SettingCardNavigation(
                label = "Política de Privacidad",
                onClick = { onNavigate(Screen.PrivacyPolicy.route) }
            )

            SettingsSectionTitle(text = "Información")

            SettingCardNavigation(
                label = stringResource(id = R.string.about_morfeo_button),
                onClick = { onNavigate(Screen.About.route) }
            )

            SettingCardNavigation(
                label = stringResource(id = R.string.help_center_button),
                onClick = { onNavigate(Screen.HelpCenter.route) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.settings_logout_button), color = Color.White)
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 4.dp),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        text = text,
        fontSize = 20.sp
    )
}

@Composable
private fun SettingCardButton(label: String, value: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.bar_color_day),
            )
        }
    }
}

@Composable
fun SettingCardNavigation(
    label: String, 
    value: String? = null, 
    valueColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value != null) {
                    val color = if (valueColor != Color.Unspecified) valueColor else LocalContentColor.current
                    Text(
                        text = value,
                        color = color,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = label,
                    tint = Color.White
                )
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

@Composable
fun SleepGoalDialog(
    initialValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedValue by remember { mutableFloatStateOf(initialValue.toFloat()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Meta de Horas de Sueño", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "${selectedValue.roundToInt()} horas",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Slider(
                    value = selectedValue,
                    onValueChange = { selectedValue = it },
                    valueRange = 5f..10f,
                    steps = 4
                )

                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.dialog_cancel_button))
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { onConfirm(selectedValue.roundToInt()) }) {
                        Text(stringResource(id = R.string.dialog_save_button))
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteDataConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Borrar datos") },
        text = { Text("¿Estás seguro de que quieres borrar todos tus datos locales? Esta acción no se puede deshacer.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Borrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}
