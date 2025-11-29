package com.ucb.morfeo.features.welcome.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.ucb.morfeo.features.welcome.domain.model.UserModel
import com.ucb.morfeo.features.welcome.domain.usecase.FetchUserCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WelcomeViewModel(
    val fetchUserCase: FetchUserCase
): ViewModel() {
    sealed class WelcomeStateUI{
        object Init: WelcomeStateUI()
        object  Loading: WelcomeStateUI()
        class  Error(val message: String): WelcomeStateUI()
        class Success(val profile: UserModel): WelcomeStateUI()
    }

    private val _uiState = MutableStateFlow<WelcomeStateUI>(WelcomeStateUI.Init)
    val uiState : StateFlow<WelcomeStateUI> = _uiState.asStateFlow()


    suspend fun getToken(): String = suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(!task.isSuccessful){
                continuation.resumeWith(Result.failure(task.exception!!))
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("TOKEN", token)
            continuation.resume(token ?: "")
        }
    }
}