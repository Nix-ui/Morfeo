package com.ucb.morfeo.features.innernotification.data

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ucb.morfeo.R

class NotificationManagerHelper(private val context: Context) {

    fun showSleepNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(context, "sleep_channel")
            .setSmallIcon(R.drawable.morfeo)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = NotificationManagerCompat.from(context)
        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
