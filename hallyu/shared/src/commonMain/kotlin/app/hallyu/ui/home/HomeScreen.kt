package app.hallyu.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import app.hallyu.data.HallyuStore
import app.hallyu.data.Seed
import app.hallyu.design.HallyuBrand
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuType
import app.hallyu.design.WaveDivider
import app.hallyu.design.WaveLogo
import app.hallyu.design.components.EmptyState
import app.hallyu.design.components.ErrorState
import app.hallyu.design.components.IconCircle
import app.hallyu.design.PosterCard
import app.hallyu.design.components.SkeletonPostCard
import app.hallyu.navigation.Routes
import app.hallyu.ui.PostCardWired

@Composable
fun HomeScreen(store: HallyuStore, nav: (String) -> Unit) {
    val s by store.state.collectAsState()
    val ready by store.ready.collectAsState()
    var feed by remember { mutableStateOf("foryou") }
    var error by remember { mutableStateOf(false) }
    var taps by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(HallyuBrand.Gradient),
                contentAlignment = Alignment.Center,
            ) {
                WaveLogo(size = 20.dp, strokeWidth = 2.dp)
            }
            Spacer(Modifier.width(10.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable {
                        // Dev hook: five taps on the brand simulates a network failure
                        // so the error state is reachable in the preview.
                        taps = if (taps >= 4) {
                            error = true
                            0
                        } else taps + 1
                    },
            ) {
                Text("HALLYU", style = HallyuType.Title)
            }
            Spacer(Modifier.weight(1f))
            IconCircle("✦", onClick = { nav(Routes.EXPLORE) }, modifier = Modifier.size(36.dp))
            Spacer(Modifier.width(10.dp))
            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(HallyuColors.Surface2)
                    .combinedClickable(
                        onClick = { nav(Routes.PROFILE_SETTINGS) },
                        onLongClick = {
                            // Dev hook: long-press the gear simulates an offline session.
                            store.setOffline(!s.offline)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text("⚙", style = HallyuType.Small)
            }
        }
        if (s.offline) {
            Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)) {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(HallyuColors.Surface2)
                        .border(1.dp, HallyuColors.Line, RoundedCornerShape(999.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("⚠ ", style = HallyuType.Small, color = HallyuColors.Warning)
                    Spacer(Modifier.width(4.dp))
                    Text("Offline — showing cached content", style = HallyuType.Small)
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SegChip("For You", feed == "foryou", { feed = "foryou" })
            SegChip("Following", feed == "following", { feed = "following" })
        }
        Spacer(Modifier.height(16.dp))

        when {
            !ready -> {
                SkeletonPostCard()
                Spacer(Modifier.height(10.dp))
                SkeletonPostCard()
                Spacer(Modifier.height(10.dp))
                SkeletonPostCard()
            }
            error -> ErrorState(onRetry = { error = false })
            else -> {
                NowAiringRow(nav)
                Spacer(Modifier.height(6.dp))
                val ids = when (feed) {
                    "foryou" -> Seed.feedForYou
                    else -> (s.extraPosts["following"] ?: emptyList()) + Seed.feedFollowing
                }
                if (ids.isEmpty()) {
                    EmptyState(
                        title = "Your fandom is quiet here.",
                        body = "Follow a few dramas or communities to get things moving.",
                        actionLabel = "Explore dramas",
                        onAction = { nav(Routes.EXPLORE) },
                    )
                } else {
                    ids.forEach { id ->
                        val p = s.posts[id] ?: return@forEach
                        PostCardWired(post = p, store = store, onClick = { nav(Routes.post(p.id)) })
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SegChip(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .weight(1f)
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (active) HallyuBrand.Gradient else HallyuColors.Surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = HallyuType.BodyStrong, color = if (active) androidx.compose.ui.graphics.Color.White else HallyuColors.Text2)
    }
}

@Composable
private fun NowAiringRow(nav: (String) -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(6.dp).height(6.dp).clip(RoundedCornerShape(999.dp)).background(HallyuColors.Coral))
            Spacer(Modifier.width(6.dp))
            Text("NOW AIRING", style = HallyuType.Caption)
            Spacer(Modifier.weight(1f))
            WaveDivider(Modifier.width(84.dp).height(14.dp), color = HallyuColors.Line)
        }
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            listOf("d_mbb", "d_bona", "d_scandal").forEach { id ->
                val d = Seed.dramas.getValue(id)
                PosterCard(
                    seed = d.posterSeed,
                    emoji = d.posterEmoji,
                    title = "",
                    width = 132.dp,
                    badge = d.nextEpisodeLabel,
                    badgeLive = d.status.name == "AIRING",
                    onClick = { nav(Routes.drama(id)) },
                )
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}
