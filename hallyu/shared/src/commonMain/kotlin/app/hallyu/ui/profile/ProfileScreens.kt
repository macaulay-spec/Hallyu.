package app.hallyu.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Constraints
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.hallyu.data.HallyuStore
import app.hallyu.data.Seed
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuType
import app.hallyu.design.Avatar
import app.hallyu.design.components.ButtonVariant
import app.hallyu.design.components.Chip
import app.hallyu.design.CommunityAvatar
import app.hallyu.design.components.EmptyState
import app.hallyu.design.components.FollowButton
import app.hallyu.design.components.GradientProgress
import app.hallyu.design.components.HallyuButton
import app.hallyu.design.PlaceholderArt
import app.hallyu.design.components.SectionHeader
import app.hallyu.design.components.TopBar
import app.hallyu.domain.Post
import app.hallyu.navigation.Routes
import app.hallyu.ui.PostCardWired
import app.hallyu.ui.kCount

private fun followingCount(store: HallyuStore): Int {
    val s = store.state.value
    return s.followedUsers.size + s.followedDramas.size + s.followedActors.size + s.joinedCommunities.size
}

@Composable
fun ProfileScreen(store: HallyuStore, nav: (String) -> Unit) {
    val s by store.state.collectAsState()
    val me = Seed.profiles.getValue("u_hana")
    val myPosts = s.posts.values.filter { it.authorId == "u_hana" }

    val savedCount = (89 + (s.bookmarks.size - 3)).coerceAtLeast(0)

    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(200.dp)) {
            PlaceholderArt(seed = 10, emoji = "🌃", modifier = Modifier.fillMaxSize(), emojiSize = 64f)
        }
        Column(Modifier.padding(horizontal = 20.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = (-40).dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Avatar(seed = 0, emoji = me.avatarEmoji, size = 84.dp, ring = true)
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier
                        .size(36.dp)
                        .align(Alignment.Bottom)
                        .clip(RoundedCornerShape(999.dp))
                        .background(HallyuColors.Surface2)
                        .border(1.dp, HallyuColors.Line)
                        .clickable { nav(Routes.PROFILE_SETTINGS) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("⌄", style = HallyuType.BodyStrong)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(me.displayName, style = HallyuType.H1)
            Text("@${me.username}", style = HallyuType.Small, color = HallyuColors.Text3)
            Spacer(Modifier.height(8.dp))
            Text(me.bio, style = HallyuType.Body, color = HallyuColors.Text2)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                StatCell("214", "Following", onClick = { nav(Routes.PROFILE_FOLLOWING) }, modifier = Modifier.weight(0.8f))
                StatCell("1.2k", "Followers", onClick = { nav(Routes.PROFILE_FOLLOWERS) }, modifier = Modifier.weight(0.8f))
                StatCell(savedCount.toString(), "Saved", onClick = { nav(Routes.PROFILE_SAVED) }, modifier = Modifier.weight(0.8f))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .border(1.dp, HallyuColors.BrandBlue)
                        .clickable { nav(Routes.PROFILE_SETTINGS) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text("Edit profile", style = HallyuType.Small, color = HallyuColors.BrandBlue)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth()) {
                UnderlineTab("Posts", active = true, onClick = { nav(Routes.PROFILE_POSTS) })
                UnderlineTab("Watching", active = false, onClick = { nav(Routes.PROFILE_WATCHING) })
                UnderlineTab("Communities", active = false, onClick = { nav(Routes.PROFILE_COMMUNITIES) })
                UnderlineTab("Saved", active = false, onClick = { nav(Routes.PROFILE_SAVED) })
            }
            Spacer(Modifier.height(14.dp))
            PostGrid(myPosts, store, nav)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCell(value: String, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = HallyuType.Title)
        Text(label, style = HallyuType.Caption)
    }
}

/** Underline-style profile tabs (mockup 16): active = text-1 + brand underline. */
@Composable
private fun UnderlineTab(label: String, active: Boolean, onClick: () -> Unit) {
    Column(
        Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            style = HallyuType.BodyStrong.copy(color = if (active) HallyuColors.Text1 else HallyuColors.Text3),
        )
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth(0.7f).height(2.dp).clip(RoundedCornerShape(999.dp)).background(if (active) HallyuColors.BrandBlue else androidx.compose.ui.graphics.Color.Transparent))
    }
}

@Composable
fun PostGrid(posts: List<Post>, store: HallyuStore, nav: (String) -> Unit) {
    if (posts.isEmpty()) {
        EmptyState(title = "No posts yet", body = "Your posts will appear here.")
        return
    }
    // docs/03 §3.7: the profile grid is a true masonry — each tile keeps its own
    // aspect ratio and drops into the shortest column, so nothing gets stretched.
    MasonryGrid(Modifier.fillMaxWidth(), columns = 2, gap = 8.dp) {
        posts.forEach { p ->
            PostTile(p, store, Modifier.fillMaxWidth()) { nav(Routes.post(p.id)) }
        }
    }
}

@Composable
private fun MasonryGrid(
    modifier: Modifier = Modifier,
    columns: Int,
    gap: Dp,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        with(LocalDensity.current) {
            val gapPx = gap.roundToPx()
            val colWidthPx = ((constraints.maxWidth - gapPx * (columns - 1)) / columns).coerceAtLeast(1)
            val fixedWidth = Constraints.fixed(colWidthPx)
            val colHeights = IntArray(columns)
            val placed = ArrayList<Triple<Placeable, Int, Int>>(measurables.size)
            measurables.forEach { m ->
                val placeable = m.measure(fixedWidth)
                var col = 0
                for (c in 1 until columns) if (colHeights[c] < colHeights[col]) col = c
                placed.add(Triple(placeable, col * (colWidthPx + gapPx), colHeights[col]))
                colHeights[col] += placeable.height + gapPx
            }
            val totalHeight = ((colHeights.maxOrNull() ?: 0) - (if (placed.isNotEmpty()) gapPx else 0)).coerceAtLeast(0)
            layout(constraints.maxWidth, totalHeight) {
                placed.forEach { (p, x, y) -> p.place(x, y) }
            }
        }
    }
}

@Composable
private fun PostTile(p: Post, store: HallyuStore, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val liked = app.hallyu.domain.Reaction.LIKE in (store.state.value.reactions[p.id] ?: emptySet())
    val bookmarked = p.id in store.state.value.bookmarks
    val epLabel = p.episodeNumber?.let { "EP $it" }
    Column(
        modifier
            .clip(RoundedCornerShape(HallyuShape.Card))
            .background(HallyuColors.Surface)
            .border(1.dp, HallyuColors.Line)
            .clickable(onClick = onClick),
    ) {
        if (p.media.isNotEmpty()) {
            Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
                PlaceholderArt(
                    seed = p.mediaSeeds.firstOrNull() ?: 0,
                    emoji = if (p.media.first().isVideo) "🎬" else "✍",
                    modifier = Modifier.fillMaxSize(),
                )
                if (bookmarked) {
                    Text(
                        "♡",
                        style = HallyuType.BodyStrong,
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    )
                }
                if (epLabel != null) {
                    Text(
                        epLabel,
                        style = HallyuType.Caption,
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                if (epLabel != null) {
                    Text(epLabel, style = HallyuType.Caption)
                    Spacer(Modifier.height(6.dp))
                }
                Text(p.content, style = HallyuType.Body, maxLines = 4)
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("♥", style = HallyuType.Small, color = if (liked) HallyuColors.Coral else HallyuColors.Text3)
            Spacer(Modifier.width(4.dp))
            Text(kCount(p.reactions[app.hallyu.domain.Reaction.LIKE] ?: 0), style = HallyuType.Caption)
            Spacer(Modifier.weight(1f))
            if (p.dramaId != null && epLabel == null) {
                Text(Seed.dramas[p.dramaId]?.title ?: "", style = HallyuType.Caption, maxLines = 1)
            }
        }
    }
}

// ---------- Sub-screens ----------

@Composable
private fun SubHeader(store: HallyuStore, title: String, onBack: () -> Unit, tabs: List<Pair<String, String>> = emptyList(), active: String? = null, nav: (String) -> Unit = {}) {
    TopBar(title = title, onBack = onBack)
    if (tabs.isNotEmpty()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tabs.forEach { (label, route) ->
                Box(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active == label) HallyuColors.Surface2 else HallyuColors.Surface)
                        .border(1.dp, if (active == label) HallyuColors.Coral else HallyuColors.Line)
                        .clickable { nav(route) }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                ) {
                    Text(label, style = HallyuType.Small, color = if (active == label) HallyuColors.Text1 else HallyuColors.Text2)
                }
            }
        }
    }
}

@Composable
fun ProfilePostsScreen(store: HallyuStore, nav: (String) -> Unit, onBack: () -> Unit) {
    val s by store.state.collectAsState()
    val myPosts = s.posts.values.filter { it.authorId == "u_hana" }
    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        SubHeader(store, "Hana Kim", onBack, nav = nav, active = "Posts")
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            PostGrid(myPosts, store, nav)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileSavedScreen(store: HallyuStore, nav: (String) -> Unit, onBack: () -> Unit) {
    val s by store.state.collectAsState()
    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        SubHeader(store, "Saved", onBack, nav = nav, active = "Saved")
        Column(Modifier.padding(horizontal = 20.dp)) {
            SectionHeader("SAVED POSTS")
            if (s.bookmarks.isEmpty()) {
                Text("Bookmark posts and they'll live here.", style = HallyuType.Small)
            }
            s.bookmarks.forEach { id ->
                val p = s.posts[id] ?: return@forEach
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .clickable { nav(Routes.post(id)) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (p.media.isNotEmpty()) {
                        PlaceholderArt(seed = p.mediaSeeds.firstOrNull() ?: 0, emoji = "🖼", modifier = Modifier.width(44.dp).height(56.dp).clip(RoundedCornerShape(8.dp)), emojiSize = 14f)
                        Spacer(Modifier.width(10.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(p.content, style = HallyuType.Body, maxLines = 2)
                        Spacer(Modifier.height(3.dp))
                        Text("@${Seed.profiles[p.authorId]?.username} · ${p.timeLabel}", style = HallyuType.Caption)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(12.dp))
            SectionHeader("SAVED DRAMAS")
            s.watching.values.forEach { e ->
                val d = Seed.dramas[e.dramaId] ?: return@forEach
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .clickable { nav(Routes.drama(d.id)) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PlaceholderArt(seed = d.posterSeed, emoji = d.posterEmoji, modifier = Modifier.width(44.dp).height(66.dp).clip(RoundedCornerShape(8.dp)), emojiSize = 16f)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(d.title, style = HallyuType.BodyStrong, maxLines = 1)
                        Text(
                            if (e.status == "completed") "Completed · ${d.year}" else "Watching · through Ep ${e.watchedThrough}",
                            style = HallyuType.Caption,
                        )
                    }
                    Text("→", style = HallyuType.Body, color = HallyuColors.Text3)
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileFollowersScreen(store: HallyuStore, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        SubHeader(store, "Followers", onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            Seed.followers.forEach { (userId, label) ->
                val p = Seed.profiles[userId] ?: return@forEach
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(seed = p.avatarSeed, emoji = p.avatarEmoji, size = 44.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("@${p.username}", style = HallyuType.BodyStrong, maxLines = 1)
                            if (label == "Mutual") {
                                Spacer(Modifier.width(6.dp))
                                Chip(label = "Mutual")
                            }
                        }
                        Text(p.displayName, style = HallyuType.Caption)
                    }
                    FollowButton(following = userId in store.state.value.followedUsers, onClick = { store.toggleFollowUser(userId) })
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileFollowingScreen(store: HallyuStore, onBack: () -> Unit) {
    val s by store.state.collectAsState()
    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        SubHeader(store, "Following", onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            s.followedUsers.forEach { userId ->
                val p = Seed.profiles[userId] ?: return@forEach
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (p.isOfficial) CommunityAvatar(seed = 7, emoji = "📺", size = 44.dp) else Avatar(seed = p.avatarSeed, emoji = p.avatarEmoji, size = 44.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("@${p.username}", style = HallyuType.BodyStrong, maxLines = 1)
                        Text(p.displayName, style = HallyuType.Caption)
                    }
                    FollowButton(following = true, onClick = { store.toggleFollowUser(userId) })
                }
                Spacer(Modifier.height(8.dp))
            }
            s.followedActors.forEach { actorId ->
                val a = Seed.actors[actorId] ?: return@forEach
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(seed = a.portraitSeed, emoji = a.portraitEmoji, size = 44.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(a.name, style = HallyuType.BodyStrong)
                        Text("Actor", style = HallyuType.Caption)
                    }
                    FollowButton(following = true, onClick = { store.toggleFollowActor(actorId) })
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileWatchingScreen(store: HallyuStore, nav: (String) -> Unit, onBack: () -> Unit) {
    val s by store.state.collectAsState()
    val watching = s.watching.values.filter { it.status == "watching" }
    val completed = s.watching.values.filter { it.status == "completed" }
    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        SubHeader(store, "Watching", onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            SectionHeader("NOW WATCHING")
            if (watching.isEmpty()) {
                EmptyState(
                    title = "You're not watching anything yet.",
                    body = "Pick a drama and we'll keep your spoilers safe.",
                )
            }
            watching.forEach { e ->
                val d = Seed.dramas[e.dramaId] ?: return@forEach
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PlaceholderArt(seed = d.posterSeed, emoji = d.posterEmoji, modifier = Modifier.width(52.dp).height(78.dp).clip(RoundedCornerShape(8.dp)), emojiSize = 18f)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(d.title, style = HallyuType.BodyStrong, maxLines = 1)
                        Text("Ep ${e.watchedThrough} of ${d.totalEpisodes}", style = HallyuType.Caption)
                        Spacer(Modifier.height(8.dp))
                        GradientProgress(fraction = e.watchedThrough.toFloat() / d.totalEpisodes.coerceAtLeast(1), modifier = Modifier.fillMaxWidth().height(4.dp))
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .border(1.dp, HallyuColors.Coral)
                                    .clickable { store.markWatched(d.id, e.watchedThrough + 1) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                            ) {
                                Text("+1 episode", style = HallyuType.Caption, color = HallyuColors.Coral)
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(e.updatedLabel, style = HallyuType.Caption)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            if (completed.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                SectionHeader("COMPLETED")
                completed.forEach { e ->
                    val d = Seed.dramas[e.dramaId] ?: return@forEach
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(HallyuShape.Card))
                            .background(HallyuColors.Surface)
                            .border(1.dp, HallyuColors.Line)
                            .clickable { nav(Routes.drama(d.id)) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PlaceholderArt(seed = d.posterSeed, emoji = d.posterEmoji, modifier = Modifier.width(52.dp).height(78.dp).clip(RoundedCornerShape(8.dp)), emojiSize = 18f)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(d.title, style = HallyuType.BodyStrong)
                            Text("Completed · ${d.year}", style = HallyuType.Caption)
                        }
                        Text("✓", style = HallyuType.BodyStrong, color = HallyuColors.Success)
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Watched +1 logs your progress — spoiler veils unlock as you go. (Preview: stored locally.)",
                style = HallyuType.Caption,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileCommunitiesScreen(store: HallyuStore, nav: (String) -> Unit, onBack: () -> Unit) {
    val s by store.state.collectAsState()
    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        SubHeader(store, "My communities", onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            s.joinedCommunities.forEach { id ->
                val c = s.communities[id] ?: return@forEach
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .clickable { nav(Routes.community(c.slug)) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CommunityAvatar(seed = c.avatarSeed, emoji = c.avatarEmoji, size = 44.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.name, style = HallyuType.BodyStrong, maxLines = 1)
                        Text(c.membersLabel + " members", style = HallyuType.Caption)
                    }
                    Text("✓", style = HallyuType.BodyStrong, color = HallyuColors.Success)
                }
                Spacer(Modifier.height(8.dp))
            }
            s.pendingCommunities.forEach { id ->
                val c = s.communities[id] ?: return@forEach
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CommunityAvatar(seed = c.avatarSeed, emoji = c.avatarEmoji, size = 44.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.name, style = HallyuType.BodyStrong, maxLines = 1)
                        Text(c.membersLabel + " members", style = HallyuType.Caption)
                    }
                    Text("⏳ Pending", style = HallyuType.Caption, color = HallyuColors.Warning)
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HallyuShape.Card))
                    .border(1.dp, HallyuColors.Line)
                    .clickable { nav(Routes.CREATE_COMMUNITY) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("＋", style = HallyuType.H2, color = HallyuColors.Coral)
                Spacer(Modifier.width(10.dp))
                Text("Create a community", style = HallyuType.BodyStrong)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileSettingsScreen(store: HallyuStore, nav: (String) -> Unit, onBack: () -> Unit) {
    val s by store.state.collectAsState()
    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        SubHeader(store, "Settings", onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            SectionHeader("ACCOUNT")
            SettingsToggleRow("Private profile", s.privateProfile) { store.setPrivate(!s.privateProfile) }
            SettingsToggleRow("Push notifications", s.pushEnabled) { store.setPush(!s.pushEnabled) }
            Spacer(Modifier.height(8.dp))
            Text("Spoiler preference", style = HallyuType.Body)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(label = "By my progress", selected = s.spoilerPolicy == "by_progress", onClick = { store.setSpoilerPolicy("by_progress") })
                Chip(label = "Always blur", selected = s.spoilerPolicy == "always_blur", onClick = { store.setSpoilerPolicy("always_blur") })
                Chip(label = "Never blur", selected = s.spoilerPolicy == "never_blur", onClick = { store.setSpoilerPolicy("never_blur") })
            }
            Spacer(Modifier.height(16.dp))
            SectionHeader("MUTED DRAMAS")
            if (s.mutedDramas.isEmpty()) {
                Text("No muted dramas.", style = HallyuType.Small)
            }
            s.mutedDramas.forEach { id ->
                val d = Seed.dramas[id] ?: return@forEach
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(d.title, style = HallyuType.BodyStrong)
                        Text("Muted from your feed", style = HallyuType.Caption)
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .border(1.dp, HallyuColors.Line)
                            .clickable { store.toggleMuteDrama(id) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("Unmute", style = HallyuType.Small)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(8.dp))
            SectionHeader("SAFETY")
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HallyuShape.Card))
                    .background(HallyuColors.Surface)
                    .border(1.dp, HallyuColors.Line)
                    .clickable { nav(Routes.PROFILE_BLOCKED) }
                    .padding(12.dp),
            ) {
                Text("Blocked users", style = HallyuType.BodyStrong, modifier = Modifier.weight(1f))
                Text("→", style = HallyuType.Body, color = HallyuColors.Text3)
            }
            Spacer(Modifier.height(16.dp))
            SectionHeader("ABOUT")
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HallyuShape.Card))
                    .background(HallyuColors.Surface)
                    .border(1.dp, HallyuColors.Line)
                    .clickable { nav(Routes.PROFILE_ABOUT) }
                    .padding(12.dp),
            ) {
                Text("About Hallyu", style = HallyuType.BodyStrong, modifier = Modifier.weight(1f))
                Text("v1.0.0 · preview", style = HallyuType.Caption, modifier = Modifier.weight(1f))
                Text("→", style = HallyuType.Body, color = HallyuColors.Text3)
            }
            Spacer(Modifier.height(24.dp))
            HallyuButton(label = "Log out", onClick = { store.logout() }, variant = ButtonVariant.SECONDARY)
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsToggleRow(label: String, on: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HallyuShape.Card))
            .background(HallyuColors.Surface)
            .border(1.dp, HallyuColors.Line)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = HallyuType.Body, modifier = Modifier.weight(1f))
        Box(
            Modifier
                .width(44.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (on) HallyuColors.Coral else HallyuColors.Surface2)
                .border(1.dp, if (on) HallyuColors.Coral else HallyuColors.Line)
                .clickable(onClick = onToggle),
            contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(Modifier.size(18.dp).padding(horizontal = 3.dp).clip(RoundedCornerShape(999.dp)).background(if (on) androidx.compose.ui.graphics.Color.White else HallyuColors.Text3))
        }
    }
}

@Composable
fun ProfileBlockedScreen(store: HallyuStore, onBack: () -> Unit) {
    val s by store.state.collectAsState()
    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        SubHeader(store, "Blocked users", onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            if (s.blockedUsers.isEmpty()) {
                EmptyState(title = "Nobody blocked", body = "Users you block will appear here.")
            }
            s.blockedUsers.forEach { id ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(seed = (id.length % 6) + 2, emoji = "🚫", size = 44.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("@${id.removePrefix("u_").removeSuffix("1").removeSuffix("2").removeSuffix("3").removeSuffix("4")}", style = HallyuType.BodyStrong, modifier = Modifier.weight(1f))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .border(1.dp, HallyuColors.Line)
                            .clickable { store.unblockUser(id) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("Unblock", style = HallyuType.Small)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileAboutScreen(store: HallyuStore, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        SubHeader(store, "About Hallyu", onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HallyuShape.Card))
                    .background(HallyuColors.Surface)
                    .border(1.dp, HallyuColors.Line)
                    .padding(16.dp),
            ) {
                Column {
                    Text("Hallyu", style = HallyuType.H2)
                    Text("v1.0.0 · Where the Wave Lives", style = HallyuType.Small)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "This is a preview build running on local sample data. The backend (auth, feeds, publishing, notifications) is pending and will swap in behind the same interfaces — no UI changes expected.",
                style = HallyuType.Body,
                color = HallyuColors.Text2,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Drama data in this preview is representative sample content. Production data is served server-side via TMDB; the frontend never calls TMDB directly.",
                style = HallyuType.Body,
                color = HallyuColors.Text2,
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}
