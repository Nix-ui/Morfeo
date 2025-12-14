package com.ucb.morfeo.features.settings.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.R
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExportDataScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current

    var dateRangeOption by remember { mutableStateOf("Última semana") }
    val dateRanges = listOf("Última semana", "Último mes", "Desde el principio")

    val dataToExportOptions = remember {
        mutableStateMapOf(
            "Horas de sueño" to true,
            "Puntuación de sueño" to true,
            "Consistencia" to false
        )
    }

    Scaffold(
        topBar = {
            TopNavBar(
                isBackEnable = true,
                currentScreenName = "Exportar Datos",
                onBackScreen = onBack
            )
        },
        containerColor = colorResource(R.color.firefly)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Selecciona el rango de fechas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            dateRanges.forEach {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable { dateRangeOption = it }
                ) {
                    RadioButton(selected = dateRangeOption == it, onClick = { dateRangeOption = it })
                    Text(text = it, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Selecciona los datos a exportar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            dataToExportOptions.keys.forEach { key ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable { dataToExportOptions[key] = !dataToExportOptions.getValue(key) }
                ) {
                    Checkbox(checked = dataToExportOptions.getValue(key), onCheckedChange = { dataToExportOptions[key] = it })
                    Text(text = key, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val selectedData = dataToExportOptions.filter { it.value }.keys.joinToString()
                    // En una implementación real, pasarías estas opciones al viewModel
                    // viewModel.exportData(dateRangeOption, selectedData)
                    showToast(context, "Iniciando exportación para $dateRangeOption: $selectedData")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = dataToExportOptions.values.any { it }
            ) {
                Text(text = "Confirmar Exportación")
            }
        }
    }
}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Preview
@Composable
fun PreviewExportDataScreen() {
    ExportDataScreen(onBack = {})
}
