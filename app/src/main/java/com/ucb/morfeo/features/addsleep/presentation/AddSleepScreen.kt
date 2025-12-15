package com.ucb.morfeo.features.addsleep.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSleepScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddSleepViewModel = koinViewModel()
) {
    val form by viewModel.form.collectAsState()
    val ui by viewModel.ui.collectAsState()

    LaunchedEffect(ui) {
        if (ui is AddSleepUiState.Saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar sueño") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("Fecha: ${form.date}")

            OutlinedTextField(
                value = form.bedHHmm,
                onValueChange = viewModel::setBed,
                label = { Text("Hora de dormir (HH:mm)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.wakeHHmm,
                onValueChange = viewModel::setWake,
                label = { Text("Hora de despertar (HH:mm)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.score,
                onValueChange = viewModel::setScore,
                label = { Text("Sleep score (0-100)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.awakeMinutes,
                onValueChange = viewModel::setAwake,
                label = { Text("Minutos despierto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = form.deep,
                    onValueChange = viewModel::setDeep,
                    label = { Text("Deep %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = form.rem,
                    onValueChange = viewModel::setRem,
                    label = { Text("REM %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            when (ui) {
                is AddSleepUiState.Error -> {
                    Text(
                        text = (ui as AddSleepUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is AddSleepUiState.Saving -> {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
                else -> Unit
            }

            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth(),
                enabled = ui !is AddSleepUiState.Saving
            ) {
                Text("Guardar")
            }
        }
    }
}
