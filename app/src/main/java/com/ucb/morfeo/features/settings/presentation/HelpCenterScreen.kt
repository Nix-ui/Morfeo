package com.ucb.morfeo.features.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.R
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar

@Composable
fun HelpCenterScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopNavBar(
                isBackEnable = true,
                currentScreenName = "Centro de Ayuda",
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
                .verticalScroll(rememberScrollState())
        ) {
            HelpItem(
                question = "¿Cómo calcula Morfeo mis horas de sueño?",
                answer = "Analizamos los periodos de inactividad prolongada durante la noche."
            )
            Spacer(modifier = Modifier.height(16.dp))
            HelpItem(
                question = "¿Qué precisión tiene el análisis?",
                answer = "Aproximadamente ±30 minutos comparado con wearables especializados."
            )
            Spacer(modifier = Modifier.height(16.dp))
            HelpItem(
                question = "¿Morfeo afecta la batería?",
                answer = "El consumo es mínimo al usar APIs nativas."
            )
        }
    }
}

@Composable
private fun HelpItem(question: String, answer: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.cloud_burst))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview
@Composable
fun PreviewHelpCenterScreen() {
    HelpCenterScreen(onBack = {})
}
