package com.ucb.morfeo.features.login.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.login.domain.model.Email
import com.ucb.morfeo.features.login.domain.model.LogInUser
import com.ucb.morfeo.features.login.domain.model.Password
import com.ucb.morfeo.features.login.domain.model.UserModel
import com.ucb.morfeo.features.login.domain.usecase.FetchLogInUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogInViewModel(
    val fetchLogInUserUseCase: FetchLogInUserUseCase
): ViewModel() {
    sealed class LogInUIState{
        object Init: LogInUIState()
        object Loading: LogInUIState()
        class Error(val message: String): LogInUIState()
        class Success(val userModel: UserModel): LogInUIState()
    }
    private val _logInState = MutableStateFlow<LogInUIState>(LogInUIState.Init)
    val logInState : StateFlow<LogInUIState> = _logInState.asStateFlow()
     fun logIn(email: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            _logInState.value = LogInUIState.Loading
            val result = fetchLogInUserUseCase.invoke(LogInUser(Email(email), Password (password)))
            result.fold(
                onSuccess = {
                    _logInState.value = LogInUIState.Success(it)
                },
                onFailure = {
                    _logInState.value = LogInUIState.Error(it.message ?: "Error desconocido")
                }
            )
        }
    }
}