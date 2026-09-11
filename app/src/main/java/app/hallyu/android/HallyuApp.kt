package app.hallyu.android

import android.app.Application
import android.util.Log
import app.hallyu.di.appModules
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class HallyuApp : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        } catch (t: Throwable) {
            Log.e("HallyuApp", "FirebaseApp initialization handled: ${t.message}", t)
        }

        try {
            startKoin {
                androidLogger(Level.ERROR)
                androidContext(this@HallyuApp)
                modules(appModules)
            }
        } catch (t: Throwable) {
            Log.e("HallyuApp", "Koin initialization handled: ${t.message}", t)
        }
    }
}
