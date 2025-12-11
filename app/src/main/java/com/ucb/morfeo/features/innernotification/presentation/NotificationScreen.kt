package com.ucb.morfeo.features.innernotification.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.ucb.morfeo.features.TopNavBar.presentation.TopNavBar
import com.ucb.morfeo.navigation.buttonNavBar.presentation.ButtomNavBar
import org.koin.androidx.compose.koinViewModel
import com.ucb.morfeo.R

@Composable
fun NotificationScreen(
    onBackTap: (String)->Unit={},
    viewModel: InnerNotificationViewModel = koinViewModel()
) {
    val notifications = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopNavBar(
            isBackEnable = true,
            currentScreenName = stringResource(R.string.notification_screen_title),
            onBackScreen = onBackTap
        ) },
        bottomBar = { ButtomNavBar(
            selectedRoute = "Notifications",
            onRouteSelected = onBackTap
        )},
        containerColor = colorResource(R.color.firefly)
    ) {innerPadding ->
        when(val state = notifications.value){
            is InnerNotificationViewModel.NotificationsStateUI.Loading-> {
                CircularProgressIndicator()
            }
            is InnerNotificationViewModel.NotificationsStateUI.Error->{
                Text(state.message)
            }
            is InnerNotificationViewModel.NotificationsStateUI.Success->{
                if(state.notifications.isEmpty()){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("No hay Notificaciones")
                    }
                }else{
                    LazyColumn(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        items(items=state.notifications){ notification->
                            NotificationViewCard(
                                onNavigatePath = onBackTap,
                                notification = notification,
                                onDeleteClick = { viewModel.delete(notification.id) },
                                onReadClick = { viewModel.markAsRead(notification.id)}
                            )
                        }
                    }
                }
            }
            else ->{

            }
        }
    }
}