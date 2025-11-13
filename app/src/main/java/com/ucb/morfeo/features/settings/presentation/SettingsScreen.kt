package com.ucb.morfeo.features.settings.presentation


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.ucb.morfeo.R
import com.ucb.morfeo.navigation.Screen
import com.ucb.morfeo.navigation.buttonNavBar.presentation.ButtomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    var showSleepTimeDialog by remember { mutableStateOf(false) }
    var showWakeTimeDialog by remember { mutableStateOf(false) }

    val sleepTime by viewModel.sleepTime.collectAsState()
    val wakeupTime by viewModel.wakeUpTime.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    var sleepTimeInput by remember { mutableStateOf(sleepTime ?: "") }
    var wakeupTimeInput by remember { mutableStateOf(wakeupTime ?: "") }

    if(showSleepTimeDialog){
        TimePickerDialog(
            title = "Seleccionar hora de dormir",
            onDismiss = { showSleepTimeDialog = false },
            onConfirm = {timepickerState ->
                showSleepTimeDialog = false
                val selectedTime = String.format("%02d:%02d", timepickerState.hour, timepickerState.minute)
                Toast.makeText(context, "Hora de dormir guardada: $selectedTime", Toast.LENGTH_SHORT).show()
            }
        )
    }
    if(showWakeTimeDialog){
        TimePickerDialog(
            title = "Seleccionar hora de despertar",
            onDismiss = { showWakeTimeDialog = false },
            onConfirm = {timepickerState ->
                showWakeTimeDialog = false
                val selectedTime = String.format("%02d:%02d", timepickerState.hour, timepickerState.minute)
                Toast.makeText(context, "Hora de dormir guardada: $selectedTime", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopNavBar(true, stringResource(R.string.settings_screen_name),onNavigate)
        },
        bottomBar = {
            ButtomNavBar(Screen.Settings.route,onNavigate)
        },
        containerColor = colorResource(R.color.firefly),
        modifier = Modifier.scrollable(
            rememberScrollState(),
            orientation = Orientation.Vertical,
            true)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                modifier = Modifier.padding(16.dp)
                    .align(alignment = Alignment.Start),
                style= MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                text = "Definir Horario de sueño y despertar",
                fontSize = 25.sp
            )
            Card(
                modifier = Modifier.fillMaxWidth()
                    .height(65.dp),
                colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight()
                ){

                    Text(
                        modifier = Modifier.padding(start=16.dp) ,
                        style= MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = "Definir la hora de dormir",
                        fontSize = 15.sp
                    )
                    Button(
                        onClick = { showSleepTimeDialog = true },
                        contentPadding = ButtonDefaults.TextButtonContentPadding,
                        modifier= Modifier.width(80.dp)
                            .height(30.dp)
                            .padding(end=16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.firefly),
                        )
                    ) {
                        Text(
                            style= MaterialTheme.typography.labelSmall,
                            color = colorResource(R.color.bar_color_day),
                            text = "Definir",
                            fontSize = 9.sp
                        )
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth()
                    .height(65.dp),
                colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight()
                ){

                    Text(
                        modifier = Modifier.padding(start=16.dp) ,
                        style= MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = "Definir la hora para despertar",
                        fontSize = 15.sp
                    )
                    Button(
                        onClick = { showWakeTimeDialog = true },
                        contentPadding = ButtonDefaults.TextButtonContentPadding,
                        modifier= Modifier.width(80.dp)
                            .height(30.dp)
                            .padding(end=16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.firefly),
                        )
                    ) {
                        Text(
                            style= MaterialTheme.typography.labelSmall,
                            color = colorResource(R.color.bar_color_day),
                            text = "Definir",
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Text(
                modifier = Modifier.padding(vertical=16.dp)
                    .align(alignment = Alignment.Start),
                style= MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                text = "Notificaciones",
                fontSize = 20.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth()
                    .height(65.dp),
                colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Text(
                        text="Notificaciones",
                        modifier = Modifier.padding(start=16.dp) ,
                        style= MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            viewModel.toggleNotifications(it)
                        },
                        modifier = Modifier.width(80.dp)
                            .height(30.dp)
                            .padding(end=16.dp),
                        colors = SwitchDefaults.colors().copy(
                            checkedThumbColor = colorResource(R.color.dodger_blue),
                            checkedTrackColor = colorResource(R.color.firefly),
                            uncheckedThumbColor = colorResource(R.color.firefly),
                            uncheckedTrackColor = colorResource(R.color.bar_color_day)
                        )
                    )
                }
            }
            Text(
                modifier = Modifier.padding(vertical=16.dp)
                    .align(alignment = Alignment.Start),
                style= MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                text = "Apariencia",
                fontSize = 20.sp
            )
            Card(
                modifier = Modifier.fillMaxWidth()
                    .height(65.dp),
                colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Text(
                        text="Modo oscuro",
                        modifier = Modifier.padding(start=16.dp) ,
                        style= MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                    Switch(
                        checked = themeMode == 1,
                        onCheckedChange = {
                            viewModel.updateThemeMode(if (it) 1 else 0)
                        },
                        modifier = Modifier.width(80.dp)
                            .height(30.dp)
                            .padding(end=16.dp),
                        colors = SwitchDefaults.colors().copy(
                            checkedThumbColor = colorResource(R.color.dodger_blue),
                            checkedTrackColor = colorResource(R.color.firefly),
                            uncheckedThumbColor = colorResource(R.color.firefly),
                            uncheckedTrackColor = colorResource(R.color.bar_color_day)
                        )
                    )
                }
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
                        Text("Cancelar")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { onConfirm(timePickerState) }) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}


@Preview()
@Composable()
fun PreviewSettingsScreen(){
    SettingsScreen()
}