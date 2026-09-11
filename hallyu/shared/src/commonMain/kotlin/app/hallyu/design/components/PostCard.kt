package app.hallyu.design.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.blur
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.hallyu.design.HallyuBrand
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuSpacing
import app.hallyu.design.HallyuType
import app.hallyu.design.PlaceholderArt
import app.hallyu.design.VerifiedSeal
import app.hallyu.domain.Community
import app.hallyu.domain.Drama
import app.hallyu.domain.Media
import app.hallyu.domain.Post
import app.hallyu.domain.PostCategory
import app.hallyu.domain.Profile
import app.hallyu.domain.Reaction

/** Posts default to Discussion; the category chip is hidden for it to reduce noise. */
val PostCategoryDefault = PostCategory.DISCUSSION

@Composable
private fun CategoryTag(category: PostCategory) {
    if (category == PostCategoryDefault) return
    Text(
        "${category.emoji} ${category.label}",
        style = HallyuType.Caption,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(HallyuColors.Surface2)
            .padding(horizontal = 6.dp, vertical = 3.dp),
    )
}

private fun reactionColor(r: Reaction): Color = when (r) {
    Reaction.LIKE -> HallyuColors.ReactLike
    Reaction.CRUSH -> HallyuColors.ReactCrush
    Reaction.CRYING -> HallyuColors.ReactCrying
    Reaction.FIRE -> HallyuColors.ReactFire
    Reaction.CLAP -> HallyuColors.ReactClap
}

/** The signature context affordance: 2:3 thumbnail + "Drama · Ep N" (design system §3.6). */
@Composable
fun DramaChip(
    drama: Drama?,
    episodeNumber: Int?,
    community: Community? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(HallyuColors.Surface2)
            .border(1.dp, HallyuColors.Line, shape)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (drama != null) {
            PlaceholderArt(
                seed = drama.posterSeed,
                emoji = drama.posterEmoji,
                modifier = Modifier.width(16.dp).height(24.dp).clip(RoundedCornerShape(4.dp)),
                emojiSize = 9f,
            )
            Spacer(Modifier.width(5.dp))
        }
        val label = when {
            drama != null && episodeNumber != null -> "${drama.title} · Ep $episodeNumber"
            drama != null -> drama.title
            community != null -> community.name
            else -> return
        }
        Text(label, style = HallyuType.Small, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun ReactionBar(
    reactions: Map<Reaction, Int>,
    mine: Set<Reaction>,
    commentCount: Int,
    repostCount: Int,
    bookmarked: Boolean,
    onReact: (Reaction) -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Show the reactions that exist on this post (♥ always), like first — mockup 11.
        val shown = Reaction.entries
            .filter { (reactions[it] ?: 0) > 0 || it in mine || it == Reaction.LIKE }
            .sortedWith(compareBy({ if (it == Reaction.LIKE) 0 else 1 }, { it.ordinal }))
        shown.forEach { r ->
            val active = r in mine
            val count = (reactions[r] ?: 0) + (if (active) 1 else 0)
            // §3.7 motion: reaction chip scales 1→1.3→1 on a 300ms spring.
            val pop = remember { Animatable(1f) }
            var skipFirst by remember { mutableStateOf(true) }
            LaunchedEffect(active) {
                if (skipFirst) {
                    skipFirst = false
                    return@LaunchedEffect
                }
                if (active) {
                    pop.snapTo(1f)
                    pop.animateTo(1.3f, spring(dampingRatio = 0.5f))
                    pop.animateTo(1f, spring(dampingRatio = 0.6f))
                }
            }
            Row(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = pop.value
                        scaleY = pop.value
                    }
                    .clip(CircleShape)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) reactionColor(r).copy(alpha = 0.16f) else Color.Transparent)
                    .clickable(onClick = { onReact(r) })
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(r.emoji, fontSize = 14.sp)
                if (count > 0) {
                    Spacer(Modifier.width(4.dp))
                    Text("$count", style = HallyuType.Small.copy(color = if (active) reactionColor(r) else HallyuColors.Text2))
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("💬", fontSize = 14.sp)
            if (commentCount > 0) {
                Spacer(Modifier.width(4.dp))
                Text("$commentCount", style = HallyuType.Small)
            }
            Spacer(Modifier.width(10.dp))
            Text("🔁", fontSize = 14.sp)
            if (repostCount > 0) {
                Spacer(Modifier.width(4.dp))
                Text("$repostCount", style = HallyuType.Small)
            }
            Spacer(Modifier.width(10.dp))
            Text("🔖", fontSize = 14.sp, color = if (bookmarked) HallyuColors.BrandBlue else HallyuColors.Text2)
        }
    }
}

/** §9 signature interaction: blur + gradient veil + tap to reveal. */
@Composable
fun SpoilerVeil(
    label: String,
    seed: Int,
    hidden: Boolean,
    onReveal: () -> Unit,
    isVideo: Boolean = false,
    modifier: Modifier = Modifier,
) {
    // §3.7 motion: blur 24→0 in 300ms ease-out, veil lifts up 12pt.
    val reveal by animateFloat(
        targetValue = if (hidden) 0f else 1f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "spoilerReveal",
    )
    Box(
        modifier = modifier
            .aspectRatio(if (isVideo) 16f / 9f else 4f / 5f)
            .clip(RoundedCornerShape(HallyuShape.Card)),
        contentAlignment = Alignment.Center,
    ) {
        PlaceholderArt(
            seed = seed,
            emoji = if (isVideo) "🎬" else "🎭",
            modifier = Modifier
                .matchParentSize()
                .blur(((1f - reveal) * 24f).dp),
            emojiSize = 48f,
        )
        if (hidden || reveal < 1f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha((1f - reveal).coerceIn(0f, 1f))
                    .background(HallyuBrand.VeilGradient),
            )
            Column(
                modifier = Modifier
                    .offset(y = (-12.dp * reveal))
                    .alpha((1f - reveal).coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(999.dp))
                    .background(HallyuColors.Ink950.copy(alpha = 0.88f))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clickable(enabled = hidden, onClick = onReveal),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("⚠ $label", style = HallyuType.BodyStrong, color = HallyuColors.Text1)
                Text("Tap to reveal", style = HallyuType.Caption, color = HallyuColors.Text2)
            }
        }
        if (isVideo && !hidden) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .alpha(reveal)
                    .clip(CircleShape)
                    .background(HallyuColors.Ink950.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center,
            ) { Text("▶", color = Color.White, fontSize = 16.sp) }
        }
        if (hidden) {
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(HallyuColors.Ink950.copy(alpha = 0.7f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(if (isVideo) "16:9" else "4:5", style = HallyuType.Caption, color = HallyuColors.Text1)
            }
        }
    }
}

@Composable
fun MediaBlock(media: Media, seed: Int, hidden: Boolean, label: String, onReveal: () -> Unit, modifier: Modifier = Modifier) {
    SpoilerVeil(label = label, seed = seed, hidden = hidden, onReveal = onReveal, isVideo = media.isVideo, modifier = modifier)
}

/** The workhorse feed item (§51 anatomy). */
@Composable
fun PostCard(
    post: Post,
    author: Profile,
    drama: Drama?,
    community: Community?,
    myReactions: Set<Reaction>,
    bookmarked: Boolean,
    spoilerHidden: Boolean,
    onOpen: () -> Unit,
    onDrama: (() -> Unit)?,
    onReact: (Reaction) -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(HallyuShape.Card)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(HallyuColors.Surface)
            .border(1.dp, HallyuColors.Line, cardShape)
            .padding(HallyuSpacing.L),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // §3.6: reposts get a hairline row above the original author row.
        if (post.reposterId != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Reposted by you —", style = HallyuType.Small, color = HallyuColors.Text3)
                Box(Modifier.weight(1f).height(1.dp).background(HallyuColors.Line))
            }
        }

        var expanded by remember { mutableStateOf(false) }
        val isLong = post.content.length > 140
        val bodyMaxLines = when {
            expanded -> Int.MAX_VALUE
            isLong -> 4
            else -> Int.MAX_VALUE
        }

        Row(verticalAlignment = Alignment.Top) {
            Avatar(author.avatarSeed, author.avatarEmoji, size = 40.dp, ring = author.isOfficial)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("@${author.username}", style = HallyuType.BodyStrong, maxLines = 1)
                    if (author.isOfficial) {
                        Spacer(Modifier.width(5.dp))
                        VerifiedSeal(size = 13.dp)
                        Spacer(Modifier.width(4.dp))
                        Text("OFFICIAL", style = HallyuType.Caption)
                    }
                }
                Text("${author.displayName} · ${post.timeLabel}", style = HallyuType.Small)
            }
            if (drama != null || community != null) {
                DramaChip(drama = drama, episodeNumber = post.episodeNumber, community = community, onClick = onDrama)
            }
        }

        if (post.isPinned) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📌", fontSize = 12.sp)
                Spacer(Modifier.width(5.dp))
                Text("Pinned by ${post.pinnedBy?.let { "@" + it } ?: "moderator"}", style = HallyuType.Caption)
            }
        }

        val first = post.media.firstOrNull()
        val seed = post.mediaSeeds.firstOrNull() ?: 0
        val veilLabel = "Spoiler · ${drama?.title ?: "This post"}${post.spoilerLevel?.let { " · Episode $it" } ?: ""}"

        if (spoilerHidden) {
            // §9: veiled posts replace the text with blurred bars — no spoiler leaks.
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(HallyuColors.Text3.copy(alpha = 0.35f)))
                Box(Modifier.fillMaxWidth(0.72f).height(10.dp).clip(RoundedCornerShape(5.dp)).background(HallyuColors.Text3.copy(alpha = 0.35f)))
            }
            if (first != null) {
                MediaBlock(media = first, seed = seed, hidden = true, label = veilLabel, onReveal = onOpen, modifier = Modifier.fillMaxWidth())
            }
        } else if (first != null && post.media.size == 1) {
            // Feed anatomy (mockup 11): single media sits beside the copy.
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MediaBlock(media = first, seed = seed, hidden = false, label = veilLabel, onReveal = onOpen, modifier = Modifier.weight(0.42f))
                Column(Modifier.weight(0.58f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(post.content, style = HallyuType.Body, maxLines = bodyMaxLines, overflow = TextOverflow.Ellipsis)
                    if (isLong && !expanded) {
                        Text("…more", style = HallyuType.Caption, color = HallyuColors.Text2, modifier = Modifier.clickable { expanded = true })
                    }
                    CategoryTag(post.category)
                }
            }
        } else {
            Text(post.content, style = HallyuType.Body, maxLines = bodyMaxLines, overflow = TextOverflow.Ellipsis)
            if (isLong && !expanded) {
                Text("…more", style = HallyuType.Caption, color = HallyuColors.Text2, modifier = Modifier.clickable { expanded = true })
            }
            CategoryTag(post.category)
            if (first != null) {
                MediaBlock(media = first, seed = seed, hidden = false, label = veilLabel, onReveal = onOpen, modifier = Modifier.fillMaxWidth())
            }
        }

        ReactionBar(
            reactions = post.reactions,
            mine = myReactions,
            commentCount = post.commentCount,
            repostCount = post.repostCount + (if (post.reposterId != null) 1 else 0),
            bookmarked = bookmarked,
            onReact = onReact,
            onRepost = onRepost,
            onBookmark = onBookmark,
        )
    }
}

/** Whole-card tap wrapper keeping the card's internal gestures intact. */
@Composable
fun ClickablePostCard(
    post: Post,
    author: Profile,
    drama: Drama?,
    community: Community?,
    myReactions: Set<Reaction>,
    bookmarked: Boolean,
    spoilerHidden: Boolean,
    onOpen: () -> Unit,
    onDrama: (() -> Unit)?,
    onReact: (Reaction) -> Unit,
    onRepost: () -> Unit,
    onBookmark: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PostCard(
        post = post,
        author = author,
        drama = drama,
        community = community,
        myReactions = myReactions,
        bookmarked = bookmarked,
        spoilerHidden = spoilerHidden,
        onOpen = onOpen,
        onDrama = onDrama,
        onReact = onReact,
        onRepost = onRepost,
        onBookmark = onBookmark,
        modifier = modifier,
    )
}


