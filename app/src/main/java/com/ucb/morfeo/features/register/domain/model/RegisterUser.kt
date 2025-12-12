package com.ucb.morfeo.features.register.domain.model

import com.ucb.morfeo.features.login.domain.model.Email
import com.ucb.morfeo.features.login.domain.model.Password

data class RegisterUser(
    val email: Email,
    val password: Password,
    val nombre: String
)
