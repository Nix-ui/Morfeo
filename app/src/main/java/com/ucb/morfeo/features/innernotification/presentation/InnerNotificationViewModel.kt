package com.ucb.morfeo.features.innernotification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.innernotification.domain.model.NotificationModel
import com.ucb.morfeo.features.innernotification.domain.usecases.DeleteNotificationUseCase
import com.ucb.morfeo.features.innernotification.domain.usecases.GetAllNotificationsUseCase
import com.ucb.morfeo.features.innernotification.domain.usecases.MarkNotificationAsReadUseCase
import com.ucb.morfeo.features.innernotification.domain.usecases.RecivedNotificationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class InnerNotificationViewModel(
    private val getAllNotificationsUseCase: GetAllNotificationsUseCase,
    private val recivedNotificationUseCase: RecivedNotificationUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,

): ViewModel() {
    sealed class NotificationsStateUI{
        object Init: NotificationsStateUI()
        class Error(val message:String): NotificationsStateUI()
        object Loading: NotificationsStateUI()
        class Success(val notifications:List<NotificationModel>): NotificationsStateUI()
    }
    private val _uiState = MutableStateFlow<NotificationsStateUI>(NotificationsStateUI.Init)
    val uiState: StateFlow<NotificationsStateUI> = _uiState.asStateFlow()

    init{
        getAllNotifications()
    }
    fun getAllNotifications(){
        getAllNotificationsUseCase.invoke()
            .onEach { notifications->
                _uiState.value = NotificationsStateUI.Success(notifications)
            }
            .catch {e->
                _uiState.value = NotificationsStateUI.Error(e.message.toString())
            }
            .launchIn(viewModelScope)
    }

    fun markAsRead(id: Int){
        viewModelScope.launch {
            markNotificationAsReadUseCase.invoke(id)
        }
    }
    fun delete(id:Int){
        viewModelScope.launch {
            deleteNotificationUseCase.invoke(id)
        }
    }
}