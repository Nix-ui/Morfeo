package com.ucb.morfeo.features.login.domain.model

@JvmInline
value class Email(val value: String) {
    init {
        require(value.isNotEmpty()) { "Email cannot be empty" }
        require(value.contains("@")) { "Email must contain an '@' symbol" }
    }
}