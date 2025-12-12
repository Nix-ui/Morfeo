package com.ucb.morfeo.features.register.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.login.domain.model.Email
import com.ucb.morfeo.features.login.domain.model.Password
import com.ucb.morfeo.features.login.domain.model.UserModel
import com.ucb.morfeo.features.register.domain.model.RegisterUser
import com.ucb.morfeo.features.register.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun register(email: String, password: String, nombre: String) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val registerUser = RegisterUser(Email(email), Password(password), nombre)
                val result = registerUseCase(registerUser)
                result.fold(
                    onSuccess = { user -> _registrationState.value = RegistrationState.Success(user) },
                    onFailure = { error -> _registrationState.value = RegistrationState.Error(error.message ?: "Error desconocido") }
                )
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Error de validación")
            }
        }
    }
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val user: UserModel) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
