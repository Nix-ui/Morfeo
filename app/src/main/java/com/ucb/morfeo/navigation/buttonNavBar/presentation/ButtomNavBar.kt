package com.ucb.morfeo.navigation.buttonNavBar.presentation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.morfeo.R
import com.ucb.morfeo.navigation.Screen

data class NavigationItem(val label:String,val route:String)

@Composable
fun ButtomNavBar(selectedRoute:String, onRouteSelected: (String)-> Unit){
    NavigationBar(
        containerColor = colorResource(R.color.cloud_burst),
        tonalElevation = 4.dp
    ) {
        val items = listOf(
            NavigationItem("Inicio", Screen.Home.route),
            NavigationItem("Semana", Screen.Week.route),
            NavigationItem("Analisis", Screen.Analysis.route),
            NavigationItem("Consejos", Screen.Tips.route),
            NavigationItem("Ajustes", Screen.Settings.route),
        )
        items.forEach { item ->
            NavigationBarItem(
                selected = false,
                onClick = { onRouteSelected(item.route)},
                icon = {

                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 13.sp,
                        color = if ( selectedRoute == item.route) colorResource(R.color.dodger_blue) else Color.White
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun previewButtomNavBar(){
    var selectedItem by remember { mutableStateOf("") }
    ButtomNavBar(
        selectedRoute = selectedItem,
        onRouteSelected = {selectedItem = it}
    )
}