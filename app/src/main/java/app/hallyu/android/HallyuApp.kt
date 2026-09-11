package app.hallyu.android

import android.app.Application
import app.hallyu.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HallyuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@HallyuApp)
            modules(appModules)
        }
    }
}
