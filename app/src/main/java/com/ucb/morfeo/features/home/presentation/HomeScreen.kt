package com.ucb.morfeo.features.home.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.morfeo.R
import com.ucb.morfeo.navigation.buttonNavBar.presentation.ButtomNavBar
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar
import com.ucb.morfeo.features.core.maintenance.presentation.MaintenanceStatusViewModel
import com.ucb.morfeo.navigation.Screen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigatedToTab: (String) -> Unit = {},
    maintenanceStatusViewModel: MaintenanceStatusViewModel = koinViewModel()
){
    val sleepScore = 50
    val averageSleep = "7h 25m"
    val bedTime = "23:40"
    val wakeTime = "06:58"
    val weeklyImprovement = 5
    var selectItem by remember { mutableStateOf("") }
    val context = LocalContext.current
    val maintenanceStatus = maintenanceStatusViewModel.maintenanceStatusState.collectAsState()



    LaunchedEffect(Unit) {
        maintenanceStatusViewModel.checkMaintenanceStatus()
    }


    when (val state = maintenanceStatus.value) {
        is MaintenanceStatusViewModel.MaintenanceStatusUIState.Error -> {
            onNavigatedToTab(Screen.Maintenance.route)
        }
        is MaintenanceStatusViewModel.MaintenanceStatusUIState.Success -> {
            if (state.isMaintenance) {
                onNavigatedToTab(Screen.Maintenance.route)
                Toast.makeText(context, "En mantenimiento", Toast.LENGTH_LONG).show()
            }else{
                onNavigatedToTab(Screen.Home.route)
                Toast.makeText(context, "En funcionamiento", Toast.LENGTH_LONG).show()
            }
        }
        else -> Unit
    }
    Scaffold(
        containerColor = colorResource(R.color.firefly),
        topBar = {
            TopNavBar(false, stringResource(R.string.dashboard_title))
        },
        bottomBar = {
            ButtomNavBar(
                selectedRoute = selectItem,
                onRouteSelected = { route ->
                    selectItem = route
                    onNavigatedToTab(route)
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState(), true),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            SleepScoreCard(
                score = sleepScore,
                improvement = weeklyImprovement
            )
            SleepStatsCard(
                averageSleep = averageSleep,
                bedTime = bedTime,
                wakeTime = wakeTime
            )
            SleepWeeklyBar(
                weekScore = listOf(20,40,79,59,39,0,0)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun SleepScoreCard(
    score: Int,
    improvement: Int
) {
    @Composable
    fun getScoreColor(score: Int): Color {
        return when {
            score >= 80 -> colorResource(R.color.pass)
            score >= 60 -> colorResource(R.color.warning)
            else -> colorResource(R.color.error)
        }
    }
    @Composable
    fun getBorderColor(score: Int): Color {
        return when {
            score >= 80 -> colorResource(R.color.pass_light)
            score >= 60 -> colorResource(R.color.warning_light)
            else -> colorResource(R.color.error_light)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(
                width = 2.dp,
                color = getBorderColor(score),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.cloud_burst)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = score.toString(),
                style = TextStyle(
                    fontSize = 55.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "Puntuación de sueño",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = if (improvement > 0) "+$improvement% respecto a la semana anterior"
                else "$improvement% respecto a la semana anterior",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = if (improvement > 0) colorResource(R.color.error) else colorResource(R.color.pass),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.width(200.dp)
            )
            SleepProgressBar(
                progress = score / 100f,
                color = getScoreColor(score)
            )
        }
    }
}

@Composable
fun SleepProgressBar(
    progress: Float,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Gray.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.7f),
                            color
                        )
                    )
                )
        )
    }
}

@Composable
fun SleepStatsCard(
    averageSleep: String,
    bedTime: String,
    wakeTime: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .border(
                width = 2.dp,
                color = colorResource(R.color.dodger_blue),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.cloud_burst)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SleepStatItem(
                title = "Promedio\n7 días",
                value = averageSleep,
                icon = "📊"
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(80.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )
            SleepStatItem(
                title = "Dormir\nHabitual",
                value = bedTime,
                icon = "🌙"
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(80.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )
            SleepStatItem(
                title = "Despertar",
                value = wakeTime,
                icon = "☀️"
            )
        }
    }
}

@Composable
fun SleepStatItem(
    title: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )

        Text(
            text = title,
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        )

        Text(
            text = value,
            style = TextStyle(
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun SleepWeeklyBar(
    weekScore: List<Int>,
    modifier: Modifier = Modifier
) {
    require(weekScore.size == 7) { "weekScore debe tener exactamente 7 elementos" }

    val daysOfWeek = listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do")
    val maxScore = weekScore.maxOrNull() ?: 100
    val todayIndex = 4

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(
                width = 1.dp,
                color = colorResource(R.color.bar_color_day).copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.cloud_burst)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xFF4DD0E1).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = Color(0xFF4DD0E1).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = "Últimos",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "7 días",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xFF4DD0E1).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = "Ver",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        Text(
                            text = "semana",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF4DD0E1).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weekScore.forEachIndexed { index, score ->
                        WeeklyBarItem(
                            score = score,
                            day = daysOfWeek[index],
                            maxScore = maxScore,
                            isHighlighted = index == todayIndex,
                            maxHeight = 100.dp
                        )
                    }
                }
                if (todayIndex < weekScore.size) {
                    val highlightedScore = weekScore[todayIndex]
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (todayIndex * 45).dp,
                                y = (-20).dp
                            )
                            .background(
                                color = colorResource(R.color.firefly),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${highlightedScore}h ${(highlightedScore * 0.6).toInt()}min",
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyBarItem(
    score: Int,
    day: String,
    maxScore: Int,
    isHighlighted: Boolean,
    maxHeight: Dp
) {
    val barHeight = if (maxScore > 0) {
        maxHeight * (score.toFloat() / maxScore.toFloat()).coerceIn(0.2f, 1f)
    } else {
        maxHeight * 0.2f
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.width(40.dp)
    ) {
        Box(
            modifier = Modifier
                .width(if (isHighlighted) 16.dp else 12.dp)
                .height(barHeight)
                .background(
                    color = if (isHighlighted) {
                        colorResource(R.color.teal_700)
                    } else {
                        Color(0xFF4DD0E1).copy(alpha = 0.7f)
                    }
                )
                .then(
                    if (isHighlighted) {
                        Modifier
                            .border(
                                width = 2.dp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                    } else Modifier
                )
        )

        Spacer(modifier = Modifier.height(8.dp))
        if (isHighlighted) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(20.dp)
                    .background(
                        color = colorResource(R.color.firefly),
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }else{
            Text(
                text = day,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = if (isHighlighted) Color(0xFF4DD0E1) else Color.White.copy(alpha = 0.8f),
                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}


@Preview
@Composable
fun previewHomeScreen() {
    HomeScreen()
}