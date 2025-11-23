package com.ucb.morfeo.features.login.domain.model

data class UserModel(
    val email: Email,
    val nombre: String,
    val edad: Edad,
    val genero: Genero,
    val altura: Altura,
    val peso: Peso
) {
}

enum class Genero{
    MASCULINO,
    FEMENINO,
    OTRO
}