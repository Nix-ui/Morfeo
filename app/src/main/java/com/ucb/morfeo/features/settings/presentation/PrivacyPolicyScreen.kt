package com.ucb.morfeo.features.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.R
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopNavBar(
                isBackEnable = true,
                currentScreenName = "Política de Privacidad",
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
            PolicySection(
                title = "1. Información que Recopilamos",
                content = "Morfeo está diseñado para respetar tu privacidad. No recopilamos información personal identificable. Toda la información sobre tus patrones de sueño se genera y almacena exclusivamente en tu dispositivo."
            )
            PolicySection(
                title = "2. Cómo Usamos tu Información",
                content = "La información generada se utiliza únicamente para mostrarte tus estadísticas de sueño, calcular tu consistencia y ofrecerte análisis dentro de la aplicación. Estos datos no se comparten con terceros y no salen de tu dispositivo, a menos que tú decidas exportarlos manualmente."
            )
            PolicySection(
                title = "3. Permisos de la Aplicación",
                content = "Para estimar tus horas de sueño, Morfeo requiere acceso a los datos de uso de aplicaciones (Usage Stats API). Este permiso nos permite detectar periodos de inactividad, pero no monitoriza el contenido de las aplicaciones que usas."
            )
            PolicySection(
                title = "4. Seguridad de los Datos",
                content = "La seguridad de tus datos es una prioridad. Al mantener toda la información en tu dispositivo, reducimos significativamente los riesgos de accesos no autorizados. Tú tienes el control total para borrar todos los datos locales desde el menú de ajustes en cualquier momento."
            )
            PolicySection(
                title = "5. Cambios a esta Política",
                content = "Podemos actualizar nuestra Política de Privacidad de vez en cuando. Te notificaremos de cualquier cambio publicando la nueva política en esta página."
            )
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun PreviewPrivacyPolicyScreen() {
    PrivacyPolicyScreen(onBack = {})
}
