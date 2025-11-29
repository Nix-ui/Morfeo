package com.ucb.helpet.features.time.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimeView(viewModel: TimeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is TimeUiState.Loading -> {
                CircularProgressIndicator()
            }
            is TimeUiState.Success -> {
                val formattedTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(state.time))
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            is TimeUiState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red
                )
            }
        }
    }
}