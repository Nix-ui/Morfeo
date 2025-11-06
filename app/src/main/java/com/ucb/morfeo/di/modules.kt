package com.ucb.morfeo.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.auth0.android.jwt.JWT
import com.ucb.morfeo.features.core.firabase.config.data.repository.FirebaseConfigRepository
import com.ucb.morfeo.features.core.firabase.config.domain.repository.IFirebaseConfigRepository
import com.ucb.morfeo.features.core.firabase.config.domain.usecase.AppInMaintenanceUseCase
import com.ucb.morfeo.features.core.maintenance.presentation.MaintenanceStatusViewModel
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import com.ucb.morfeo.features.login.data.repository.LogInRepository
import com.ucb.morfeo.features.login.domain.repository.ILogInRepository
import com.ucb.morfeo.features.login.domain.usecase.FetchLogInUserUseCase
import com.ucb.morfeo.features.login.presentation.LogInViewModel
import com.ucb.morfeo.features.notification.data.repository.NotificationRepository
import com.ucb.morfeo.features.notification.domain.presentation.NotificationViewModel
import com.ucb.morfeo.features.notification.domain.repository.INotificationRepository
import com.ucb.morfeo.features.notification.domain.usecase.FetchNotificationCase
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
    single<INotificationRepository> { NotificationRepository(get()) }
    factory { FetchNotificationCase(get()) }
    viewModel{ NotificationViewModel()}

    factory { FetchUserCase(get()) }
    viewModel{ WelcomeViewModel(get())}

    single { JWTDataStore(get()) }
    single<ILogInRepository> { LogInRepository(get()) }
    factory { FetchLogInUserUseCase(get())}
    viewModel { LogInViewModel(get()) }

    single<IFirebaseConfigRepository>{ FirebaseConfigRepository() }
    factory { AppInMaintenanceUseCase(get()) }
    viewModel{ MaintenanceStatusViewModel(get())}
}