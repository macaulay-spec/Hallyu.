package app.hallyu.data.repository

import app.hallyu.data.model.Drama
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DramaRepository(private val firebaseProvider: FirebaseProvider) {
    private val firestore: FirebaseFirestore? = firebaseProvider.firestore

    suspend fun getDramas(): List<Drama> {
        if (firestore == null) return emptyList()
        return try {
            val snapshot = firestore.collection("dramas").get().await()
            snapshot.toObjects(Drama::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
