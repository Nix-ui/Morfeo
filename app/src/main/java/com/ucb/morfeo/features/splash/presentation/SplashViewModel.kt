package com.ucb.morfeo.features.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.login.domain.usecase.CheckSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val checkSessionUseCase: CheckSessionUseCase
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
            val result = checkSessionUseCase.invoke()
            if (result.isSuccess) {
                _sessionState.value = SessionState.ActiveSession
            } else {
                _sessionState.value = SessionState.NoSession
            }
        }
    }
}
