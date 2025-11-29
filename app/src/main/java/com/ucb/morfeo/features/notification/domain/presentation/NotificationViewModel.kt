package com.ucb.morfeo.features.notification.domain.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.notification.domain.NotificationEventBus
import com.ucb.morfeo.features.notification.domain.usecase.FetchNotificationCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    //val fetchNotificationCase: FetchNotificationCase
): ViewModel() {
    sealed class NotificationStateUI{
        object Init: NotificationStateUI()
        object  Loading: NotificationStateUI()
        class  Error(val message: String): NotificationStateUI()
        class Success(val notification: String): NotificationStateUI()
    }
    private val _uiState = MutableStateFlow<NotificationStateUI>(NotificationStateUI.Init)
    val uiState : StateFlow<NotificationStateUI> = _uiState.asStateFlow()
    fun getNotification(){
        viewModelScope.launch(Dispatchers.IO) {
//            fetchNotificationCase.invoke().collect {data ->
//                _uiState.value = NotificationStateUI.Success(data)
////            }
            NotificationEventBus.events.collect{route->
                _uiState.value = NotificationStateUI.Success(route)
            }
        }
    }
    fun onNavigate(){
        _uiState.value = NotificationStateUI.Init
    }
}