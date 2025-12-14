package com.ucb.morfeo.features.settings.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.morfeo.R
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar

@Composable
fun AboutScreen(
    onNavigateUp: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopNavBar(
                isBackEnable = true,
                currentScreenName = stringResource(id = R.string.about_screen_title),
                onBackScreen = onNavigateUp
            )
        },
        containerColor = colorResource(R.color.firefly)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.morfeo),
                contentDescription = stringResource(id = R.string.morfeo_logo_description),
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Morfeo v1.0",
                fontSize = 28.sp,
                color = Color.White
            )
            Text(
                text = "Dios de los sueños en la mitología griega",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(32.dp))

            InfoCard(title = "Nuestra misión", text = "Morfeo te ayuda a entender y mejorar tus patrones de sueño mediante el análisis de tu uso del dispositivo, sin invadir tu privacidad.")
            Spacer(modifier = Modifier.height(16.dp))
            InfoCard(title = "Cómo funciona", text = "Utilizamos algoritmos que detectan periodos de inactividad prolongada para estimar tus horas de sueño.")
            Spacer(modifier = Modifier.height(16.dp))
            InfoCard(title = "Privacidad", text = "Todos los datos se procesan localmente en tu dispositivo.")
        }
    }
}

@Composable
private fun InfoCard(title: String, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview
@Composable
fun PreviewAboutScreen() {
    AboutScreen()
}
