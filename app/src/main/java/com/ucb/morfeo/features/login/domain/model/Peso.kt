package com.ucb.morfeo.features.login.domain.model

@JvmInline
value class Peso(val value: Double) {
    init {
        require(value > 0) { "El peso debe ser un valor positivo" }
        require(value < 500) { "El peso no puede ser mayor a 500 kg" }
    }
}