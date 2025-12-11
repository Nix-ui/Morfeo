package com.ucb.morfeo.features.time.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ucb.morfeo.features.time.data.local.TimeLocalDataSource
import com.ucb.morfeo.features.time.data.remote.TimeApi
import com.ucb.morfeo.features.time.data.repository.TimeRepositoryImpl
import com.ucb.morfeo.features.time.domain.repository.TimeRepository
import com.ucb.morfeo.features.time.domain.usecase.GetRealTimeUseCase
import com.ucb.morfeo.features.time.presentation.TimeViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val timeModule = module {
    // 1. Retrofit (API)
    single {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://worldtimeapi.org/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TimeApi::class.java)
    }

    // 2. Local DataSource (DataStore)
    single { TimeLocalDataSource(androidContext()) }

    // 3. Repository (Vincula interfaz con implementación)
    single<TimeRepository> { TimeRepositoryImpl(get(), get()) }

    // 4. UseCase (Lógica de negocio pura)
    factory { GetRealTimeUseCase(get()) }

    // 5. ViewModel
    viewModel { TimeViewModel(get()) }
}