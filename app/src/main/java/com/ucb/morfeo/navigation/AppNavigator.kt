package com.ucb.morfeo.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.morfeo.features.core.maintenance.presentation.MaintenanceScreen
import com.ucb.morfeo.features.core.maintenance.presentation.MaintenanceStatusViewModel
import com.ucb.morfeo.features.home.presentation.HomeScreen
import com.ucb.morfeo.features.innernotification.presentation.NotificationScreen
import com.ucb.morfeo.features.login.presentation.LoginScreen
import com.ucb.morfeo.features.settings.presentation.SettingsScreen
import com.ucb.morfeo.features.splash.presentation.SplashViewModel
import com.ucb.morfeo.features.week.presentation.screen.WeeklyDetailsScreen
import com.ucb.morfeo.features.welcome.presentation.WelcomeScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigator(
    maintenanceStatusViewModel: MaintenanceStatusViewModel = koinViewModel(),
    splashViewModel: SplashViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val maintenanceState by maintenanceStatusViewModel.maintenanceStatusState.collectAsState()
    val sessionState by splashViewModel.sessionState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            if (sessionState is SplashViewModel.SessionState.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                WelcomeScreen(onNavigateToTab = { route ->
                    navController.navigate(route)
                })
            }
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigatedToTab = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.LogIn.route) {
            LoginScreen(
                onNavigateRoute = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Maintenance.route) {
            MaintenanceScreen(
                onNavigateRoute = { }
            )
        }

        composable(Screen.Week.route) {
            WeeklyDetailsScreen(
                onDailyDetailClick = { localeDate->

                }
            )
        }

        composable(Screen.Analysis.route) {
            HomeScreen(
                onNavigatedToTab = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Profile.route) {
            HomeScreen(
                onNavigatedToTab = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigate = {route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Tips.route) {
            HomeScreen(
                onNavigatedToTab = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.Notifications.route) {
            NotificationScreen(
                onBackTap = {route ->
                    navController.navigate(route)
                }
            )
        }
    }

    LaunchedEffect(sessionState, maintenanceState, navController) {
        val currentRoute = navController.currentBackStack.value.lastOrNull()?.destination?.route
        if (sessionState is SplashViewModel.SessionState.Loading) {
            return@LaunchedEffect
        }
        if (maintenanceState is MaintenanceStatusViewModel.MaintenanceStatusUIState.Success) {
            val isMaintenance =
                (maintenanceState as MaintenanceStatusViewModel.MaintenanceStatusUIState.Success).isMaintenance
            if (isMaintenance && currentRoute != Screen.Maintenance.route) {
                navController.navigate(Screen.Maintenance.route) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                }
                return@LaunchedEffect
            }
            if (!isMaintenance && currentRoute == Screen.Maintenance.route) {
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                }
                return@LaunchedEffect
            }
        }
        if (sessionState is SplashViewModel.SessionState.ActiveSession && currentRoute == Screen.Welcome.route) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Welcome.route) { inclusive = true }
            }
        }
    }
}

@Preview
@Composable
fun previewAppNavigator() {
    AppNavigator()
}
