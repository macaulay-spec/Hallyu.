package app.hallyu.domain

/** The five curated reactions (design system §3.2 — A-4). */
enum class Reaction(val label: String, val emoji: String) {
    LIKE("Like", "❤"),
    CRUSH("Crush", "😍"),
    CRYING("Crying", "😭"),
    FIRE("Fire", "🔥"),
    CLAP("Clap", "👏");
}

enum class PostCategory(val label: String, val emoji: String) {
    REACTION("Reaction", "💬"),
    DISCUSSION("Discussion", "🗣"),
    THEORY("Theory", "🧠"),
    RECOMMENDATION("Recommendation", "✨"),
    MEME("Meme", "😂"),
    NEWS("News", "📰"),
    QUESTION("Question", "❓"),
    FAN_CONTENT("Fan content", "🎨");
}

enum class DramaStatus { UPCOMING, AIRING, COMPLETED }

data class Profile(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarEmoji: String,
    val avatarSeed: Int,
    val bio: String = "",
    val isOfficial: Boolean = false,
    val orgName: String? = null,
    val isActor: Boolean = false,
)

data class Drama(
    val id: String,
    val title: String,
    val titleKo: String,
    val year: Int,
    val network: String,
    val status: DramaStatus,
    val genres: List<String>,
    val totalEpisodes: Int,
    val currentEpisode: Int,
    val airDay: String = "",
    val airTimeKst: String = "",
    val nextEpisodeLabel: String = "",
    val posterSeed: Int,
    val posterEmoji: String,
    val overview: String = "",
)

data class Episode(
    val id: String,
    val dramaId: String,
    val number: Int,
    val title: String,
    val airLabel: String,
    val discussionCount: String,
    val isUpcoming: Boolean = false,
)

data class CastMember(
    val actorId: String,
    val character: String,
    val isLead: Boolean = true,
)

data class Actor(
    val id: String,
    val name: String,
    val nameKo: String,
    val portraitEmoji: String,
    val portraitSeed: Int,
    val followersLabel: String,
    val dramas: List<String> = emptyList(), // (dramaId, role, year) encoded
)

data class Community(
    val id: String,
    val slug: String,
    val name: String,
    val description: String,
    val membersLabel: String,
    val avatarSeed: Int,
    val avatarEmoji: String = "〰",
    val visibility: String = "Public",
    val createdBy: String = "",
    val rules: List<String> = emptyList(),
    val isOfficial: Boolean = false,
)

data class Media(val url: String = "", val aspect: Aspect = Aspect.PORTRAIT_4_5, val isVideo: Boolean = false)

enum class Aspect { PORTRAIT_4_5, WIDE_16_9 }

data class Post(
    val id: String,
    val authorId: String,
    val content: String,
    val category: PostCategory = PostCategory.DISCUSSION,
    val dramaId: String? = null,
    val episodeNumber: Int? = null,
    val spoilerLevel: Int? = null, // spoils through ep N of the tagged drama (A-3)
    val media: List<Media> = emptyList(),
    val mediaSeeds: List<Int> = emptyList(),
    val timeLabel: String = "1h",
    val reactions: Map<Reaction, Int> = emptyMap(),
    val commentCount: Int = 0,
    val repostCount: Int = 0,
    val isPinned: Boolean = false,
    val pinnedBy: String? = null,
    val reposterId: String? = null,
    val hashtag: String? = null,
    val communityId: String? = null,
    val actorId: String? = null,
)

data class Comment(
    val id: String,
    val postId: String,
    val parentId: String?,
    val authorId: String,
    val content: String,
    val timeLabel: String,
    val reactions: Map<Reaction, Int> = emptyMap(),
    val depth: Int = 0,
)

enum class NotificationCategory { CRITICAL, IMPORTANT, OPTIONAL }

data class AppNotification(
    val id: String,
    val category: NotificationCategory,
    val title: String,
    val body: String,
    val timeLabel: String,
    val actorId: String? = null,
    val icon: String = "🔔",
    val hasThumbnail: Boolean = false,
    val thumbnailSeed: Int = 0,
)

data class WatchingEntry(
    val dramaId: String,
    val status: String, // watching | completed
    val watchedThrough: Int,
    val updatedLabel: String = "",
)

data class Trend(
    val rank: Int,
    val hashtag: String,
    val velocity: String,
    val postsLabel: String,
)

/** Everything the preview world contains (the mockup world-state). */
data class WorldState(
    val profiles: Map<String, Profile>,
    val dramas: Map<String, Drama>,
    val episodes: Map<String, List<Episode>>,
    val cast: Map<String, List<CastMember>>,
    val actors: Map<String, Actor>,
    val communities: Map<String, Community>,
    val posts: Map<String, Post>,
    val feedForYou: List<String>,
    val feedFollowing: List<String>,
    val comments: Map<String, List<Comment>>,
    val notifications: List<AppNotification>,
    val trends: List<Trend>,
    val watching: Map<String, WatchingEntry>,
    val bookmarks: List<String>,
)
