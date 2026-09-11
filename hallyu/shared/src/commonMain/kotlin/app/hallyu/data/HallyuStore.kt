package app.hallyu.data

import app.hallyu.domain.Community
import app.hallyu.domain.Comment
import app.hallyu.domain.Post
import app.hallyu.domain.PostCategory
import app.hallyu.domain.Reaction
import app.hallyu.domain.WatchingEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.module.module

data class Session(val username: String, val onboarded: Boolean)

/**
 * Preview single source of truth. In the backend phase this is replaced by
 * repositories over Firebase (owner: product) behind the same surface — no UI changes.
 */
data class HallyuState(
    val session: Session? = null,
    val followedDramas: Set<String> = setOf("d_mbb"),
    val followedActors: Set<String> = setOf("a_kimsoohyun"),
    val followedUsers: Set<String> = setOf("u_sunny", "u_ji", "u_minsu", "u_tvn"),
    val joinedCommunities: Set<String> = setOf("c_watch", "c_romance", "c_theory"),
    val pendingCommunities: Set<String> = setOf("c_sageuk"),
    val watching: Map<String, WatchingEntry> = Seed.watching,
    val watchedThrough: Map<String, Int> = mapOf("d_mbb" to 11, "d_para" to 4, "d_bona" to 8, "d_qot" to 16),
    val bookmarks: Set<String> = Seed.bookmarks.toSet(),
    val reactions: Map<String, Set<Reaction>> = emptyMap(),
    val reposts: Set<String> = emptySet(),
    val revealedPosts: Set<String> = emptySet(),
    val revealedEpisode: Pair<String, Int>? = null,
    val mutedDramas: Set<String> = setOf("d_mmh", "d_lr"),
    val notificationsRead: Set<String> = emptySet(),
    val communities: Map<String, Community> = Seed.communities,
    val extraPosts: Map<String, List<String>> = emptyMap(), // feed key -> prepended post ids
    val extraComments: Map<String, List<Comment>> = emptyMap(),
    val privateProfile: Boolean = false,
    val pushEnabled: Boolean = true,
    val offline: Boolean = false, // dev hook: simulates a dead network over cached data
    val spoilerPolicy: String = "by_progress", // by_progress | always_blur | never_blur
    val reminders: Set<String> = emptySet(), // "dramaId:ep"
    val posts: Map<String, Post> = Seed.posts,
    val commentReactions: Map<String, Set<Reaction>> = emptyMap(), // keyed by comment id
    val blockedUsers: List<String> = Seed.blockedUsers,
)

class HallyuStore {
    val state = MutableStateFlow(HallyuState())
    val ready = MutableStateFlow(false)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        // Preview "fetch" latency so skeleton states are real, not skipped.
        scope.launch {
            delay(700)
            ready.value = true
        }
    }

    private inline fun update(block: (HallyuState) -> HallyuState) {
        state.update(block)
    }

    // ---- Session ----
    fun completeAuth(username: String) = update { it.copy(session = Session(username, onboarded = false)) }
    fun completeOnboarding() = update { it.copy(session = it.session?.copy(onboarded = true)) }
    fun logout() = update { HallyuState() }

    // ---- Social graph ----
    fun toggleFollowDrama(id: String) = update { s -> s.copy(followedDramas = toggle(s.followedDramas, id)) }
    fun toggleFollowActor(id: String) = update { s -> s.copy(followedActors = toggle(s.followedActors, id)) }
    fun toggleFollowUser(id: String) = update { s -> s.copy(followedUsers = toggle(s.followedUsers, id)) }
    fun toggleJoinCommunity(id: String) = update { s ->
        val pending = id in s.pendingCommunities
        s.copy(
            joinedCommunities = if (pending || id !in s.joinedCommunities) s.joinedCommunities + id else s.joinedCommunities - id,
            pendingCommunities = s.pendingCommunities - id,
        )
    }
    fun createCommunity(name: String, description: String): String {
        val id = "c_user_${(state.value.communities.size + 1)}"
        val c = Community(
            id = id,
            slug = name.lowercase().replace(Regex("[^a-z0-9]+"), "-"),
            name = name,
            description = description,
            membersLabel = "1",
            avatarSeed = (state.value.communities.size * 7) % 6,
            createdBy = "u_hana",
        )
        update { it.copy(communities = it.communities + (id to c), joinedCommunities = it.joinedCommunities + id) }
        return id
    }

    // ---- Post interactions ----
    fun toggleReaction(postId: String, r: Reaction) = update { s ->
        val cur = s.reactions[postId] ?: emptySet()
        s.copy(reactions = s.reactions + (postId to (if (r in cur) cur - r else cur + r)))
    }
    fun toggleBookmark(postId: String) = update { s -> s.copy(bookmarks = toggle(s.bookmarks, postId)) }
    fun toggleRepost(postId: String) = update { s -> s.copy(reposts = toggle(s.reposts, postId)) }

    // ---- Spoiler system (§9) ----
    fun revealPost(postId: String) = update { it.copy(revealedPosts = it.revealedPosts + postId) }
    fun revealEpisodeForSession(dramaId: String, episode: Int) = update { it.copy(revealedEpisode = dramaId to episode) }
    fun markWatched(dramaId: String, episode: Int) = update { s ->
        val ep = episode.coerceAtLeast(s.watchedThrough[dramaId] ?: 0)
        val entry = s.watching[dramaId]?.copy(watchedThrough = ep, updatedLabel = "just now") ?: WatchingEntry(dramaId, "watching", ep, "just now")
        s.copy(watchedThrough = s.watchedThrough + (dramaId to ep), watching = s.watching + (dramaId to entry))
    }
    fun isSpoilerHidden(post: Post, s: HallyuState): Boolean {
        val level = post.spoilerLevel ?: return false
        val dramaId = post.dramaId ?: return false
        if (s.spoilerPolicy == "never_blur") return false
        if (post.id in s.revealedPosts) return false
        if (s.revealedEpisode?.first == dramaId && (s.revealedEpisode?.second ?: 0) >= level) return false
        if (s.spoilerPolicy == "always_blur") return true
        return (s.watchedThrough[dramaId] ?: 0) < level
    }

    // ---- Notifications ----
    fun markAllNotificationsRead() = update { it.copy(notificationsRead = Seed.notifications.map { n -> n.id }.toSet()) }

    // ---- Muting / safety ----
    fun toggleMuteDrama(id: String) = update { s -> s.copy(mutedDramas = toggle(s.mutedDramas, id)) }
    fun unblockUser(id: String) = update { s -> s.copy(blockedUsers = s.blockedUsers - id) }
    fun setPrivate(v: Boolean) = update { it.copy(privateProfile = v) }
    fun setPush(v: Boolean) = update { it.copy(pushEnabled = v) }
    fun setSpoilerPolicy(v: String) = update { it.copy(spoilerPolicy = v) }

    // ---- Dev hooks ----
    fun setOffline(v: Boolean) = update { it.copy(offline = v) }

    // ---- Reminders ----
    fun toggleReminder(dramaId: String, episode: Int) = update { s ->
        val key = "$dramaId:$episode"
        s.copy(reminders = toggle(s.reminders, key))
    }

    // ---- Composer ----
    fun addPost(
        content: String,
        category: PostCategory,
        dramaId: String?,
        episodeNumber: Int?,
        spoilerLevel: Int?,
        hashtag: String? = null,
    ): String {
        val id = "p_user_${state.value.posts.size + 1}"
        val post = Post(
            id = id, authorId = "u_hana", content = content, category = category,
            dramaId = dramaId, episodeNumber = episodeNumber, spoilerLevel = spoilerLevel,
            timeLabel = "now", hashtag = hashtag,
        )
        update { s ->
            s.copy(
                posts = s.posts + (id to post),
                extraPosts = s.extraPosts + ("following" to listOf(id) + (s.extraPosts["following"] ?: emptyList())),
            )
        }
        return id
    }

    fun addComment(postId: String, content: String, parentId: String?): String {
        val id = "c_user_${state.value.extraComments.values.sumOf { it.size } + 1}"
        val depth = if (parentId != null) ((Seed.comments[postId]?.find { it.id == parentId }?.depth ?: 0) + 1).coerceAtMost(2) else 0
        val c = Comment(id, postId, parentId, "u_hana", content, "now", emptyMap(), depth)
        update { s -> s.copy(extraComments = s.extraComments + (postId to (s.extraComments[postId] ?: emptyList()) + c)) }
        return id
    }

    fun toggleCommentReaction(commentId: String, r: Reaction) = update { s ->
        val cur = s.commentReactions[commentId] ?: emptySet()
        s.copy(commentReactions = s.commentReactions + (commentId to (if (r in cur) cur - r else cur + r)))
    }
}

private fun toggle(set: Set<String>, id: String): Set<String> =
    if (id in set) set - id else set + id

val appModule = module {
    single { HallyuStore() }
}
