package com.ucb.morfeo.features.permissions.presentation.screen

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.R
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar
import com.ucb.morfeo.features.permissions.presentation.viewmodel.PermissionsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PermissionsScreen(
    onNavigate: (String) -> Unit = {},
    onPermissionsGranted: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: PermissionsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // If permissions are already granted, navigate away.
    LaunchedEffect(uiState.areAllPermissionsGranted) {
        if (uiState.areAllPermissionsGranted) {
            onPermissionsGranted()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onNotificationPermissionResult(isGranted)
        }
    )

    val usageStatsSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // After returning from settings, check the permission state again
        viewModel.updateUsageStatsPermissionState(context)
    }

    // Check the usage stats permission when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.updateUsageStatsPermissionState(context)
    }

    Scaffold(
        topBar = {
            TopNavBar(
                isBackEnable = true, 
                currentScreenName = stringResource(id = R.string.permision_title), 
                onNavigateTo = onNavigate,
                onBackScreen = onBack
            )
        },
        containerColor = colorResource(R.color.firefly)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Necesitamos tu permiso",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Text(
                text = "Usaremos horarios de actividad/inactividad (sin leer tu contenido) para estimar cuándo duermes y despiertas.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )

            PermissionCard(
                title = "Actividad del dispositivo",
                description = "Desbloqueos e inactividad prolongada",
                checked = uiState.isUsageStatsPermissionGranted,
                onCardClick = {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    usageStatsSettingsLauncher.launch(intent)
                }
            )

            PermissionCard(
                title = "Notificaciones",
                description = "Recordatorios de hora de dormir (opcional)",
                checked = uiState.isNotificationPermissionGranted,
                onCardClick = {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        // On older versions, the permission is granted by default
                        viewModel.onNotificationPermissionResult(true)
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onPermissionsGranted,
                modifier = Modifier.fillMaxWidth(),
                // Enable the button only if the essential permission is granted
                enabled = uiState.isUsageStatsPermissionGranted
            ) {
                Text("Continuar")
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    checked: Boolean,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.cloud_burst))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
            }
            Switch(
                checked = checked,
                onCheckedChange = null // Display only, interaction is handled by card click
            )
        }
    }
}

@Preview
@Composable
private fun PermissionsScreenPreview() {
    PermissionsScreen()
}
