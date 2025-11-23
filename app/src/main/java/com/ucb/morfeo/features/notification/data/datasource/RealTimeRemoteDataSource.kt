package com.ucb.morfeo.features.notification.data.datasource

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RealTimeRemoteDataSource {
    suspend fun notificationEvents(): Flow<String> = callbackFlow {
        val callback = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
                Log.d("NOTIFICATION", error.toException().toString())
                close(error.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val path = snapshot.getValue(String::class.java)
                Log.d("NOTIFICATION", path.toString())
                if (path != null) {
                    trySend(path)
                }
            }
        }
        val database = Firebase.database
        val myRef = database.getReference("notification")
        myRef.addValueEventListener(callback)
        awaitClose {
            myRef.removeEventListener(callback)
        }
    }
}