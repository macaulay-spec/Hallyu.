package app.hallyu.data.repository

import android.app.Activity
import app.hallyu.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth?,
    private val firestore: FirebaseFirestore?
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    suspend fun checkAuthStatus(): Boolean {
        if (auth == null) return false
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            fetchUserProfile(firebaseUser.uid)
            true
        } else {
            false
        }
    }

    suspend fun login(email: String, pass: String): Result<Unit> {
        if (auth == null) return Result.failure(Exception("Firebase not configured"))
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            result.user?.uid?.let { fetchUserProfile(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(email: String, pass: String, username: String): Result<Unit> {
        if (auth == null || firestore == null) return Result.failure(Exception("Firebase not configured"))
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("User creation failed")
            
            val newUser = User(uid = uid, username = username, email = email)
            firestore.collection("users").document(uid).set(newUser).await()
            
            _currentUser.value = newUser
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        if (auth == null || firestore == null) return Result.failure(Exception("Firebase not configured"))
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: throw Exception("Google Sign-In failed")
            
            // Check if user exists in Firestore, if not create them
            val doc = firestore.collection("users").document(user.uid).get().await()
            if (!doc.exists()) {
                val newUser = User(
                    uid = user.uid, 
                    username = user.displayName ?: "User", 
                    email = user.email ?: "",
                    avatarUrl = user.photoUrl?.toString() ?: ""
                )
                firestore.collection("users").document(user.uid).set(newUser).await()
                _currentUser.value = newUser
            } else {
                _currentUser.value = doc.toObject(User::class.java)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithApple(activity: Activity): Result<Unit> {
        if (auth == null || firestore == null) return Result.failure(Exception("Firebase not configured"))
        return try {
            val provider = OAuthProvider.newBuilder("apple.com")
            provider.scopes = listOf("email", "name")
            
            val pending = auth.pendingAuthResult
            val result = if (pending != null) {
                pending.await()
            } else {
                auth.startActivityForSignInWithProvider(activity, provider.build()).await()
            }
            
            val user = result.user ?: throw Exception("Apple Sign-In failed")
            
            val doc = firestore.collection("users").document(user.uid).get().await()
            if (!doc.exists()) {
                val newUser = User(
                    uid = user.uid, 
                    username = user.displayName ?: "Apple User", 
                    email = user.email ?: ""
                )
                firestore.collection("users").document(user.uid).set(newUser).await()
                _currentUser.value = newUser
            } else {
                _currentUser.value = doc.toObject(User::class.java)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
        _currentUser.value = null
    }

    private suspend fun fetchUserProfile(uid: String) {
        if (firestore == null) return
        try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            if (snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                _currentUser.value = user
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
