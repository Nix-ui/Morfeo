package com.ucb.morfeo.features.register.domain.model

import com.ucb.morfeo.features.login.domain.model.Altura
import com.ucb.morfeo.features.login.domain.model.Edad
import com.ucb.morfeo.features.login.domain.model.Email
import com.ucb.morfeo.features.login.domain.model.Password
import com.ucb.morfeo.features.login.domain.model.Peso

data class RegisterUser(
    val email: Email,
    val password: Password,
    val nombre: String,
    val edad: Edad,
    val peso: Peso,
    val altura: Altura
)
