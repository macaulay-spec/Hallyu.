package app.hallyu.ui.explore

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
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuType
import app.hallyu.design.Avatar
import app.hallyu.design.components.Chip
import app.hallyu.design.CommunityAvatar
import app.hallyu.design.components.EmptyState
import app.hallyu.design.components.FollowButton
import app.hallyu.design.PlaceholderArt
import app.hallyu.design.PosterCard
import app.hallyu.design.components.SectionHeader
import app.hallyu.navigation.Routes
import app.hallyu.ui.kCount

@Composable
fun ExploreScreen(store: HallyuStore, nav: (String) -> Unit) {
    val s by store.state.collectAsState()
    var query by remember { mutableStateOf("") }
    var section by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Explore", style = HallyuType.H1, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(999.dp))
                    .background(HallyuColors.Surface2)
                    .border(1.dp, HallyuColors.Line)
                        .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔎", style = HallyuType.Body)
                    Spacer(Modifier.width(8.dp))
                    SelectionContainer {
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            textStyle = HallyuType.Body.copy(color = HallyuColors.Text1),
                            cursorBrush = SolidColor(HallyuColors.Coral),
                        )
                    }
                    if (query.isEmpty()) {
                        Text("Search dramas, actors, fans…", style = HallyuType.Body, color = HallyuColors.Text3, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            if (query.isBlank()) MainExplore(store, s.followedUsers, section) { section = if (section == it) null else it }, nav
            else SearchResults(store, s.followedUsers, query, nav) { query = "" }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun visibleSection(section: String?, id: String): Boolean = section == null || section == id

@Composable
private fun MainExplore(store: HallyuStore, followedUsers: Set<String>, section: String?, onSection: (String) -> Unit, nav: (String) -> Unit) {
    val chips = listOf("Trending", "Airing", "Upcoming", "Popular", "Actors", "Communities")
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { label ->
            Chip(
                label = label,
                selected = section == label,
                onClick = { onSection(label) },
            )
        }
    }
    Spacer(Modifier.height(6.dp))

    if (visibleSection(section, "Trending")) {
        SectionHeader("TRENDING NOW")
        Seed.trends.forEach { t ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HallyuShape.Card))
                    .background(HallyuColors.Surface)
                    .border(1.dp, HallyuColors.Line)
                    .clickable { nav(Routes.hashtag(t.hashtag.removePrefix("#"))) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("${t.rank}", style = HallyuType.H2, color = HallyuColors.Text3, modifier = Modifier.width(28.dp))
                Column(Modifier.weight(1f)) {
                    Text(t.hashtag, style = HallyuType.BodyStrong)
                    Text("${t.velocity} · ${t.postsLabel}", style = HallyuType.Caption)
                }
                Text("↑", style = HallyuType.BodyStrong, color = HallyuColors.Success)
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(10.dp))
    }

    if (visibleSection(section, "Airing")) {
        SectionHeader("CURRENTLY AIRING")
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            listOf("d_mbb", "d_bona", "d_para").forEach { id ->
                val d = Seed.dramas.getValue(id)
                PosterCard(seed = d.posterSeed, emoji = d.posterEmoji, title = d.title, width = 128.dp, badge = d.nextEpisodeLabel, badgeLive = true, onClick = { nav(Routes.drama(id)) })
            }
        }
        Spacer(Modifier.height(14.dp))
    }

    if (visibleSection(section, "Upcoming")) {
        SectionHeader("COMING UP")
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            listOf("d_scandal", "d_love").forEach { id ->
                val d = Seed.dramas.getValue(id)
                PosterCard(seed = d.posterSeed, emoji = d.posterEmoji, title = d.title, width = 128.dp, badge = d.nextEpisodeLabel, onClick = { nav(Routes.drama(id)) })
            }
        }
        Spacer(Modifier.height(14.dp))
    }

    if (visibleSection(section, "Popular")) {
        SectionHeader("POPULAR THIS WEEK")
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            listOf("d_qot", "d_para", "d_mmh", "d_lr").forEach { id ->
                val d = Seed.dramas.getValue(id)
                PosterCard(seed = d.posterSeed, emoji = d.posterEmoji, title = d.title, width = 128.dp, onClick = { nav(Routes.drama(id)) })
            }
        }
        Spacer(Modifier.height(14.dp))
    }

    if (visibleSection(section, "Actors")) {
        SectionHeader("ACTORS TO FOLLOW")
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Seed.actors.values.forEach { a ->
                Column(Modifier.clickable { nav(Routes.actor(a.id)) }, horizontalAlignment = Alignment.CenterHorizontally) {
                    Avatar(seed = a.portraitSeed, emoji = a.portraitEmoji, size = 64.dp)
                    Spacer(Modifier.height(6.dp))
                    Text(a.name, style = HallyuType.Caption, maxLines = 1)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
    }

    if (visibleSection(section, "Communities")) {
        SectionHeader("COMMUNITIES TO JOIN")
        listOf("c_dnx", "c_sageuk", "c_meme").forEach { id ->
            val c = Seed.communities.getValue(id)
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HallyuShape.Card))
                    .background(HallyuColors.Surface)
                    .border(1.dp, HallyuColors.Line)
                    .clickable { nav(Routes.community(c.slug)) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CommunityAvatar(seed = c.avatarSeed, emoji = c.avatarEmoji, size = 44.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(c.name, style = HallyuType.BodyStrong, maxLines = 1)
                    Text(c.description, style = HallyuType.Caption, maxLines = 1)
                }
                Text(c.membersLabel, style = HallyuType.Caption)
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(10.dp))
    }

    SectionHeader("OFFICIAL ACCOUNTS")
    val tvn = Seed.profiles.getValue("u_tvn")
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HallyuShape.Card))
            .background(HallyuColors.Surface)
            .border(1.dp, HallyuColors.Line)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CommunityAvatar(seed = 7, emoji = "📺", size = 44.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("tvN", style = HallyuType.BodyStrong)
                Spacer(Modifier.width(6.dp))
                Text("OFFICIAL", style = HallyuType.Caption)
            }
            Text("Premieres, previews, and official drops", style = HallyuType.Caption)
        }
        FollowButton(following = "u_tvn" in followedUsers, onClick = { store.toggleFollowUser("u_tvn") })
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun SearchResults(store: HallyuStore, followedUsers: Set<String>, query: String, nav: (String) -> Unit, onClear: () -> Unit) {
    val q = query.trim().lowercase()
    val dramas = Seed.dramas.values.filter { it.title.lowercase().contains(q) || it.network.lowercase().contains(q) }
    val actors = Seed.actors.values.filter { it.name.lowercase().contains(q) }
    val communities = Seed.communities.values.filter { it.name.lowercase().contains(q) || it.description.lowercase().contains(q) }
    val posts = Seed.posts.values.filter { it.content.lowercase().contains(q) }.take(5)

    if (dramas.isEmpty() && actors.isEmpty() && communities.isEmpty() && posts.isEmpty()) {
        Spacer(Modifier.height(16.dp))
        EmptyState(
            title = "No matching dramas, actors, users, or communities found.",
            body = "“$query” didn't surface anything — the wave is quiet here.",
            actionLabel = "Try another search",
            onAction = onClear,
        )
        return
    }

    if (dramas.isNotEmpty()) {
        SectionHeader("DRAMAS")
        dramas.forEach { d ->
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
                PlaceholderArt(seed = d.posterSeed, emoji = d.posterEmoji, modifier = Modifier.width(40.dp).height(60.dp).clip(RoundedCornerShape(8.dp)), emojiSize = 14f)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(d.title, style = HallyuType.BodyStrong, maxLines = 1)
                    Text("${d.network} · ${d.year}", style = HallyuType.Caption)
                }
                Chip(label = if (d.id in store.state.value.followedDramas) "Following" else "Follow", selected = d.id in store.state.value.followedDramas, onClick = { store.toggleFollowDrama(d.id) })
            }
            Spacer(Modifier.height(8.dp))
        }
    }
    if (actors.isNotEmpty()) {
        SectionHeader("ACTORS")
        actors.forEach { a ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HallyuShape.Card))
                    .background(HallyuColors.Surface)
                    .border(1.dp, HallyuColors.Line)
                    .clickable { nav(Routes.actor(a.id)) }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Avatar(seed = a.portraitSeed, emoji = a.portraitEmoji, size = 44.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(a.name, style = HallyuType.BodyStrong)
                    Text(a.followersLabel + " followers", style = HallyuType.Caption)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
    if (communities.isNotEmpty()) {
        SectionHeader("COMMUNITIES")
        communities.forEach { c ->
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
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(c.name, style = HallyuType.BodyStrong, maxLines = 1)
                    Text(c.membersLabel + " members", style = HallyuType.Caption)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
    if (posts.isNotEmpty()) {
        SectionHeader("POSTS")
        posts.forEach { p ->
            val author = Seed.profiles[p.authorId]
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HallyuShape.Card))
                    .background(HallyuColors.Surface)
                    .border(1.dp, HallyuColors.Line)
                    .clickable { nav(Routes.post(p.id)) }
                    .padding(10.dp),
            ) {
                Column {
                    Text(p.content, style = HallyuType.Body, maxLines = 2)
                    Spacer(Modifier.height(4.dp))
                    Text("@${author?.username} · ${p.timeLabel}", style = HallyuType.Caption)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
