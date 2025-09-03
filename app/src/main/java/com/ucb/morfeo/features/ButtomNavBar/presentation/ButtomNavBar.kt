package com.ucb.morfeo.features.ButtomNavBar.presentation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.morfeo.R
import com.ucb.morfeo.features.welcome.presentation.NavigationItem

@Composable
fun ButtomNavBar(selectedItem:Int, onItemSelected: (Int)-> Unit){
    NavigationBar(
        containerColor = colorResource(R.color.cloud_burst),
        tonalElevation = 4.dp
    ) {
        val items = listOf(
            NavigationItem("Inicio"),
            NavigationItem("Semana"),
            NavigationItem("Analisis"),
            NavigationItem("Consejos"),
            NavigationItem("Ajustes"),
        )
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = false,
                onClick = { onItemSelected(index)},
                icon = {

                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 13.sp,
                        color = if ( selectedItem == index) colorResource(R.color.dodger_blue) else Color.White
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun previewButtomNavBar(){
    var selectedItem by remember { mutableIntStateOf(0) }
    ButtomNavBar(
        selectedItem = selectedItem,
        onItemSelected = {selectedItem = it}
    )
}