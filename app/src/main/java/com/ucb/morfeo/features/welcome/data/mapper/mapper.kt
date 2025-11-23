package com.ucb.morfeo.features.welcome.data.mapper

import com.ucb.morfeo.features.welcome.data.database.entity.UserEntity
import com.ucb.morfeo.features.welcome.domain.model.UserModel
import kotlin.uuid.Uuid

fun UserEntity.toModel(): UserModel{
    return UserModel(
        email = email,
        password = password,
        burnDate = burnedDate,
        weight = weight,
        height = height
    )
}

fun UserModel.toEntity(): UserEntity{
    return UserEntity(
        email = email,
        password = password,
        burnedDate = burnDate,
        weight = weight,
        height = height
    )
}