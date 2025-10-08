package com.ucb.morfeo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.morfeo.features.home.presentation.HomeScreen
import com.ucb.morfeo.features.login.presentation.LoginScreen
import com.ucb.morfeo.features.notification.domain.presentation.NotificationViewModel
import com.ucb.morfeo.features.welcome.presentation.WelcomeScreen


@Composable
fun AppNavigator() {
    val navController = rememberNavController()
//    val notificationViewModel: NotificationViewModel = viewModel()
//    val uiState by notificationViewModel.uiState.collectAsState()
//    LaunchedEffect(uiState) {
//        notificationViewModel.getNotification()
//        if(uiState is NotificationViewModel.NotificationStateUI.Success){
//            val route = (uiState as NotificationViewModel.NotificationStateUI.Success).notification
//            navController.navigate(route)
//            notificationViewModel.onNavigate()
//        }
//    }
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ){
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToTab = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.Home.route){
            HomeScreen(
                onNavigatedToTab = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.LogIn.route){
            LoginScreen(
                onNavigateRoute = { route ->
                    navController.navigate(route)
                }
            )
        }
    }
}
@Preview
@Composable
fun previewAppNavigator(){
    AppNavigator()
}