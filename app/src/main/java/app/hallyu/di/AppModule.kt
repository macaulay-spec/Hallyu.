package app.hallyu.di

import app.hallyu.data.repository.AuthRepository
import app.hallyu.data.repository.DramaRepository
import app.hallyu.data.repository.FeedRepository
import app.hallyu.data.repository.firebaseModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    factory { app.hallyu.ui.main.HomeViewModel(get(), get()) }
    single { AuthRepository(get()) }
    single { DramaRepository(get()) }
    single { FeedRepository(get()) }
    factory { app.hallyu.ui.auth.AuthViewModel(get()) }
}

val appModules = listOf(firebaseModule, repositoryModule)
