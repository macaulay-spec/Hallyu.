package app.hallyu.data.model

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val bio: String = ""
)

data class Drama(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val posterUrl: String = "",
    val genres: List<String> = emptyList()
)

data class Post(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String = "",
    val dramaId: String = "",
    val dramaTitle: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val likesCount: Int = 0,
    val hasSpoiler: Boolean = false
)
