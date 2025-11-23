package com.ucb.morfeo.features.permis.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import com.ucb.morfeo.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar

@Composable
fun PermisionScreen(isBackeable:Boolean = false , onNavigateRoute: (String) -> Unit = {}) {
    Scaffold (
        topBar ={
            TopNavBar(isBackeable, stringResource(R.string.permision_title), onNavigateRoute)
        },
        containerColor = colorResource(R.color.firefly)
    ){innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(innerPadding)
                .background(colorResource(R.color.firefly))
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.permision_subtitle),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 50.dp)
            )
            Text(
                text=stringResource(R.string.permision_body_text),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst)),
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Row {
                    Column {
                        Text(
                            text = stringResource(R.string.permision_activity_subtitle),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.permision_activity_text),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                    FilledIconToggleButton(
                        checked = false,
                        onCheckedChange = {}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = false)
fun PreviewPermitionScreen(){
    PermisionScreen()
}