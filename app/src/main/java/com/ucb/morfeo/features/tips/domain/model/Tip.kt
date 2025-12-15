package com.ucb.morfeo.features.tips.domain.model

enum class TipPriority { HIGH, MEDIUM, LOW }

data class Tip(
    val id: String,
    val title: String,
    val message: String,
    val priority: TipPriority
)
