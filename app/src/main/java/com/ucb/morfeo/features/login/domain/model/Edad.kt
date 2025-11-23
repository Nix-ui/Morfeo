package com.ucb.morfeo.features.login.domain.model

@JvmInline
value class Edad(val value:Int){
    init {
        require(value>=0){"La edad no puede ser negativa"}
        require(value<100){"La edad no puede ser mayor a 100"}
    }
}