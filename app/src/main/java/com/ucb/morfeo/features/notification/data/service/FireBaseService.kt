package com.ucb.morfeo.features.notification.data.service

import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ucb.morfeo.features.notification.domain.NotificationEventBus
import com.ucb.morfeo.navigation.AppNavigator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FireBaseService: FirebaseMessagingService(){
    companion object{
        val TAG = FireBaseService::class.java.simpleName
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "Refreshed token: $token")
    }
    override fun onMessageReceived(message: RemoteMessage) {
        if(message.data.isNotEmpty()){
            Log.d(TAG, "Message data payload: ${message.data}")
            val route = message.data["PATH"]?:"/home"
            runBlocking{
                NotificationEventBus.emit(route)
            }
        }
        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }
}