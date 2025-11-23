package com.ucb.morfeo.features.login.domain.model

@JvmInline
value class Altura(val value: Double){
    init{
        require(value>0){"La altura no puede ser negativa"}
        require(value<300){"La altura no puede ser mayor a 3 metros"}
    }
}