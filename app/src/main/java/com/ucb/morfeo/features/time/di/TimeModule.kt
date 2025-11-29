
package com.ucb.helpet.features.time.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ucb.helpet.features.time.data.local.TimeLocalDataSource
import com.ucb.helpet.features.time.data.remote.TimeApi
import com.ucb.helpet.features.time.presentation.TimeViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val timeModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://worldtimeapi.org/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TimeApi::class.java)
    }

    single {
        TimeLocalDataSource(androidContext())
    }

    viewModel {
        TimeViewModel(get(), get())
    }
}
