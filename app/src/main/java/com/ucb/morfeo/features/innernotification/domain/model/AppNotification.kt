package com.ucb.morfeo.features.innernotification.domain.model

data class AppNotification(
    val id: Int=0,
    val title: String,
    val message: String,
    val notificationType: NotificationType = NotificationType.GENERIC,
    val timeStamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
)
