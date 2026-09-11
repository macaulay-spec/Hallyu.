package app.hallyu.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.hallyu.data.HallyuStore
import app.hallyu.data.Seed
import app.hallyu.design.components.PostCard
import app.hallyu.domain.Post
import app.hallyu.domain.Reaction

/**
 * Maps a post onto the wired PostCard (store state + mutations).
 * Every feed/list screen uses this so interactions stay consistent.
 */
@Composable
fun PostCardWired(post: Post, store: HallyuStore, onClick: () -> Unit, onDrama: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    val s by store.state.collectAsState()
    val author = Seed.profiles[post.authorId] ?: return
    PostCard(
        post = post,
        author = author,
        drama = Seed.dramas[post.dramaId],
        community = Seed.communities[post.communityId],
        myReactions = s.reactions[post.id] ?: emptySet(),
        bookmarked = post.id in s.bookmarks,
        spoilerHidden = store.isSpoilerHidden(post, s),
        onOpen = onClick,
        onDrama = onDrama,
        onReact = { r -> store.toggleReaction(post.id, r) },
        onRepost = { store.toggleRepost(post.id) },
        onBookmark = { store.toggleBookmark(post.id) },
        modifier = modifier,
    )
}

/** "2.4k"-style count used across threads and stats. */
fun kCount(n: Int): String = if (n >= 1000) "${n / 1000}.${(n % 1000) / 100}k" else n.toString()
