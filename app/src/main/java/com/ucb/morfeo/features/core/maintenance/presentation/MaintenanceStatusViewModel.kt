package com.ucb.morfeo.features.core.maintenance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.core.firabase.config.domain.usecase.AppInMaintenanceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MaintenanceStatusViewModel(
    private val appInMaintenanceUseCase: AppInMaintenanceUseCase,
    val onNavigateRoute: (String) -> Unit = { }
): ViewModel() {
    sealed class MaintenanceStatusUIState{
        object Init: MaintenanceStatusUIState()
        object Loading: MaintenanceStatusUIState()
        class Error(val message: String): MaintenanceStatusUIState()
        class Success(val isMaintenance: Boolean): MaintenanceStatusUIState()
    }
    private val _maintenanceStatusState = MutableStateFlow<MaintenanceStatusUIState>(MaintenanceStatusUIState.Init)
    val maintenanceStatusState : StateFlow<MaintenanceStatusUIState> = _maintenanceStatusState.asStateFlow()

    init{
        listenForMaintenanceStatus()
    }

    private fun listenForMaintenanceStatus(){
        viewModelScope.launch(Dispatchers.IO){
            _maintenanceStatusState.value = MaintenanceStatusUIState.Loading
            appInMaintenanceUseCase.getInitialStatus()
                .onSuccess {initialStatus ->
                    _maintenanceStatusState.value = MaintenanceStatusUIState.Success(initialStatus)
                }
                .onFailure {
                    _maintenanceStatusState.value = MaintenanceStatusUIState.Error(it.message ?: "Error desconocido")
                }

            appInMaintenanceUseCase.listenForUpdates()
                .catch { exception->
                    _maintenanceStatusState.value = MaintenanceStatusUIState.Error(exception.message ?: "Error desconocido")
                }
                .collect {result ->
                    result.fold(
                        onSuccess = {
                            _maintenanceStatusState.value = MaintenanceStatusUIState.Success(it)
                        },
                        onFailure = {
                            _maintenanceStatusState.value = MaintenanceStatusUIState.Error(it.message ?: "Error desconocido")
                        }
                    )
                }
        }
    }
}