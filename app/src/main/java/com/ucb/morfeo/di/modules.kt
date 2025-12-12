package com.ucb.morfeo.di

import android.content.Context
import androidx.work.WorkerFactory
import com.ucb.morfeo.features.settings.data.datastore.SettingsDataStore
import com.ucb.morfeo.features.settings.presentation.SettingsViewModel
import com.auth0.android.jwt.JWT
import com.ucb.morfeo.features.core.firabase.config.data.repository.FirebaseConfigRepository
import com.ucb.morfeo.features.core.firabase.config.domain.repository.IFirebaseConfigRepository
import com.ucb.morfeo.features.core.firabase.config.domain.usecase.AppInMaintenanceUseCase
import com.ucb.morfeo.features.core.maintenance.presentation.MaintenanceStatusViewModel
import com.ucb.morfeo.features.innernotification.data.NotificationManagerHelper
import com.ucb.morfeo.features.innernotification.data.datasource.LocalNotificationDataSource
import com.ucb.morfeo.features.innernotification.data.datastore.local.LocalNotificationDataStore
import com.ucb.morfeo.features.innernotification.data.repository.NotificationRepository
import com.ucb.morfeo.features.innernotification.domain.repository.INotificationRepository
import com.ucb.morfeo.features.innernotification.domain.usecases.DeleteNotificationUseCase
import com.ucb.morfeo.features.innernotification.domain.usecases.GetAllNotificationsUseCase
import com.ucb.morfeo.features.innernotification.domain.usecases.MarkNotificationAsReadUseCase
import com.ucb.morfeo.features.innernotification.domain.usecases.RecivedNotificationUseCase
import com.ucb.morfeo.features.innernotification.presentation.InnerNotificationViewModel
import com.ucb.morfeo.features.login.data.datasource.JWTDataStore
import com.ucb.morfeo.features.login.data.repository.CheckSessionRepository
import com.ucb.morfeo.features.login.data.repository.LogInRepository
import com.ucb.morfeo.features.login.domain.repository.ICheckSessionRepository
import com.ucb.morfeo.features.login.domain.repository.ILogInRepository
import com.ucb.morfeo.features.login.domain.usecase.CheckSessionUseCase
import com.ucb.morfeo.features.login.domain.usecase.FetchLogInUserUseCase
import com.ucb.morfeo.features.login.presentation.LogInViewModel
import com.ucb.morfeo.features.notification.data.worker.SleepNotificationWorker
import com.ucb.morfeo.features.register.data.repository.RegisterRepository
import com.ucb.morfeo.features.register.domain.repository.IRegisterRepository
import com.ucb.morfeo.features.register.domain.usecase.RegisterUseCase
import com.ucb.morfeo.features.register.presentation.RegisterViewModel
//import com.ucb.morfeo.features.notification.data.repository.NotificationRepository
//import com.ucb.morfeo.features.notification.domain.presentation.NotificationViewModel
//import com.ucb.morfeo.features.notification.domain.repository.INotificationRepository
//import com.ucb.morfeo.features.notification.domain.usecase.FetchNotificationCase
import com.ucb.morfeo.features.splash.presentation.SplashViewModel
import com.ucb.morfeo.features.welcome.data.database.AppRoomDatabase
import com.ucb.morfeo.features.welcome.data.repository.UserRepository
import com.ucb.morfeo.features.welcome.domain.repository.IWelcomeRepository
import com.ucb.morfeo.features.welcome.domain.usecase.FetchUserCase
import com.ucb.morfeo.features.welcome.presentation.WelcomeViewModel
import com.ucb.morfeo.features.core.database.dao.SleepDao
import com.ucb.morfeo.features.week.data.repository.WeeklyRepositoryImpl
import com.ucb.morfeo.features.week.domain.repository.WeeklyRepository
import com.ucb.morfeo.features.week.domain.usecase.CalculateConsistencyUseCase
import com.ucb.morfeo.features.week.domain.usecase.GetWeeklySummaryUseCase
import com.ucb.morfeo.features.week.presentation.viewmodel.WeeklyDetailsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // 📦 ROOM DATABASE
    single { AppRoomDatabase.getDatabase(get()) }
    single { get<AppRoomDatabase>().userDao() }

    // 🛌 SLEEP DATABASE
    single { get<AppRoomDatabase>().sleepDao() }

    // 👤 WELCOME / USER
    single<IWelcomeRepository> { UserRepository(get()) }
    factory { FetchUserCase(get()) }
    viewModel { WelcomeViewModel(get()) }

//    // 🔔 NOTIFICATIONS
//    single<INotificationRepository> { NotificationRepository(get()) }
//    factory { FetchNotificationCase(get()) }
//    viewModel { NotificationViewModel() }

    // 🔐 LOGIN
    single { JWTDataStore(get()) }
    single<ILogInRepository> { LogInRepository(get()) }
    factory { FetchLogInUserUseCase(get()) }
    viewModel { LogInViewModel(get()) }

    // 📝 REGISTER
    single<IRegisterRepository> { RegisterRepository(get()) }
    factory { RegisterUseCase(get()) }
    viewModel { RegisterViewModel(get()) }

    // 🛠️ MANTENIMIENTO
    single<IFirebaseConfigRepository> { FirebaseConfigRepository() }
    factory { AppInMaintenanceUseCase(get()) }
    viewModel { MaintenanceStatusViewModel(get()) }

    // 💾 CHECK SESSION / SPLASH
    single<ICheckSessionRepository> { CheckSessionRepository(get()) }
    factory { CheckSessionUseCase(get()) }
    viewModel { SplashViewModel(get()) }

    // ⚙️ SETTINGS
    single { SettingsDataStore(get()) }
    viewModel { SettingsViewModel(get(), get()) }

    //Inner Notification
    single{ get<AppRoomDatabase>().notificationDao() }
    single { LocalNotificationDataSource(get()) }
    single<INotificationRepository>{ NotificationRepository(get()) }
    single { GetAllNotificationsUseCase(get()) }
    single { MarkNotificationAsReadUseCase(get()) }
    single { RecivedNotificationUseCase(get()) }
    single { DeleteNotificationUseCase(get()) }
    viewModel { InnerNotificationViewModel(
        get(),get(),
    get(),get()) }



    single { NotificationManagerHelper(androidContext()) }

    //Week Module
    factory { GetWeeklySummaryUseCase(get()) }
    factory { CalculateConsistencyUseCase() }
    single<WeeklyRepository> { WeeklyRepositoryImpl(get(), get()) }
    viewModel { WeeklyDetailsViewModel(get(), get()) }
}
