package com.ucb.morfeo.core.di

import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager

// Esta clase evita la inicialización automática por defecto de WorkManager.
class CustomWorkManagerInitializer : Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        // Devuelve una instancia "falsa" que no se usa,
        // ya que la real la configuraremos nosotros en App.kt
        val configuration = Configuration.Builder().build()
        WorkManager.initialize(context, configuration)
        return WorkManager.getInstance(context)
    }

    // Devuelve una lista vacía para que no dependa de otros inicializadores.
    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
