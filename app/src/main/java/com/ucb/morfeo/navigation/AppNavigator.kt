package com.ucb.morfeo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.morfeo.features.home.presentation.HomeScreen
import com.ucb.morfeo.features.welcome.presentation.WelcomeScreen


@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ){
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToTab = { tabIndex ->
                    when(tabIndex){
                        0->navController.navigate(Screen.Home.route)
                    }
                }
            )
        }
        composable(Screen.Home.route){
            HomeScreen(
                onNavigatedToTab = { tabIndex ->
                    when(tabIndex){
                        0 -> navController.navigate(Screen.Home.route)
                    }
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