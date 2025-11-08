package com.ucb.morfeo.features.core.maintenance.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.morfeo.navigation.Screen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    onNavigateRoute: (String) -> Unit = { },
    maintenanceStatusViewModel: MaintenanceStatusViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val maintenanceStatus = maintenanceStatusViewModel.maintenanceStatusState.collectAsState()

    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }


    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            isRefreshing = false
            onNavigateRoute(Screen.Maintenance.route)
        }
    }

    when (val state = maintenanceStatus.value) {
        is MaintenanceStatusViewModel.MaintenanceStatusUIState.Error -> {
            onNavigateRoute(Screen.Maintenance.route)
        }
        is MaintenanceStatusViewModel.MaintenanceStatusUIState.Success -> {
            if (!state.isMaintenance) {
                onNavigateRoute(Screen.Home.route)
            }
        }
        else -> Unit
    }

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Morfeo",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(8.dp)
                                .background(Color(0xFFFF7A00), CircleShape)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color(0xFF0A0E1A),
            bottomBar = { FooterSection() }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0A0E1A), Color(0xFF101426))
                        )
                    )
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        AnimatedGearIcon()

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Estamos en mantenimiento",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Estamos mejorando Morfeo para ofrecerte una mejor experiencia.\nVolveremos pronto.",
            color = Color(0xFFB8B9C3),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {},
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF141A2A)
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFFFF7A00), CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Mantenimiento en progreso",
                color = Color.White,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Si tienes alguna pregunta, contacta con soporte",
            color = Color(0xFF6D7080),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FooterSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Versión", color = Color(0xFF6D7080), fontSize = 13.sp)
            Text("Estado", color = Color(0xFF6D7080), fontSize = 13.sp)
        }
        Column(horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(bottom = 40.dp)
            ) {
            Text("Morfeo v1.0", color = Color.White, fontSize = 13.sp)
            Text("En mantenimiento", color = Color(0xFFFF7A00), fontSize = 13.sp)
        }
    }
}

@Composable
@Preview
fun MorfeoMaintenanceScreenPreview() {
    MaintenanceScreen()
}