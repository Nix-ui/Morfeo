package com.ucb.morfeo.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.home.domain.model.SleepRecordScoreModel
import com.ucb.morfeo.features.home.domain.usecase.GetLastRecord
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getLastRecordUseCase: GetLastRecord,
    private val jwtDataStore: JWTDataStore
): ViewModel() {
    sealed class HomeStateUI{
        class Error(val message:String): HomeStateUI()
        class Success(val lastRecord: SleepRecordScoreModel): HomeStateUI()
        object Loading: HomeStateUI()
        object Empty: HomeStateUI()
    }
    private val _uiState = MutableStateFlow<HomeStateUI>(HomeStateUI.Loading)
    val uiState: StateFlow<HomeStateUI> = _uiState.asStateFlow()
    fun getLastRecord(){
        viewModelScope.launch {
            val userEmail= jwtDataStore.getUserMail()
            userEmail.onSuccess { email ->
                val result = getLastRecordUseCase(email)
                result.onSuccess { recordScoreModel ->
                    _uiState.value = HomeStateUI.Success(recordScoreModel)
                }.onFailure {
                    _uiState.value = HomeStateUI.Empty
                }
            }.onFailure {
                _uiState.value = HomeStateUI.Error(it?.message ?: "User not login")
            }
        }
    }
}