package app.hallyu.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import app.hallyu.data.HallyuStore
import app.hallyu.data.Seed
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuType
import app.hallyu.design.Avatar
import app.hallyu.design.components.DramaChip
import app.hallyu.design.components.FollowButton
import app.hallyu.design.components.HallyuSheet
import app.hallyu.design.components.IconCircle
import app.hallyu.design.components.ReactionBar
import app.hallyu.design.components.MediaBlock
import app.hallyu.design.components.SheetAction
import app.hallyu.design.components.TopBar
import app.hallyu.domain.Comment
import app.hallyu.domain.Post
import app.hallyu.domain.Reaction
import app.hallyu.navigation.Routes
import app.hallyu.ui.kCount

@Composable
fun PostDetailScreen(store: HallyuStore, postId: String, onBack: () -> Unit, nav: (String) -> Unit) {
    val s by store.state.collectAsState()
    val post = s.posts[postId] ?: return
    val author = Seed.profiles[post.authorId] ?: return
    val drama = Seed.dramas[post.dramaId]
    val community = Seed.communities[post.communityId]
    val hidden = store.isSpoilerHidden(post, s)
    var text by remember { mutableStateOf("") }
    var replyParent by remember { mutableStateOf<String?>(null) }
    var linkCopied by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var confirmReport by remember { mutableStateOf(false) }
    val reposted = post.id in s.reposts
    val followingAuthor = author.id in s.followedUsers
    val mutedDrama = drama?.id?.let { it in s.mutedDramas } ?: false

    val menuActions = buildList {
        if (hidden) add(SheetAction("Reveal spoiler for this session", onClick = { store.revealPost(post.id) }))
        add(SheetAction(if (bookmarked) "Remove bookmark" else "Bookmark post", onClick = { store.toggleBookmark(post.id) }))
        add(SheetAction(if (reposted) "Undo repost" else "Repost", onClick = { store.toggleRepost(post.id) }))
        add(SheetAction("Copy link", onClick = { linkCopied = true }))
        add(SheetAction(if (followingAuthor) "Unfollow @${author.username}" else "Follow @${author.username}", onClick = { store.toggleFollowUser(author.id) }))
        drama?.let { d -> add(SheetAction(if (mutedDrama) "Unmute ${d.title}" else "Mute ${d.title}", onClick = { store.toggleMuteDrama(d.id) })) }
        if (confirmReport) {
            add(SheetAction("Confirm report", destructive = true, onClick = { confirmReport = false; menuOpen = false }))
        } else {
            add(SheetAction("Report post", destructive = true, onClick = { confirmReport = true }))
        }
    }

    val allComments = (Seed.comments[postId] ?: emptyList()) + (s.extraComments[postId] ?: emptyList())
    val replyHandle = replyParent?.let { pid -> allComments.find { it.id == pid }?.let { c -> Seed.profiles[c.authorId]?.username } }
    val placeholder = replyHandle?.let { "@$it " } ?: "Add a comment…"
    val myReactions = s.reactions[post.id] ?: emptySet()
    val bookmarked = post.id in s.bookmarks

    Box(Modifier.fillMaxSize()) {
    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        TopBar(
            title = "Post",
            onBack = onBack,
            actions = {
                IconCircle(
                    icon = if (bookmarked) "🔖" else "📑",
                    onClick = { store.toggleBookmark(post.id) },
                )
                Spacer(Modifier.width(8.dp))
                IconCircle(icon = "⋯", onClick = { menuOpen = true })
            },
        )

        Column(Modifier.padding(horizontal = 20.dp)) {
            // Author row with follow (mockup 17)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(seed = author.avatarSeed, emoji = author.avatarEmoji, size = 48.dp, ring = author.isOfficial)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("@${author.username}", style = HallyuType.BodyStrong, color = HallyuColors.Text1, maxLines = 1)
                        if (author.isOfficial) {
                            Spacer(Modifier.width(6.dp))
                            Text("OFFICIAL", style = HallyuType.Caption)
                        }
                    }
                    Text("${author.displayName} · ${post.timeLabel}", style = HallyuType.Small)
                }
                FollowButton(
                    following = author.id in s.followedUsers,
                    onClick = { store.toggleFollowUser(author.id) },
                )
            }
            Spacer(Modifier.height(14.dp))

            if (hidden) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).background(HallyuColors.Text3.copy(alpha = 0.35f)))
                    Box(Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).background(HallyuColors.Text3.copy(alpha = 0.35f)))
                    Box(Modifier.fillMaxWidth(0.8f).height(12.dp).clip(RoundedCornerShape(6.dp)).background(HallyuColors.Text3.copy(alpha = 0.35f)))
                }
            } else {
                Text(post.content, style = HallyuType.Body)
            }

            if (post.media.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                MediaBlock(
                    media = post.media.first(),
                    seed = post.mediaSeeds.firstOrNull() ?: 0,
                    hidden = hidden,
                    label = "Spoiler · ${drama?.title ?: "This post"}${post.spoilerLevel?.let { " · Episode $it" } ?: ""}",
                    onReveal = { store.revealPost(post.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (drama != null || community != null) {
                    DramaChip(
                        drama = drama,
                        episodeNumber = post.episodeNumber,
                        community = community,
                        onClick = if (drama != null) { nav(Routes.drama(drama.id)) } else { community?.let { nav(Routes.community(it.slug)) } },
                    )
                }
                if (post.category != app.hallyu.domain.PostCategory.DISCUSSION) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HallyuColors.Surface2)
                            .border(1.dp, HallyuColors.Line)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text("${post.category.emoji} ${post.category.label.uppercase()}", style = HallyuType.Caption)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(HallyuColors.Line))
            Spacer(Modifier.height(12.dp))

            ReactionBar(
                reactions = post.reactions,
                mine = myReactions,
                commentCount = allComments.size,
                repostCount = post.repostCount,
                bookmarked = bookmarked,
                onReact = { r -> store.toggleReaction(post.id, r) },
                onRepost = { store.toggleRepost(post.id) },
                onBookmark = { store.toggleBookmark(post.id) },
            )
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(1f))
                IconCircle("↗", onClick = { linkCopied = true })
            }
            if (linkCopied) {
                Text(
                    "✓ Link copied — ${Routes.deepLink(Routes.post(postId))}",
                    style = HallyuType.Caption,
                    color = HallyuColors.Info,
                    modifier = Modifier.align(Alignment.End),
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(18.dp))
            Text("COMMENTS (${allComments.size})", style = HallyuType.Caption)
            Spacer(Modifier.height(12.dp))

            // Composer
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(seed = 0, emoji = "👩", size = 30.dp)
                Spacer(Modifier.width(10.dp))
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(HallyuColors.Surface2)
                        .border(1.dp, HallyuColors.Line)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    SelectionContainer {
                        BasicTextField(
                            value = text,
                            onValueChange = { text = it },
                            singleLine = true,
                            textStyle = HallyuType.Body.copy(color = HallyuColors.Text1),
                            cursorBrush = SolidColor(HallyuColors.Coral),
                        )
                    }
                    if (text.isEmpty()) {
                        Text(placeholder, style = HallyuType.Body, color = if (replyHandle != null) HallyuColors.Coral else HallyuColors.Text3)
                    }
                }
                Spacer(Modifier.width(10.dp))
                IconCircle(
                    icon = "➤",
                    onClick = {
                        if (text.isNotBlank()) {
                            store.addComment(postId, text, replyParent)
                            text = ""
                            replyParent = null
                        }
                    },
                )
            }
            Spacer(Modifier.height(18.dp))

            if (allComments.isEmpty()) {
                Text("Be the first to comment.", style = HallyuType.Small)
            } else {
                allComments.forEach { c ->
                    CommentRow(c = c, store = store, onReply = { parent, _ -> replyParent = parent })
                    Spacer(Modifier.height(16.dp))
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    HallyuSheet(
        visible = menuOpen,
        onDismiss = {
            menuOpen = false
            confirmReport = false
        },
        actions = menuActions,
    )
    }
}

@Composable
private fun CommentRow(c: Comment, store: HallyuStore, onReply: (String, String) -> Unit) {
    val s by store.state.collectAsState()
    val author = Seed.profiles[c.authorId]
    if (author == null) return
    val liked = Reaction.LIKE in (s.commentReactions[c.id] ?: emptySet())
    val count = (c.reactions[Reaction.LIKE] ?: 0) + (if (liked) 1 else 0)
    Column(Modifier.padding(start = (c.depth * 16).dp)) {
        Row {
            Avatar(seed = author.avatarSeed, emoji = author.avatarEmoji, size = 30.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("@${author.username}", style = HallyuType.BodyStrong, color = HallyuColors.Text1)
                    if (c.parentId != null) {
                        Spacer(Modifier.width(6.dp))
                        Text("replying", style = HallyuType.Caption)
                    }
                    Spacer(Modifier.weight(1f))
                    Text(c.timeLabel, style = HallyuType.Caption)
                }
                Spacer(Modifier.height(4.dp))
                Text(c.content, style = HallyuType.Body)
                Spacer(Modifier.height(6.dp))
                Row {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { store.toggleCommentReaction(c.id, Reaction.LIKE) }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            "♥ ${kCount(count)}",
                            style = HallyuType.Caption,
                            color = if (liked) HallyuColors.Coral else HallyuColors.Text3,
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Reply",
                        style = HallyuType.Caption,
                        color = HallyuColors.Info,
                        modifier = Modifier.clickable { onReply(c.id, author.username) },
                    )
                }
            }
        }
    }
}
