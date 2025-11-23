package com.ucb.morfeo.features.login.domain.model

@JvmInline
value class Password(val value: String) {
    init {
        require(value.isNotEmpty()) { "Password cannot be empty" }
        //require(value.length >= 8) { "Password must be at least 8 characters long" }
        //require(value.any { it.isUpperCase() }) { "Password must contain at least one uppercase letter" }
        //require(value.any { it.isLowerCase() }) { "Password must contain at least one lowercase letter" }
        //require(value.any { it.isDigit() }) { "Password must contain at least one digit" }
    }
}