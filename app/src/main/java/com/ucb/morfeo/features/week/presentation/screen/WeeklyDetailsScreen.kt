package com.ucb.morfeo.features.week.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.features.week.presentation.viewmodel.WeeklyDetailsViewModel
import com.ucb.morfeo.features.week.presentation.viewmodel.WeeklySummaryState
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDate
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeeklyDetailsScreen(
    viewModel: WeeklyDetailsViewModel = koinViewModel(),
    onDailyDetailClick: (kotlinx.datetime.LocalDate) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val weeklyState by viewModel.weeklyState.collectAsState()
    val selectedWeek by viewModel.selectedWeek.collectAsState()

    Scaffold(
        topBar = {
            WeeklyTopAppBar(
                selectedWeek = selectedWeek,
                onPreviousWeek = { viewModel.navigateToPreviousWeek() },
                onNextWeek = { viewModel.navigateToNextWeek() },
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        when (val state = weeklyState) {
            is WeeklySummaryState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is WeeklySummaryState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error al cargar datos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadWeeklySummary() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is WeeklySummaryState.Success -> {
                WeeklySummaryContent(
                    weeklySummary = state.weeklySummary,
                    onDailyDetailClick = onDailyDetailClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeeklyTopAppBar(
    selectedWeek: kotlinx.datetime.LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onBackClick: () -> Unit
) {
    val weekEnd = selectedWeek.plus(6, DateTimeUnit.DAY)

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onPreviousWeek) {
                    Icon(Icons.Default.ArrowBackIos, contentDescription = "Semana anterior")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${formatDate(selectedWeek)} - ${formatDate(weekEnd)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = selectedWeek.year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onNextWeek) {
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = "Semana siguiente")
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
        }
    )
}

@Composable
private fun WeeklySummaryContent(
    weeklySummary: com.ucb.morfeo.features.week.domain.model.WeeklySummary,
    onDailyDetailClick: (kotlinx.datetime.LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WeeklyStatsCard(weeklySummary)
        }

        item {
            ConsistencyCard(weeklySummary)
        }

        item {
            DailySleepList(
                dailyData = weeklySummary.dailyData,
                onDayClick = onDailyDetailClick
            )
        }

        item {
            WeekComparisonCard(weeklySummary)
        }
    }
}

@Composable
private fun WeeklyStatsCard(weeklySummary: com.ucb.morfeo.features.week.domain.model.WeeklySummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumen Semanal",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Promedio Sueño",
                    value = formatDuration(weeklySummary.averageSleepDuration),
                    color = MaterialTheme.colorScheme.primary
                )

                StatItem(
                    label = "Puntuación",
                    value = "${weeklySummary.averageSleepScore}",
                    color = getScoreColor(weeklySummary.averageSleepScore)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weeklySummary.bestSleepDay?.let { bestDay ->
                    StatItem(
                        label = "Mejor día",
                        value = getDayName(bestDay.date),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                weeklySummary.worstSleepDay?.let { worstDay ->
                    StatItem(
                        label = "Peor día",
                        value = getDayName(worstDay.date),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ConsistencyCard(weeklySummary: com.ucb.morfeo.features.week.domain.model.WeeklySummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Consistencia",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { weeklySummary.consistencyScore / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = getConsistencyColor(weeklySummary.consistencyScore)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${weeklySummary.consistencyScore.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = getConsistencyColor(weeklySummary.consistencyScore)
            )

            Text(
                text = getConsistencyMessage(weeklySummary.consistencyScore),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DailySleepList(
    dailyData: List<com.ucb.morfeo.features.week.domain.model.DailySleepData>,
    onDayClick: (kotlinx.datetime.LocalDate) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Días de la Semana",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            dailyData.forEach { dayData ->
                DailySleepItem(
                    dailyData = dayData,
                    onClick = { onDayClick(dayData.date) }
                )
                if (dayData != dailyData.last()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun DailySleepItem(
    dailyData: com.ucb.morfeo.features.week.domain.model.DailySleepData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = getDayName(dailyData.date),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formatDate(dailyData.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatDuration(dailyData.sleepDuration),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${dailyData.sleepScore} pts",
                    style = MaterialTheme.typography.bodySmall,
                    color = getScoreColor(dailyData.sleepScore)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ver detalles",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeekComparisonCard(weeklySummary: com.ucb.morfeo.features.week.domain.model.WeeklySummary) {
    val comparison = weeklySummary.comparisonWithPreviousWeek
    if (comparison == 0f) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (comparison > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                contentDescription = null,
                tint = if (comparison > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (comparison > 0) {
                    "+${comparison.toInt()}% respecto a la semana anterior"
                } else {
                    "${comparison.toInt()}% respecto a la semana anterior"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (comparison > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun formatDuration(duration: kotlin.time.Duration): String {
    val hours = duration.inWholeHours
    val minutes = duration.inWholeMinutes % 60
    return "${hours}h ${minutes}m"
}

private fun formatDate(date: kotlinx.datetime.LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
    return date.toJavaLocalDate().format(formatter)
}

private fun getDayName(date: kotlinx.datetime.LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
    return date.toJavaLocalDate().format(formatter)
}

private fun getScoreColor(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF4CAF50)
        score >= 60 -> Color(0xFF2196F3)
        score >= 40 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}

private fun getConsistencyColor(score: Float): Color {
    return when {
        score >= 80 -> Color(0xFF4CAF50)
        score >= 60 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}

private fun getConsistencyMessage(score: Float): String {
    return when {
        score >= 80 -> "Excelente consistencia"
        score >= 60 -> "Buena consistencia"
        else -> "Puedes mejorar la consistencia"
    }
}