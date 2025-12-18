package com.ucb.morfeo.features.sleepanalysis.presentaion

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.patrykandpatrick.vico.core.extension.setFieldValue
import com.ucb.morfeo.R
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar
import com.ucb.morfeo.features.settings.presentation.SettingsViewModel
import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepAnalysis
import com.ucb.morfeo.features.sleepanalysis.domain.model.SleepSession
import com.ucb.morfeo.navigation.Screen
import com.ucb.morfeo.navigation.buttonNavBar.presentation.ButtomNavBar
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SleepTrackingScreen(
    viewModel: SleepTrackingViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    userEmail: String = "usuario@ejemplo.com",
    onNavigate: (String)-> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val recordingState by viewModel.recordingState.collectAsState()
    LaunchedEffect(Unit) {
        // Cargar historial al iniciar
        viewModel.loadSleepHistory(userEmail)
    }
    Scaffold(
        topBar = {
            TopNavBar(
                true,
                "Analysis",
                onNavigateTo = onNavigate,
                onBackScreen = { onNavigate(Screen.Home.route)},
            )
        },
        bottomBar = { ButtomNavBar(
            Screen.Analysis.route,
            onNavigate
        ) },
        containerColor = colorResource(R.color.firefly),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // Contenido principal basado en el estado
            when (recordingState) {
                is SleepTrackingViewModel.RecordingState.Idle -> {
                    IdleStateView(
                        onStartSleep = { wakeTime ->
                            viewModel.startSleepTracking(userEmail, wakeTime)
                        },
                        wakeTimeUp = settingsViewModel.wakeUpTime.collectAsState()?.value ?: null,
                        sleepTime = settingsViewModel.sleepTime.collectAsState()?.value ?: null
                    )
                }

                is SleepTrackingViewModel.RecordingState.Starting -> {
                    StartingStateView()
                }

                is SleepTrackingViewModel.RecordingState.Recording -> {
                    val session = (recordingState as SleepTrackingViewModel.RecordingState.Recording).session

                    RecordingStateView(
                        session = session,
                        onStop = { viewModel.stopAnalyzeSleep() },
                        onCancel = { viewModel.cancelCurrentRecording() }
                    )
                }

                is SleepTrackingViewModel.RecordingState.Stopping -> {
                    StoppingStateView()
                }

                is SleepTrackingViewModel.RecordingState.Completed -> {
                    val analysis = (recordingState as SleepTrackingViewModel.RecordingState.Completed).analysis
                    CompletedStateView(analysis = analysis)
                }

                is SleepTrackingViewModel.RecordingState.Cancelling -> {
                    CancellingStateView()
                }

                is SleepTrackingViewModel.RecordingState.Error -> {
                    val error = (recordingState as SleepTrackingViewModel.RecordingState.Error).message
                    ErrorStateView(error = error) {
                        viewModel.clearError()
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Historial de sueño (solo si no está grabando)
            if (recordingState is SleepTrackingViewModel.RecordingState.Idle ||
                recordingState is SleepTrackingViewModel.RecordingState.Completed) {
                SleepHistorySection(
                    history = uiState.sleepHistory,
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}

@Composable
fun SleepTrackingHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Nightlight,
                contentDescription = "Sleep Tracking",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Seguimiento del Sueño",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Analiza tus patrones de sueño con audio",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun IdleStateView(
    wakeTimeUp: Pair<Int,Int>?,
    sleepTime: Pair<Int,Int>?,
    onStartSleep: (LocalDateTime) -> Unit
) {
    var wakeTime by remember {
        mutableStateOf(
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }
    if(wakeTimeUp != null){
        wakeTime = LocalDateTime(
            year = wakeTime.year,
            month = wakeTime.month,
            dayOfMonth = wakeTime.dayOfMonth,
            hour = wakeTimeUp.first,
            minute = wakeTimeUp.second,
            second = 0,
            nanosecond = 0
        )
    }
    if(wakeTimeUp != null && sleepTime!=null){
        if(wakeTimeUp.first < sleepTime.first || (wakeTimeUp.first == sleepTime.first && wakeTimeUp.second < sleepTime.second)){
            val todayTime = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val newDate = todayTime.toInstant(TimeZone.currentSystemDefault())
                .plus(value = 24, unit = DateTimeUnit.HOUR)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            wakeTime = LocalDateTime(
                year = newDate.year,
                month = newDate.month,
                dayOfMonth = newDate.dayOfMonth,
                hour = wakeTimeUp.first,
                minute = wakeTimeUp.second,
                second = 0,
                nanosecond = 0
            )
        }
    }
    var showTimePicker by remember { mutableStateOf(false) }
    val permisionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {permissions ->
        permissions.forEach { (permission, isGranted) ->
        }
    }
    LaunchedEffect(Unit) {
        permisionLauncher.launch(
            arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
        )
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Listo para dormir?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Presiona el botón para comenzar a grabar tu sueño",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Hora de despertar estimada
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showTimePicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hora de despertar",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = formatTimeForDisplay(wakeTime),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Cambiar hora",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = {
                    onStartSleep(wakeTime)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "COMENZAR A GRABAR SUEÑO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "La grabación se detendrá automáticamente a la hora seleccionada",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun StartingStateView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Preparando grabación...",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Configurando micrófono y creando archivos",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun RecordingStateView(
    session: SleepSession,
    onStop: () -> Unit,
    onCancel: () -> Unit
) {
    // Contador de tiempo transcurrido
    val elapsedTime = remember { mutableStateOf(0L) }

    LaunchedEffect(key1 = true) {
        while (true) {
            delay(1000) // Actualizar cada segundo
            elapsedTime.value = System.currentTimeMillis() - session.startTimeStamp
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A237E) // Azul oscuro para indicar noche
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
                .verticalScroll(rememberScrollState(),true),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Indicador de grabación activa
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Círculo exterior pulsante
                AnimatedRecordingPulse()

                // Icono de micrófono
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Grabando",
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "GRABANDO SUEÑO",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu sueño está siendo analizado",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tiempo transcurrido
            RecordingTimer(elapsedTime = elapsedTime.value)

            Spacer(modifier = Modifier.height(32.dp))

            // Información de la sesión
            RecordingInfoCard(session = session)

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onStop,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "DETENER",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(text = "CANCELAR")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nota: El audio se eliminará después del análisis para proteger tu privacidad",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun AnimatedRecordingPulse() {
    var scale by remember { mutableStateOf(1f) }

    LaunchedEffect(key1 = true) {
        while (true) {
            scale = 1.2f
            delay(1000)
            scale = 1f
            delay(1000)
        }
    }

    androidx.compose.animation.AnimatedContent(
        targetState = scale,
        label = "pulse"
    ) { targetScale ->
        Box(
            modifier = Modifier
                .size(100.dp)
                .animateContentSize()
        ) {
            androidx.compose.foundation.Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                drawCircle(
                    color = Color.Red.copy(alpha = 0.3f),
                    radius = size.minDimension * 0.45f * targetScale
                )

                drawCircle(
                    color = Color.Red.copy(alpha = 0.6f),
                    radius = size.minDimension * 0.35f
                )
            }
        }
    }
}

@Composable
fun RecordingTimer(elapsedTime: Long) {
    val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tiempo transcurrido",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 48.sp
            )
        }
    }
}

@Composable
fun RecordingInfoCard(session: SleepSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            RecordingInfoItem(
                icon = Icons.Default.Schedule,
                title = "Inicio",
                value = formatDateTime(session.bedTime)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.White.copy(alpha = 0.2f)
            )

            RecordingInfoItem(
                icon = Icons.Default.Alarm,
                title = "Despertar programado",
                value = formatDateTime(session.scheduledWakeTime)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.White.copy(alpha = 0.2f)
            )

            RecordingInfoItem(
                icon = Icons.Default.Person,
                title = "Usuario",
                value = session.userEmail
            )
        }
    }
}

@Composable
fun RecordingInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun StoppingStateView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Analizando sueño...",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Procesando audio y generando reporte",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progreso simulado con animación
            AnimatedProgressIndicator()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Esto puede tomar unos minutos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AnimatedProgressIndicator() {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = true) {
        while (progress < 0.95f) {
            delay(300)
            progress += 0.05f
        }
    }

    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primaryContainer
    )
}

@Composable
fun CompletedStateView(
    analysis: SleepAnalysis
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.ic_launcher_background)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Análisis completado",
                modifier = Modifier.size(64.dp),
                tint = colorResource(R.color.dodger_blue)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡ANÁLISIS COMPLETADO!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu sueño ha sido analizado exitosamente",
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(R.color.white)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Score Card
            ScoreCard(score = analysis.sleepScore)

            Spacer(modifier = Modifier.height(24.dp))

            // Métricas principales
            SleepMetricsGrid(analysis = analysis)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Navegar a detalles */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "VER REPORTE COMPLETO",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { /* Volver a inicio */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "NUEVO ANÁLISIS")
            }
        }
    }
}

@Composable
fun ScoreCard(score: Int) {
    val color = when {
        score >= 80 -> Color(0xFF4CAF50) // Verde
        score >= 60 -> Color(0xFF2196F3) // Azul
        score >= 40 -> Color(0xFFFF9800) // Naranja
        else -> Color(0xFFF44336) // Rojo
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
                .verticalScroll(rememberScrollState(),true)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "PUNTAJE DE SUEÑO",
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$score/100",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    score >= 80 -> "Excelente calidad de sueño"
                    score >= 60 -> "Buena calidad de sueño"
                    score >= 40 -> "Sueño regular"
                    else -> "Sueño pobre - Considera consultar"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
    }
}

@Composable
fun SleepMetricsGrid(analysis:SleepAnalysis) {
    val metrics = listOf(
        Triple("Sueño profundo", "${analysis.deepSleepPercentage}%", Icons.Default.Nightlight),
        Triple("Sueño REM", "${analysis.remSleepPercentage}%", Icons.Default.AddReaction),
        Triple("Sueño ligero", "${analysis.lightSleepPercentage}%", Icons.Default.LightMode),
        Triple("Tiempo despierto", "${analysis.awakeDuration / (60 * 1000)} min", Icons.Default.AccessTime),
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState(),true)
    ) {
        metrics.chunked(2).forEach { rowMetrics ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowMetrics.forEach { (title, value, icon) ->
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = title,
                        value = value,
                        icon = icon
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun CancellingStateView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Cancelando grabación...",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Eliminando archivos temporales",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun ErrorStateView(
    error: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(text = "ENTENDIDO")
            }
        }
    }
}

@Composable
fun SleepHistorySection(
    history: List<SleepAnalysis>,
    isLoading: Boolean
) {
    if (isLoading) {
        CircularProgressIndicator()
        return
    }

    if (history.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Sin historial",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Aún no hay análisis de sueño",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Comienza tu primer seguimiento de sueño",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historial de Sueño",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${history.size} análisis",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items=history.take(5)) { analysis ->
                    SleepHistoryItem(analysis = analysis)
                }
            }
        }
    }
}

@Composable
fun SleepHistoryItem(
    analysis: SleepAnalysis
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Navegar a detalles */ }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score indicator
            Box(
                modifier = Modifier.size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    drawCircle(
                        color = getScoreColor(analysis.sleepScore),
                        radius = size.minDimension * 0.4f
                    )
                }

                Text(
                    text = "${analysis.sleepScore}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = analysis.date,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "${analysis.sleepDuration / (60 * 60 * 1000)}h ${(analysis.sleepDuration % (60 * 60 * 1000)) / (60 * 1000)}m",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver detalles",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Funciones auxiliares
private fun formatDateTime(dateTime: LocalDateTime): String {
    return "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year} " +
            "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}

private fun formatTimeForDisplay(dateTime: LocalDateTime): String {
    return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}

private fun getScoreColor(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF4CAF50)
        score >= 60 -> Color(0xFF2196F3)
        score >= 40 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}