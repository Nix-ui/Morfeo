package com.ucb.morfeo.features.welcome.domain.model

import java.util.Date

data class UserModel(
    val email:String, val password:String,
    val burnDate: String, val weight: Double,
    val height: Double
) {
}