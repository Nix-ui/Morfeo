package com.ucb.morfeo.navigation

sealed class Screen(val route:String) {
    object Welcome:Screen("/welcome")
    object Home:Screen("/home")
    object  Details:Screen("/details")
    object LogIn:Screen("/logIn")
    object SignUp:Screen("/signUp")
    object Maintenance:Screen("/maintenance")

}