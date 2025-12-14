package com.ucb.morfeo.features.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SplashViewModel(
    private val jwtDataStore: JWTDataStore
) : ViewModel() {

    sealed class SessionState {
        object Loading : SessionState()
        object NoSession : SessionState()
        object ActiveSession : SessionState()
    }

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState = _sessionState.asStateFlow()

    init {
        viewModelScope.launch {
            jwtDataStore.tokenFlow.collectLatest { token ->
                if (token.isNullOrBlank()) {
                    _sessionState.value = SessionState.NoSession
                } else {
                    _sessionState.value = SessionState.ActiveSession
                }
            }
        }
    }
}
