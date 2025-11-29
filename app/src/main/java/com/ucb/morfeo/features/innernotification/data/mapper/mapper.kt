package com.ucb.morfeo.features.innernotification.data.mapper

import com.ucb.morfeo.features.innernotification.database.entity.NotificationEntity
import com.ucb.morfeo.features.innernotification.domain.model.NotificationModel
import kotlin.random.Random

fun NotificationEntity.toModel(): NotificationModel{
    return NotificationModel(
        id = id,
        title =title,
        content = content,
        timeStampRecived = createDate,
        read = read,
        timeStampReaded = readedDate,
        path = path
    )
}

fun NotificationModel.toEntity(): NotificationEntity{
    return NotificationEntity(
        id=id,
        title= title,
        content = content,
        path = path,
        createDate = timeStampRecived,
        readedDate = timeStampReaded,
        read = read
    )
}