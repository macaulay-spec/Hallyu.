package app.hallyu.ui.people

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import app.hallyu.data.HallyuStore
import app.hallyu.data.Seed
import app.hallyu.design.HallyuBrand
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuType
import app.hallyu.design.Avatar
import app.hallyu.design.components.ButtonVariant
import app.hallyu.design.CommunityAvatar
import app.hallyu.design.components.EmptyState
import app.hallyu.design.components.FollowButton
import app.hallyu.design.components.HallyuButton
import app.hallyu.design.PlaceholderArt
import app.hallyu.design.components.SectionHeader
import app.hallyu.design.components.TopBar
import app.hallyu.domain.Community
import app.hallyu.navigation.Routes
import app.hallyu.ui.PostCardWired

@Composable
fun ActorScreen(store: HallyuStore, actorId: String, onBack: () -> Unit, nav: (String) -> Unit) {
    val a = Seed.actors[actorId] ?: return
    val s by store.state.collectAsState()
    val following = actorId in s.followedActors
    val posts = s.posts.values.filter { it.actorId == actorId }

    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        TopBar(title = "Actor", onBack = onBack)
        Column(Modifier.padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(8.dp))
            Avatar(seed = a.portraitSeed, emoji = a.portraitEmoji, size = 96.dp, ring = following)
            Spacer(Modifier.height(12.dp))
            Text(a.name, style = HallyuType.H2)
            if (a.nameKo.isNotBlank()) {
                Text(a.nameKo, style = HallyuType.Small)
            }
            Spacer(Modifier.height(4.dp))
            Text("${a.followersLabel} followers · ${a.dramas.size} dramas", style = HallyuType.Caption)
            Spacer(Modifier.height(14.dp))
            FollowButton(following = following, onClick = { store.toggleFollowActor(actorId) }, modifier = Modifier.width(180.dp))
        }
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(20.dp))
            SectionHeader("FILMOGRAPHY")
            Spacer(Modifier.height(8.dp))
            if (a.dramas.isEmpty()) {
                Text("Filmography arrives with the backend.", style = HallyuType.Small)
            }
            a.dramas.forEach { entry ->
                val parts = entry.split("|")
                val dId = parts.getOrNull(0)
                val role = parts.getOrNull(1)
                val year = parts.getOrNull(2)
                val d = Seed.dramas[dId]
                if (d == null) return@forEach
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
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(d.title, style = HallyuType.BodyStrong, maxLines = 1)
                        if (!role.isNullOrBlank()) {
                            Text("as $role", style = HallyuType.Caption)
                        }
                    }
                    if (!year.isNullOrBlank()) {
                        Text(year, style = HallyuType.Caption)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            if (posts.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                SectionHeader("POSTS")
                posts.forEach { p ->
                    Spacer(Modifier.height(8.dp))
                    PostCardWired(post = p, store = store, onClick = { nav(Routes.post(p.id)) })
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun CommunityScreen(store: HallyuStore, slug: String, onBack: () -> Unit, nav: (String) -> Unit) {
    val s by store.state.collectAsState()
    val c: Community? = Seed.communities.values.find { it.slug == slug } ?: s.communities.values.find { it.slug == slug }
    if (c == null) {
        Column(Modifier.fillMaxSize().background(HallyuColors.Ink950)) {
            TopBar(title = "Community", onBack = onBack)
            EmptyState(title = "Community not found", body = "It may have been renamed or removed.")
        }
        return
    }
    val joined = c.id in s.joinedCommunities
    val pending = c.id in s.pendingCommunities
    val posts = s.posts.values.filter { it.communityId == c.id }
    val pinned = posts.find { it.isPinned }

    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        TopBar(title = "Community", onBack = onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                CommunityAvatar(seed = c.avatarSeed, emoji = c.avatarEmoji, size = 56.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(c.name, style = HallyuType.H2, maxLines = 2)
                    Text("${c.membersLabel} members · ${c.visibility}", style = HallyuType.Caption)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(c.description, style = HallyuType.Body, color = HallyuColors.Text2)
            Spacer(Modifier.height(12.dp))
            HallyuButton(
                label = when {
                    joined -> "✓ Joined"
                    pending -> "⏳ Request pending"
                    else -> "＋ Join community"
                },
                onClick = { store.toggleJoinCommunity(c.id) },
                variant = if (joined) ButtonVariant.SECONDARY else ButtonVariant.PRIMARY,
            )
            if (c.rules.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                SectionHeader("COMMUNITY RULES")
                c.rules.forEach { rule ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    ) {
                        Text("•", style = HallyuType.Body, color = HallyuColors.Coral)
                        Spacer(Modifier.width(8.dp))
                        Text(rule, style = HallyuType.Body)
                    }
                }
            }
            if (pinned != null) {
                Spacer(Modifier.height(12.dp))
                SectionHeader("PINNED")
                Spacer(Modifier.height(8.dp))
                PostCardWired(post = pinned, store = store, onClick = { nav(Routes.post(pinned.id)) })
            }
            Spacer(Modifier.height(12.dp))
            SectionHeader("POSTS")
            val rest = posts.filter { it.id != pinned?.id }
            if (rest.isEmpty()) {
                Text("No posts yet. Be the first!", style = HallyuType.Small)
            }
            rest.forEach { p ->
                Spacer(Modifier.height(8.dp))
                PostCardWired(post = p, store = store, onClick = { nav(Routes.post(p.id)) })
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun HashtagScreen(store: HallyuStore, slug: String, onBack: () -> Unit, nav: (String) -> Unit) {
    val s by store.state.collectAsState()
    val tag = "#$slug"
    val posts = s.posts.values.filter { it.hashtag == tag }

    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        TopBar(title = tag, onBack = onBack)
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HallyuShape.Card))
                    .background(HallyuBrand.Gradient)
                    .padding(16.dp),
            ) {
                Text(tag, style = HallyuType.H2, color = androidx.compose.ui.graphics.Color.White)
                Spacer(Modifier.height(4.dp))
                Text("${posts.size} posts · live", style = HallyuType.Small, color = androidx.compose.ui.graphics.Color.White)
            }
            Spacer(Modifier.height(16.dp))
            if (posts.isEmpty()) {
                EmptyState(title = "No posts yet", body = "Be the first to post about $tag.")
            }
            posts.forEach { p ->
                PostCardWired(post = p, store = store, onClick = { nav(Routes.post(p.id)) })
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
