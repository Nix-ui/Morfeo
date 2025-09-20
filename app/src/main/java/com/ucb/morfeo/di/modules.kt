package com.ucb.morfeo.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.ucb.morfeo.features.welcome.data.database.AppRoomDatabase
import com.ucb.morfeo.features.welcome.data.repository.UserRepository
import com.ucb.morfeo.features.welcome.domain.repository.IWelcomeRepository
import com.ucb.morfeo.features.welcome.domain.usecase.FetchUserCase
import com.ucb.morfeo.features.welcome.presentation.WelcomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppRoomDatabase.getDatabase(get()) }
    single { get<AppRoomDatabase>().userDao() }
    single<IWelcomeRepository> { UserRepository(get()) }

    factory { FetchUserCase(get()) }
    viewModel{ WelcomeViewModel(get())}
}