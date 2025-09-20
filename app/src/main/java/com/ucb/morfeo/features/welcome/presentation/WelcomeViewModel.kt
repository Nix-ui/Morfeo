package com.ucb.morfeo.features.welcome.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.welcome.domain.model.UserModel
import com.ucb.morfeo.features.welcome.domain.usecase.FetchUserCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel(
    val fetchUserCase: FetchUserCase
): ViewModel() {
    sealed class WelcomeStateUI{
        object Init: WelcomeStateUI()
        object  Loading: WelcomeStateUI()
        class  Error(val message: String): WelcomeStateUI()
        class Success(val profile: UserModel): WelcomeStateUI()
    }

    init {
        getUser()
    }

    private val _uiState = MutableStateFlow<WelcomeStateUI>(WelcomeStateUI.Init)
    val uiState : StateFlow<WelcomeStateUI> = _uiState.asStateFlow()

    fun getUser(){
        viewModelScope.launch(Dispatchers.IO) {
            fetchUserCase.invoke().collect {data ->
                _uiState.value = WelcomeStateUI.Success(data)
            }
        }
    }
}