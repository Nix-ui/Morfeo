package com.ucb.morfeo.features.permissions.presentation.viewmodel

import android.app.AppOpsManager
import android.content.Context
import android.os.Process
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.morfeo.features.permissions.domain.usecase.GetPermissionsGrantedUseCase
import com.ucb.morfeo.features.permissions.domain.usecase.SetPermissionsGrantedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PermissionsViewModel(
    private val getPermissionsGrantedUseCase: GetPermissionsGrantedUseCase,
    private val setPermissionsGrantedUseCase: SetPermissionsGrantedUseCase
) : ViewModel() {

    private val _notificationPermissionGranted = MutableStateFlow(false)
    private val _usageStatsPermissionGranted = MutableStateFlow(false)

    val uiState: StateFlow<PermissionsUiState> = combine(
        _notificationPermissionGranted,
        _usageStatsPermissionGranted,
        getPermissionsGrantedUseCase()
    ) { notification, usage, areAllPermissionsGranted ->
        PermissionsUiState(
            isNotificationPermissionGranted = notification,
            isUsageStatsPermissionGranted = usage,
            areAllPermissionsGranted = areAllPermissionsGranted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PermissionsUiState()
    )


    fun onNotificationPermissionResult(isGranted: Boolean) {
        _notificationPermissionGranted.value = isGranted
        updatePermissionsGranted()
    }

    fun updateUsageStatsPermissionState(context: Context) {
        val hasPermission = hasUsageStatsPermission(context)
        _usageStatsPermissionGranted.value = hasPermission
        updatePermissionsGranted()
    }

    private fun updatePermissionsGranted() {
        viewModelScope.launch {
            // The essential permission is usage stats. Notification is optional.
            setPermissionsGrantedUseCase(_usageStatsPermissionGranted.value)
        }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}

data class PermissionsUiState(
    val isNotificationPermissionGranted: Boolean = false,
    val isUsageStatsPermissionGranted: Boolean = false,
    val areAllPermissionsGranted: Boolean = false
)
