package com.ucb.morfeo.features.welcome.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.morfeo.R
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar
import com.ucb.morfeo.navigation.Screen


@Composable
fun WelcomeScreen(
    onNavigateToTab :(String)-> Unit = {}
    ){
    var selectedItem by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            TopNavBar(false,stringResource(R.string.welcome_screen_title))
        },
        bottomBar = {},
        containerColor = colorResource(R.color.firefly),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
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
                        onClick = {
                            onNavigateToTab(Screen.LogIn.route)
                        },
                        colors = ButtonColors(
                            Color.Red,Color.Blue,
                            Color.Red,Color.Blue
                        )
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
        }
    }
}










@Preview
@Composable
fun PreviewWelcomeScreen(){
    WelcomeScreen()
}

