package com.ucb.morfeo.features.innernotification.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.R
import com.ucb.morfeo.features.innernotification.domain.model.NotificationModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.Instant

@Composable
fun NotificationViewCard(
    onNavigatePath: (String)->Unit = {},
    notification: NotificationModel,
    onDeleteClick: ()->Unit = {},
    onReadClick: ()->Unit = {}
) {
    OutlinedCard(
        onClick = {onNavigatePath(notification.path)},
        colors = if(notification.read) CardDefaults.cardColors().copy(containerColor = colorResource(R.color.bar_color_night)) else CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst))
    ) {
        Row(
            modifier= Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.morfeo),
                tint = Color.Unspecified,
                contentDescription = "Notification",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            )
            Column() {
                Text(
                    text=notification.title,
                    style= MaterialTheme.typography.titleLarge,
                    color= colorResource(R.color.bar_color_day),
                    textAlign = TextAlign.Center
                )
                Text(
                    text=notification.content,
                    style= MaterialTheme.typography.titleMedium,
                    color= colorResource(R.color.bar_color_day).copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text= formatTimestamp(notification.timeStampRecived),
                    style= MaterialTheme.typography.labelSmall,
                    color= colorResource(R.color.pass),
                    textAlign = TextAlign.Center
                )

            }
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(0.dp)
            ) {
                IconButton(
                    onClick = {onReadClick()},
                    enabled = !notification.read,
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsOff,
                        contentDescription = "",
                        tint = if(notification.read) colorResource(R.color.firefly) else colorResource(R.color.dodger_blue)
                    )
                }
                IconButton(
                    onClick = {onDeleteClick()},
                    enabled = notification.read
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "",
                        tint = if(notification.read) colorResource(R.color.firefly) else colorResource(R.color.dodger_blue)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("EEE, d 'de' MMMM yyyy, HH:mm",
        Locale("es", "ES")
    )
    return localDateTime.format(formatter)
}