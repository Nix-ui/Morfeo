package com.ucb.morfeo.features.innernotification.domain.model

data class NotificationModel(
    val id: Int=0,
    val title: String,
    val content: String,
    var timeStampRecived: Long = System.currentTimeMillis(),
    var read: Boolean = false,
    var timeStampReaded: Long?,
    val path: String
)
