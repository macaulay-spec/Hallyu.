package app.hallyu.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.hallyu.HallyuApp
import app.hallyu.appModule
import app.hallyu.data.HallyuStore
import org.koin.android.ext.android.inject
import org.koin.core.context.startKoin

/**
 * Koin usage is confined to the Android shell: the app module creates the
 * [HallyuStore] singleton, which is handed into the shared UI. No Koin in
 * common code, so the iOS shell can wire the same store differently.
 */
class MainActivity : ComponentActivity() {
    private val store: HallyuStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        startKoin { modules(appModule) }
        setContent {
            HallyuApp(store = store)
        }
    }
}
