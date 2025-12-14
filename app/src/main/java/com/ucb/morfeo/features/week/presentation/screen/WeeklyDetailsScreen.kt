package com.ucb.morfeo.features.week.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.ucb.morfeo.R
import com.ucb.morfeo.features.week.domain.model.DailySleepData
import com.ucb.morfeo.features.week.presentation.viewmodel.ViewMode
import com.ucb.morfeo.features.week.presentation.viewmodel.WeeklyDetailsViewModel
import com.ucb.morfeo.features.week.presentation.viewmodel.WeeklySummaryState
import com.ucb.morfeo.navigation.Screen
import com.ucb.morfeo.navigation.buttonNavBar.presentation.ButtomNavBar
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
    onBackClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val weeklyState by viewModel.weeklyState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val weekVsPrev by viewModel.weekVsPrev.collectAsState()


    Scaffold(
        topBar = {
            WeeklyTopAppBar(
                selectedDate = selectedDate,
                viewMode = viewMode,
                onPreviousClick = { viewModel.navigateToPrevious() },
                onNextClick = { viewModel.navigateToNext() },
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            ButtomNavBar(selectedRoute = Screen.Week.route, onRouteSelected = onNavigate)
        },
        containerColor = colorResource(R.color.firefly)
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ViewModeTabRow(selectedMode = viewMode, onTabSelected = { viewModel.setViewMode(it) })

            when (viewMode) {
                ViewMode.WEEK -> {
                    when (val state = weeklyState) {
                        is WeeklySummaryState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is WeeklySummaryState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.weekly_details_error_loading),
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
                                        Text(stringResource(id = R.string.weekly_details_retry_button))
                                    }
                                }
                            }
                        }

                        is WeeklySummaryState.Success -> {
                            val weeklySummary = state.weeklySummary

                            // ✅ BOTÓN DEMO: si no hay data para esta semana
                            if (weeklySummary.dailyData.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "No hay datos de sueño para esta semana.",
                                        color = Color.White.copy(alpha = 0.9f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Button(
                                        onClick = { viewModel.seedDemoWeek() },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Cargar datos demo")
                                    }
                                }
                            }

                            WeeklySummaryContent(
                                weeklySummary = weeklySummary,
                                weekVsPrev = weekVsPrev,
                                onDailyDetailClick = onDailyDetailClick
                            )

                        }
                    }
                }

                ViewMode.MONTH -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(id = R.string.weekly_details_monthly_view_wip), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewModeTabRow(selectedMode: ViewMode, onTabSelected: (ViewMode) -> Unit) {
    TabRow(
        selectedTabIndex = selectedMode.ordinal,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) {
        ViewMode.values().forEach { mode ->
            Tab(
                selected = selectedMode == mode,
                onClick = { onTabSelected(mode) },
                text = { Text(text = mode.name.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
private fun WeeklyTopAppBar(
    selectedDate: kotlinx.datetime.LocalDate,
    viewMode: ViewMode,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val title = when (viewMode) {
        ViewMode.WEEK -> {
            val weekEnd = selectedDate.plus(6, DateTimeUnit.DAY)
            "${formatDate(selectedDate)} - ${formatDate(weekEnd)}"
        }
        ViewMode.MONTH -> {
            val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
            selectedDate.toJavaLocalDate().format(monthFormatter).replaceFirstChar { it.uppercase() }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(0.0f),
                        Color.White.copy(0.06f)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.weekly_details_back_button),
                    tint = Color.White
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onPreviousClick) {
                    Icon(Icons.Default.ArrowBackIos, contentDescription = stringResource(id = R.string.weekly_details_previous_button), tint = Color.White)
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                IconButton(onClick = onNextClick) {
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = stringResource(id = R.string.weekly_details_next_button), tint = Color.White)
                }
            }

            Icon(
                painter = painterResource(R.drawable.morfeo),
                contentDescription = stringResource(id = R.string.weekly_details_app_icon_description),
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun WeeklySummaryContent(
    weeklySummary: com.ucb.morfeo.features.week.domain.model.WeeklySummary,
    weekVsPrev: WeeklyDetailsViewModel.WeekVsPrevUi?,
    onDailyDetailClick: (kotlinx.datetime.LocalDate) -> Unit,
    modifier: Modifier = Modifier
)
 {
    LazyColumn(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WeeklyStatsCard(weeklySummary)
        }
        item {
            if (weekVsPrev != null) {
                WeekVsPreviousCard(weekVsPrev)
            } else {
                // fallback a tu card antigua si aún quieres
                WeekComparisonCard(weeklySummary)
            }
        }
        item {
            SleepScoreChartCard(weeklySummary.dailyData)
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



    }
}

@Composable
fun SleepScoreChartCard(dailyData: List<DailySleepData>) {
    if (dailyData.isEmpty()) return

    val chartEntryModelProducer = ChartEntryModelProducer(
        dailyData.mapIndexed { index, day ->
            entryOf(index.toFloat(), day.sleepScore)
        }
    )

    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        getDayName(dailyData[value.toInt()].date).substring(0, 3)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.weekly_details_weekly_sleep_score_title),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            Chart(
                chart = columnChart(),
                chartModelProducer = chartEntryModelProducer,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = bottomAxisValueFormatter,
                )
            )
        }
    }
}

@Composable
private fun WeeklyStatsCard(weeklySummary: com.ucb.morfeo.features.week.domain.model.WeeklySummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.weekly_details_weekly_summary_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    label = stringResource(id = R.string.weekly_details_average_sleep_label),
                    value = formatDuration(weeklySummary.averageSleepDuration),
                    color = MaterialTheme.colorScheme.primary
                )

                StatItem(
                    label = stringResource(id = R.string.weekly_details_score_label),
                    value = "${weeklySummary.averageSleepScore}",
                    color = getScoreColor(weeklySummary.averageSleepScore)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                weeklySummary.bestSleepDay?.let { bestDay ->
                    StatItem(
                        label = stringResource(id = R.string.weekly_details_best_day_label),
                        value = getDayName(bestDay.date),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                weeklySummary.worstSleepDay?.let { worstDay ->
                    StatItem(
                        label = stringResource(id = R.string.weekly_details_worst_day_label),
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
            color = Color.White.copy(alpha = 0.8f)
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
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.weekly_details_consistency_title),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { weeklySummary.consistencyScore / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = getConsistencyColor(weeklySummary.consistencyScore),
                trackColor = Color.White.copy(alpha = 0.3f)
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
                color = Color.White.copy(alpha = 0.8f)
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
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.weekly_details_days_of_the_week_title),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            dailyData.forEach { dayData ->
                DailySleepItem(
                    dailyData = dayData,
                    onClick = { onDayClick(dayData.date) }
                )
                if (dayData != dailyData.last()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.2f))
                }
            }
        }
    }
}
@Composable
private fun WeekVsPreviousCard(ui: WeeklyDetailsViewModel.WeekVsPrevUi) {
    fun fmtMin(m: Long): String {
        val t = kotlin.math.abs(m)
        val h = t / 60
        val mm = t % 60
        return "${h}h ${mm}m"
    }

    fun signedMin(m: Long) = (if (m >= 0) "+" else "-") + fmtMin(m)
    fun signedPts(p: Int) = (if (p >= 0) "+" else "-") + kotlin.math.abs(p) + " pts"
    fun signedPct(p: Float) = (if (p >= 0f) "+" else "-") + kotlin.math.abs(p).toInt() + "%"

    val good = MaterialTheme.colorScheme.tertiary
    val bad = MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Comparación vs semana anterior", style = MaterialTheme.typography.titleMedium, color = Color.White)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Duración", color = Color.White.copy(alpha = 0.8f))
                Text(
                    "${fmtMin(ui.currentAvgMinutes)} • Prev: ${fmtMin(ui.prevAvgMinutes)} • ${signedMin(ui.diffMinutes)}",
                    color = if (ui.diffMinutes >= 0) good else bad
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score", color = Color.White.copy(alpha = 0.8f))
                Text(
                    "${ui.currentAvgScore} • Prev: ${ui.prevAvgScore} • ${signedPts(ui.diffScore)}",
                    color = if (ui.diffScore >= 0) good else bad
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Consistencia", color = Color.White.copy(alpha = 0.8f))
                Text(
                    "${ui.currentConsistency.toInt()}% • Prev: ${ui.prevConsistency.toInt()}% • ${signedPct(ui.diffConsistency)}",
                    color = if (ui.diffConsistency >= 0f) good else bad
                )
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
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = getDayName(dailyData.date),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = formatDate(dailyData.date),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatDuration(dailyData.sleepDuration),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
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
                contentDescription = stringResource(id = R.string.weekly_details_view_details_button),
                tint = Color.White.copy(alpha = 0.8f)
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
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
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
                    stringResource(id = R.string.weekly_details_comparison_positive, comparison.toInt())
                } else {
                    stringResource(id = R.string.weekly_details_comparison_negative, comparison.toInt())
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
    return date.toJavaLocalDate().format(formatter).replaceFirstChar { it.uppercase() }
}

private fun getScoreColor(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF4CAF50) // Verde
        score >= 60 -> Color(0xFF2196F3) // Azul
        score >= 40 -> Color(0xFFFF9800) // Naranja
        else -> Color(0xFFF44336)      // Rojo
    }
}

private fun getConsistencyColor(score: Float): Color {
    return when {
        score >= 80 -> Color(0xFF4CAF50) // Verde
        score >= 60 -> Color(0xFFFF9800) // Naranja
        else -> Color(0xFFF44336)      // Rojo
    }
}

@Composable
private fun getConsistencyMessage(score: Float): String {
    return when {
        score >= 80 -> stringResource(id = R.string.weekly_details_consistency_excellent)
        score >= 60 -> stringResource(id = R.string.weekly_details_consistency_good)
        else -> stringResource(id = R.string.weekly_details_consistency_improvable)
    }
}

