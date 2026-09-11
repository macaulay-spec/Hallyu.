package app.hallyu.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.koin.dsl.module

val firebaseModule = module {
    single { 
        try { Firebase.auth } catch (e: Exception) { null } 
    }
    single { 
        try { Firebase.firestore } catch (e: Exception) { null } 
    }
}
