package com.ucb.morfeo.navigation

sealed class Screen(val route:String) {
    object Welcome:Screen("/welcome")
}