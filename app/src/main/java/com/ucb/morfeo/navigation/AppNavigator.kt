package com.ucb.morfeo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                        0->navController.navigate(Screen.Welcome.route)
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