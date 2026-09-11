package app.hallyu.data.repository

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.koin.dsl.module

class FirebaseProvider(private val context: Context) {
    init {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    val auth: FirebaseAuth?
        get() = try {
            Firebase.auth
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }

    val firestore: FirebaseFirestore?
        get() = try {
            Firebase.firestore
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
}

val firebaseModule = module {
    single { FirebaseProvider(get()) }
}
