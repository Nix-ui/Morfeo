package com.ucb.morfeo.features.tips.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.features.tips.domain.model.Tip
import com.ucb.morfeo.features.tips.domain.model.TipPriority
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsScreen(
    onGoToAddSleep: () -> Unit,
    viewModel: TipsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Consejos") }) }
    ) { padding ->
        when (state) {
            is TipsUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularProgressIndicator() }
            }

            is TipsUiState.Error -> {
                val msg = (state as TipsUiState.Error).message
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Error", style = MaterialTheme.typography.titleMedium)
                    Text(msg, color = MaterialTheme.colorScheme.error)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { viewModel.load() }) { Text("Reintentar") }
                        Button(onClick = onGoToAddSleep) { Text("Registrar sueño") }
                    }
                }
            }

            is TipsUiState.Success -> {
                val s = state as TipsUiState.Success
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Basado en tus datos", style = MaterialTheme.typography.titleMedium)
                                Text("Fecha: ${s.date}")
                                if (s.today == null) {
                                    Spacer(Modifier.height(6.dp))
                                    Button(onClick = onGoToAddSleep, modifier = Modifier.fillMaxWidth()) {
                                        Text("Registrar sueño")
                                    }
                                }
                            }
                        }
                    }

                    items(s.tips) { tip ->
                        TipCard(tip)
                    }
                }
            }
        }
    }
}

@Composable
private fun TipCard(tip: Tip) {
    val badge = when (tip.priority) {
        TipPriority.HIGH -> "Prioridad alta"
        TipPriority.MEDIUM -> "Prioridad media"
        TipPriority.LOW -> "Prioridad baja"
    }

    Card(
        colors = CardDefaults.cardColors()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(tip.title, style = MaterialTheme.typography.titleMedium)
            Text(badge, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(tip.message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
