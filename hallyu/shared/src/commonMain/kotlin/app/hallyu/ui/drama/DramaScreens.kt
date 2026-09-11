package app.hallyu.ui.drama

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.hallyu.data.HallyuStore
import app.hallyu.data.Seed
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuType
import app.hallyu.design.Avatar
import app.hallyu.design.components.ButtonVariant
import app.hallyu.design.components.Chip
import app.hallyu.design.components.FollowButton
import app.hallyu.design.components.HallyuButton
import app.hallyu.design.components.IconCircle
import app.hallyu.design.PlaceholderArt
import app.hallyu.design.PosterCard
import app.hallyu.design.components.SectionHeader
import app.hallyu.design.components.TopBar
import app.hallyu.domain.Drama
import app.hallyu.domain.DramaStatus
import app.hallyu.domain.PostCategory
import app.hallyu.domain.Reaction
import app.hallyu.navigation.Routes
import app.hallyu.ui.PostCardWired
import app.hallyu.ui.kCount

private fun statusLabel(d: Drama): String = when (d.status) {
    DramaStatus.AIRING -> "Airing · Ep ${d.currentEpisode} of ${d.totalEpisodes}"
    DramaStatus.UPCOMING -> d.nextEpisodeLabel.ifBlank { "Coming soon" }
    DramaStatus.COMPLETED -> "Completed"
}

@Composable
fun DramaHubScreen(store: HallyuStore, dramaId: String, onBack: () -> Unit, nav: (String) -> Unit) {
    val d = Seed.dramas[dramaId] ?: return
    val s by store.state.collectAsState()
    val eps = Seed.episodes[dramaId] ?: emptyList()
    val castList = Seed.cast[dramaId] ?: emptyList()
    val watching = s.watching[dramaId]
    var linkTapped by remember { mutableStateOf(false) }
    var shared by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        TopBar(
            title = "",
            onBack = onBack,
            actions = { IconCircle("⬆", onClick = { shared = true }) },
        )
        Box(Modifier.fillMaxWidth().height(220.dp)) {
            PlaceholderArt(seed = d.posterSeed, emoji = d.posterEmoji, modifier = Modifier.fillMaxSize(), emojiSize = 72f)
        }
        Column(Modifier.padding(horizontal = 20.dp)) {
            if (shared) {
                Text(
                    "✓ Link copied — ${Routes.deepLink(Routes.drama(dramaId))}",
                    style = HallyuType.Caption,
                    color = HallyuColors.Info,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            Row(Modifier.fillMaxWidth().padding(top = (-70).dp)) {
                PosterCard(seed = d.posterSeed, emoji = d.posterEmoji, title = "", width = 110.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(d.title, style = HallyuType.H1, maxLines = 2)
                    if (d.titleKo.isNotBlank()) {
                        Text(d.titleKo, style = HallyuType.Small)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Chip(label = "$d.year")
                        Chip(label = "${d.totalEpisodes} Episodes")
                        d.genres.forEach { g -> Chip(label = g) }
                        Chip(label = if (d.airTimeKst.isNotBlank()) "${d.network} · ${d.airDay} ${d.airTimeKst}" else d.nextEpisodeLabel)
                    }
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(HallyuColors.Surface2)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.width(6.dp).height(6.dp).clip(RoundedCornerShape(999.dp)).background(HallyuColors.Coral))
                            Spacer(Modifier.width(6.dp))
                            Text(statusLabel(d), style = HallyuType.Small, color = if (d.status == DramaStatus.AIRING) HallyuColors.Coral else HallyuColors.Text2)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FollowButton(following = dramaId in s.followedDramas, onClick = { store.toggleFollowDrama(dramaId) }, modifier = Modifier.weight(1f))
                HallyuButton(
                    label = if (watching != null) "▶ Watching · Ep ${watching.watchedThrough} ⌄" else "＋ Start watching",
                    onClick = { nav(Routes.PROFILE_WATCHING) },
                    variant = ButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(20.dp))
            if (d.overview.isNotBlank()) {
                SectionHeader("ABOUT")
                Text(d.overview, style = HallyuType.Body, color = HallyuColors.Text2)
            }
            if (castList.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                SectionHeader("CAST")
                Spacer(Modifier.height(8.dp))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    castList.forEach { cm ->
                        val a = Seed.actors[cm.actorId] ?: return@forEach
                        Column(
                            Modifier
                                .width(78.dp)
                                .clickable { nav(Routes.actor(cm.actorId)) },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Avatar(seed = a.portraitSeed, emoji = a.portraitEmoji, size = 64.dp)
                            Spacer(Modifier.height(6.dp))
                            Text(a.name, style = HallyuType.Small, textAlign = TextAlign.Center, maxLines = 1)
                            Text(cm.character, style = HallyuType.Caption, textAlign = TextAlign.Center, maxLines = 1)
                        }
                    }
                }
            }
            if (eps.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                SectionHeader("EPISODES")
                Spacer(Modifier.height(8.dp))
                eps.forEach { ep ->
                    val active = !ep.isUpcoming && ep.number == d.currentEpisode
                    val reminded = "${dramaId}:${ep.number}" in s.reminders
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(HallyuShape.Card))
                            .background(if (active) HallyuColors.Surface2 else HallyuColors.Surface)
                            .border(1.dp, if (active) HallyuColors.Coral else HallyuColors.Line)
                            .clickable { nav(Routes.episode(dramaId, ep.number)) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (ep.title.isNotBlank()) "Episode ${ep.number} — ${ep.title}" else "Episode ${ep.number}",
                                style = HallyuType.BodyStrong,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                if (ep.isUpcoming) ep.airLabel else "${ep.airLabel}${if (ep.discussionCount.isNotBlank()) " · ${ep.discussionCount}" else ""}",
                                style = HallyuType.Caption,
                            )
                        }
                        if (active) {
                            Text("⌄", style = HallyuType.Body, color = HallyuColors.Text2)
                        }
                        if (ep.isUpcoming) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .border(1.dp, if (reminded) HallyuColors.Success else HallyuColors.Line)
                                    .clickable { store.toggleReminder(dramaId, ep.number) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                            ) {
                                Text(if (reminded) "🔔 Reminded" else "🔔 Remind me", style = HallyuType.Caption, color = if (reminded) HallyuColors.Success else HallyuColors.Text2)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            if (dramaId == "d_mbb") {
                Spacer(Modifier.height(16.dp))
                SectionHeader("COMMUNITY")
                Spacer(Modifier.height(6.dp))
                val teaser = Seed.posts["p_coffee"]
                if (teaser != null) {
                    val teaserAuthor = Seed.profiles[teaser.authorId]
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(HallyuShape.Card))
                            .background(HallyuColors.Surface)
                            .border(1.dp, HallyuColors.Line)
                            .clickable { nav(Routes.post(teaser.id)) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (teaserAuthor != null) {
                            Avatar(seed = teaserAuthor.avatarSeed, emoji = teaserAuthor.avatarEmoji, size = 32.dp)
                            Spacer(Modifier.width(10.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("@${teaserAuthor?.username}", style = HallyuType.BodyStrong, maxLines = 1)
                                if (teaser.episodeNumber != null) {
                                    Spacer(Modifier.width(6.dp))
                                    Text("· Ep ${teaser.episodeNumber}", style = HallyuType.Caption)
                                }
                                Spacer(Modifier.weight(1f))
                                Text("♥ ${kCount(teaser.reactions[Reaction.LIKE] ?: 0)}", style = HallyuType.Small, color = HallyuColors.Coral)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(teaser.content, style = HallyuType.Body, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
                Text(
                    "View all discussions →",
                    style = HallyuType.Small,
                    color = HallyuColors.Info,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(999.dp))
                        .clickable { nav(Routes.episode(dramaId, d.currentEpisode)) }
                        .padding(vertical = 6.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HallyuShape.Card))
                    .border(1.dp, HallyuColors.Line)
                    .background(HallyuColors.Surface)
                    .clickable { linkTapped = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("▶", style = HallyuType.H2, color = HallyuColors.Coral)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Watch on ${d.network}", style = HallyuType.BodyStrong)
                    Text("Official streaming link", style = HallyuType.Caption)
                }
                Text("↗", style = HallyuType.Body, color = HallyuColors.Text3)
            }
            if (linkTapped) {
                Spacer(Modifier.height(6.dp))
                Text("Preview: external links arrive with the backend.", style = HallyuType.Caption, color = HallyuColors.Warning)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun EpisodeScreen(store: HallyuStore, dramaId: String, episode: Int, onBack: () -> Unit, nav: (String) -> Unit) {
    val d = Seed.dramas[dramaId] ?: return
    val epObj = (Seed.episodes[dramaId] ?: emptyList()).find { it.number == episode } ?: return
    val s by store.state.collectAsState()
    val watchedThrough = s.watchedThrough[dramaId] ?: 0
    val caughtUp = watchedThrough >= episode
    val sessionRevealed = s.revealedEpisode?.first == dramaId && (s.revealedEpisode?.second ?: 0) >= episode
    val disclosed = caughtUp || sessionRevealed
    var text by remember { mutableStateOf("") }
    var posted by remember { mutableStateOf(false) }

    val seedIds = Seed.episodeDiscussion["e_${dramaId.removePrefix("d_")}_$episode"] ?: emptyList()
    val userIds = s.posts.values.filter { it.dramaId == dramaId && it.episodeNumber == episode }.map { it.id }
    val ids = seedIds + userIds

    Box(Modifier.fillMaxSize().background(HallyuColors.Ink950)) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            TopBar(
                title = "",
                onBack = onBack,
                actions = { IconCircle("⋯", onClick = { nav(Routes.drama(dramaId)) }) },
            )
            Box(Modifier.fillMaxWidth().height(230.dp)) {
                PlaceholderArt(seed = d.posterSeed + episode, emoji = "🎬", modifier = Modifier.fillMaxSize(), emojiSize = 64f)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .height(110.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(androidx.compose.ui.graphics.Color.Transparent, HallyuColors.Ink950.copy(alpha = 0.92f))
                            )
                        ),
                )
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                ) {
                    Text(d.title.uppercase(), style = HallyuType.Caption, color = HallyuColors.Text2)
                    Text(
                        "Episode $episode${if (epObj.title.isNotBlank()) " — ${epObj.title}" else ""}",
                        style = HallyuType.H1,
                    )
                    Text(
                        buildList {
                            add(epObj.airLabel)
                            if (d.airTimeKst.isNotBlank() && !epObj.isUpcoming) add(d.airTimeKst)
                            if (epObj.discussionCount.isNotBlank()) add("${epObj.discussionCount} in discussion")
                        }.joinToString(" · "),
                        style = HallyuType.Small,
                        color = HallyuColors.Text2,
                    )
                }
            }
            Column(Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(14.dp))
                if (!disclosed) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(HallyuShape.Card))
                            .border(1.5.dp, HallyuBrand.Gradient)
                            .background(HallyuColors.Surface)
                            .padding(14.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚠", style = HallyuType.H2, color = HallyuColors.Warning)
                            Spacer(Modifier.width(8.dp))
                            Text("Spoilers for Episode $episode", style = HallyuType.Title)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("You've watched through Episode $watchedThrough", style = HallyuType.Small)
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            HallyuButton(
                                label = "Reveal for this session",
                                onClick = { store.revealEpisodeForSession(dramaId, episode) },
                                variant = ButtonVariant.SECONDARY,
                                modifier = Modifier.weight(1f),
                                height = 40.dp,
                            )
                            HallyuButton(
                                label = "I watched Ep $episode ✓",
                                onClick = { store.markWatched(dramaId, episode) },
                                variant = ButtonVariant.PRIMARY,
                                modifier = Modifier.weight(1f),
                                height = 40.dp,
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✓", style = HallyuType.BodyStrong, color = HallyuColors.Success)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (caughtUp) "You're caught up — spoilers shown for Ep $episode." else "Revealed for this session — spoilers shown.",
                            style = HallyuType.Small,
                            color = HallyuColors.Success,
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
            SectionHeader("DISCUSSION")
            Spacer(Modifier.height(6.dp))
            if (ids.isEmpty()) {
                Text("Discussion opens at drop time.", style = HallyuType.Small)
                Spacer(Modifier.height(12.dp))
            }
            ids.forEach { id ->
                val p = s.posts[id] ?: return@forEach
                PostCardWired(post = p, store = store, onClick = { nav(Routes.post(id)) })
                Spacer(Modifier.height(10.dp))
            }
            // Room for the fixed composer.
            Spacer(Modifier.height(110.dp))
        }
        }

        // Fixed bottom composer (mockup 20): input · "Spoiler: Ep N" chip · gradient send.
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(HallyuColors.Ink900.copy(alpha = 0.97f))
                .border(1.dp, HallyuColors.Line),
        ) {
            if (posted) {
                Text(
                    "✓ Posted to the episode thread.",
                    style = HallyuType.Caption,
                    color = HallyuColors.Success,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
                        Text("Share your reaction…", style = HallyuType.Body, color = HallyuColors.Text3)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .border(1.dp, HallyuColors.Coral)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text("Spoiler: Ep $episode", style = HallyuType.Caption, color = HallyuColors.Coral)
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(HallyuBrand.Gradient)
                        .clickable {
                            if (text.isNotBlank()) {
                                store.addPost(text, PostCategory.DISCUSSION, dramaId, episode, spoilerLevel = if (caughtUp) null else episode)
                                text = ""
                                posted = true
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("➤", color = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    }
}
