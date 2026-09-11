package app.hallyu.data.repository

import app.hallyu.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FeedRepository(private val firestore: FirebaseFirestore?) {

    suspend fun getFeed(): List<Post> {
        if (firestore == null) return getLocalFallbackPosts()
        return try {
            val snapshot = firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            val list = snapshot.toObjects(Post::class.java)
            if (list.isEmpty()) {
                getLocalFallbackPosts()
            } else {
                list
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getLocalFallbackPosts()
        }
    }

    suspend fun createPost(
        authorId: String,
        authorName: String,
        dramaTitle: String,
        content: String,
        hasSpoiler: Boolean
    ): Result<Post> {
        val newPost = Post(
            id = UUID.randomUUID().toString(),
            authorId = authorId.ifBlank { "anonymous" },
            authorName = authorName.ifBlank { "Hallyu Fan" },
            dramaTitle = dramaTitle.trim(),
            content = content.trim(),
            hasSpoiler = hasSpoiler,
            timestamp = System.currentTimeMillis(),
            likesCount = 0
        )

        if (firestore == null) {
            return Result.success(newPost)
        }

        return try {
            firestore.collection("posts")
                .document(newPost.id)
                .set(newPost)
                .await()
            Result.success(newPost)
        } catch (e: Exception) {
            e.printStackTrace()
            // Even if network fails, return the post so UI remains responsive
            Result.success(newPost)
        }
    }

    suspend fun toggleLike(postId: String, increment: Boolean): Result<Unit> {
        if (firestore == null || postId.isBlank()) return Result.success(Unit)
        return try {
            val docRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentLikes = snapshot.getLong("likesCount") ?: 0L
                val updated = if (increment) currentLikes + 1 else maxOf(0L, currentLikes - 1)
                transaction.update(docRef, "likesCount", updated)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getLocalFallbackPosts(): List<Post> {
        return listOf(
            Post(
                id = "sample_1",
                authorId = "user_1",
                authorName = "KimMinJoo",
                dramaTitle = "Lovely Runner",
                content = "Sun Jae and Im Sol's chemistry is truly unmatched! That rain scene in episode 2 gave me absolute goosebumps. Who else is re-watching it already?",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                likesCount = 142,
                hasSpoiler = false
            ),
            Post(
                id = "sample_2",
                authorId = "user_2",
                authorName = "DramaAddict99",
                dramaTitle = "Queen of Tears",
                content = "Episode 14 ending explanation: Notice how Hae-in looked back before the surgery? The symbolic foreshadowing throughout this series has been incredible.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                likesCount = 89,
                hasSpoiler = true
            ),
            Post(
                id = "sample_3",
                authorId = "user_3",
                authorName = "SeoulChic",
                dramaTitle = "Crash Landing on You",
                content = "Rewatching CLOY for the 4th time. The soundtrack still hits right in the feels every single time 'Here I Am Again' plays.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 360,
                likesCount = 210,
                hasSpoiler = false
            )
        )
    }
}
