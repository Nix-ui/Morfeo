package com.ucb.morfeo.features.notification.domain

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationEventBus {
    private val _events = MutableSharedFlow<String>(replay = 0)
    val events = _events.asSharedFlow()
    suspend fun emit(route: String){
        _events.emit(route)
    }
}