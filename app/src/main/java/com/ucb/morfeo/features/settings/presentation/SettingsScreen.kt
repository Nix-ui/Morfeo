package com.ucb.morfeo.features.settings.presentation


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()

    val sleepTime by viewModel.sleepTime.collectAsState()
    val wakeupTime by viewModel.wakeUpTime.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    var sleepTimeInput by remember { mutableStateOf(sleepTime ?: "") }
    var wakeupTimeInput by remember { mutableStateOf(wakeupTime ?: "") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configuración de sueño") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 🔹 Hora de dormir
            OutlinedTextField(
                value = sleepTimeInput,
                onValueChange = { sleepTimeInput = it },
                label = { Text("Hora de dormir (ej: 22:30)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    scope.launch { viewModel.updateSleepTime(sleepTimeInput) }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar hora de dormir")
            }

            // 🔹 Hora de despertar
            OutlinedTextField(
                value = wakeupTimeInput,
                onValueChange = { wakeupTimeInput = it },
                label = { Text("Hora de despertar (ej: 07:00)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    scope.launch { viewModel.updateWakeupTime(wakeupTimeInput) }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar hora de despertar")
            }

            // 🔹 Notificaciones
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notificaciones activadas")
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        viewModel.toggleNotifications(it)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // 🔹 Tema
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modo oscuro")
                Switch(
                    checked = themeMode == 1,
                    onCheckedChange = {
                        viewModel.updateThemeMode(if (it) 1 else 0)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
