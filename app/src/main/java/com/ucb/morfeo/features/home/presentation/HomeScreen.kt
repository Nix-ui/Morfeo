package com.ucb.morfeo.features.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.morfeo.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue


@Composable
fun HomeScreen(){
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 20.dp)
            .fillMaxHeight()
            .background(color = colorResource(R.color.firefly)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .background(brush = Brush.linearGradient(
                    colors = listOf<Color>(
                        Color.White.copy(0.06f),
                        Color.White.copy(alpha = 0.0f)
                    )
                ))
        ) {
            Image(
                painter = painterResource(R.drawable.morfeo),
                contentDescription = "Icono de principal",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .padding(0.dp),
                alignment = Alignment.Center
            )
            Text(
                text = "Morfeo",
                style = TextStyle(
                    color = Color.Cyan
                )
            )
            Image(
                painter = painterResource(R.drawable.morfeo),
                contentDescription = "Logo principal de inicio",
                modifier = Modifier.size(50.dp)
            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
                .padding(top = 0.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.morfeo),
                contentDescription = "Logo principal",
                modifier = Modifier.size(200.dp)
                    .clip(CircleShape)

            )
            Text(text = "Bienvenido a Morfeo", textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color.Cyan,
                    fontSize = 30.sp,

                ),
                modifier = Modifier.padding(top = 40.dp)
            )
            Spacer(
                modifier = Modifier.size(100.dp)
            )
            Row (
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.width(400.dp)
            ){
                Button(
                    onClick = {},
                    colors = ButtonColors(Color.Red,Color.Blue,Color.Red,Color.Blue)
                ){
                    Text(
                        "Tomar la pastilla roja",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }
        var selectedItem by remember { mutableIntStateOf(0) }
        ButtomNavigationBar(
            selectedItem = selectedItem,
            onItemSelected = {selectedItem = it}
        )
    }
}


@Composable
fun ButtomNavigationBar(selectedItem:Int, onItemSelected: (Int)-> Unit){
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
fun previewButtomNavigationBar(){
    var selectedItem by remember { mutableIntStateOf(0) }
    ButtomNavigationBar(
        selectedItem = selectedItem,
        onItemSelected = {selectedItem = it}
    )
}

@Preview
@Composable
fun previewHomeScreen(){
    HomeScreen()
}

data class NavigationItem(val label:String)