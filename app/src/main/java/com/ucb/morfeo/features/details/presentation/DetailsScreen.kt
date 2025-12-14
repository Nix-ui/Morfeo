@file:OptIn(ExperimentalMaterial3Api::class)

package com.ucb.morfeo.features.details.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.features.core.database.entity.SleepCore
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsScreen(
    dateStr: String,
    onBack: () -> Unit,
    viewModel: DailyDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val date = remember(dateStr) { parseKotlinLocalDate(dateStr) }

    LaunchedEffect(dateStr) {
        if (date != null) viewModel.load(date)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del día") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text(
                    text = "Fecha: $dateStr",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (date == null) {
                item {
                    Card {
                        Column(Modifier.padding(16.dp)) {
                            Text("Fecha inválida", fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(6.dp))
                            Text("No se pudo interpretar: $dateStr")
                        }
                    }
                }
                return@LazyColumn
            }

            when (val s = state) {
                is DailyDetailsState.Loading -> {
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) { CircularProgressIndicator() }
                    }
                }

                is DailyDetailsState.Error -> {
                    item {
                        Card {
                            Column(Modifier.padding(16.dp)) {
                                Text("Error", fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(6.dp))
                                Text(s.message)
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { viewModel.load(date) }) { Text("Reintentar") }
                            }
                        }
                    }
                }

                is DailyDetailsState.Empty -> {
                    item {
                        Card {
                            Column(Modifier.padding(16.dp)) {
                                Text("Sin datos de sueño para este día", fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(6.dp))
                                Text("Asegúrate de haber registrado/recibido datos para la fecha seleccionada.")
                            }
                        }
                    }
                }

                is DailyDetailsState.Success -> {
                    val sleep = s.sleep

                    item { SummaryCard(sleep) }

                    item {
                        SleepPhasesCard(
                            deep = sleep.deepSleepPercentage,
                            rem = sleep.remSleepPercentage,
                            light = sleep.lightSleepPercentage
                        )
                    }

                    // Por ahora dejamos metas en null (pendiente)
                    item {
                        GoalVsRealCard(
                            sleep = sleep,
                            goalSleepTimeHHmm = null,
                            goalWakeTimeHHmm = null
                        )
                    }

                    item {
                        InsightCard(
                            sleep = sleep,
                            goalSleepHHmm = null,
                            goalWakeHHmm = null
                        )
                    }
                }
            }
        }
    }
}

/* ---------- UI CARDS ---------- */

@Composable
private fun SummaryCard(s: SleepCore) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Sleep Score", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${s.sleepScore}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                }
                AssistChip(onClick = {}, label = { Text(formatDurationMinutes(s.sleepDuration)) })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatMini(title = "Duración", value = formatDurationMinutes(s.sleepDuration), modifier = Modifier.weight(1f))
                StatMini(title = "Despierto", value = formatDurationMinutes(s.awakeDuration), modifier = Modifier.weight(1f))
            }

            HorizontalDivider()

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Te dormiste: ${formatTime(s.bedTime)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Te levantaste: ${formatTime(s.wakeTime)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun StatMini(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SleepPhasesCard(deep: Float, rem: Float, light: Float) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Fases del sueño", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            PhaseRow("Deep", deep)
            PhaseRow("REM", rem)
            PhaseRow("Light", light)
        }
    }
}

@Composable
private fun PhaseRow(label: String, percent: Float) {
    val p = (percent / 100f).coerceIn(0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${percent.toInt()}%")
        }
        LinearProgressIndicator(progress = { p }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun GoalVsRealCard(
    sleep: SleepCore,
    goalSleepTimeHHmm: Long? = null,
    goalWakeTimeHHmm: Long? = null
) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Objetivo vs Real", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            GoalRow(title = "Dormir", goalHHmm = goalSleepTimeHHmm, real = sleep.bedTime)
            GoalRow(title = "Despertar", goalHHmm = goalWakeTimeHHmm, real = sleep.wakeTime)

            if (goalSleepTimeHHmm == null || goalWakeTimeHHmm == null) {
                Text(
                    "Tip: configura tus horarios en Settings para ver esta comparación.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GoalRow(title: String, goalHHmm: Long?, real: LocalDateTime) {
    val realStr = formatTime(real)
    if (goalHHmm == null) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Real: $realStr")
        }
        return
    }

    val goalStr = formatHHmm(goalHHmm)
    val diffMin = diffMinutes(goalHHmm, real)

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Obj: $goalStr  •  Real: $realStr  •  ${formatSignedMinutes(diffMin)}")
    }
}

@Composable
private fun InsightCard(sleep: SleepCore, goalSleepHHmm: Long?, goalWakeHHmm: Long?) {
    val diffBed = goalSleepHHmm?.let { kotlin.math.abs(diffMinutes(it, sleep.bedTime)) }
    val diffWake = goalWakeHHmm?.let { kotlin.math.abs(diffMinutes(it, sleep.wakeTime)) }
    val consistent = listOfNotNull(diffBed, diffWake).all { it <= 30 } && (diffBed != null || diffWake != null)

    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text(if (consistent) "Consistente" else "Irregular") })
            Text(
                text = when {
                    consistent -> "Vas bien: mantén este ritmo para estabilizar tu descanso."
                    sleep.sleepScore >= 80 -> "Buen puntaje hoy. Si repites esto varios días, tu semana sube fuerte."
                    else -> "Hoy estuvo flojo: prueba dormir 30 min antes y evita pantallas al final."
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* ---------- helpers ---------- */

private fun parseKotlinLocalDate(dateStr: String): LocalDate? {
    return try {
        val d = java.time.LocalDate.parse(dateStr) // "2025-12-08"
        LocalDate(d.year, d.monthValue, d.dayOfMonth)
    } catch (_: Exception) {
        null
    }
}

private fun formatDurationMinutes(minutes: Long): String {
    val totalMin = minutes.coerceAtLeast(0)
    val h = totalMin / 60
    val m = totalMin % 60
    return "${h}h ${m}m"
}

private fun formatTime(dt: LocalDateTime): String {
    val hh = dt.hour.toString().padStart(2, '0')
    val mm = dt.minute.toString().padStart(2, '0')
    return "$hh:$mm"
}

private fun formatHHmm(hhmm: Long): String {
    val hh = (hhmm / 100).toInt().toString().padStart(2, '0')
    val mm = (hhmm % 100).toInt().toString().padStart(2, '0')
    return "$hh:$mm"
}

private fun diffMinutes(goalHHmm: Long, real: LocalDateTime): Int {
    val goalMin = ((goalHHmm / 100).toInt() * 60) + (goalHHmm % 100).toInt()
    val realMin = real.hour * 60 + real.minute
    return realMin - goalMin
}

private fun formatSignedMinutes(diff: Int): String {
    val sign = if (diff >= 0) "+" else "-"
    val a = kotlin.math.abs(diff)
    return "$sign${a}m"
}
