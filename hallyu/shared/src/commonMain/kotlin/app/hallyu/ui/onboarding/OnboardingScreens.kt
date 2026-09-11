package app.hallyu.ui.onboarding

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
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuType
import app.hallyu.design.WaveLogo
import app.hallyu.design.WaveDivider
import app.hallyu.design.Avatar
import app.hallyu.design.components.ButtonVariant
import app.hallyu.design.CommunityAvatar
import app.hallyu.design.components.HallyuButton
import app.hallyu.design.PosterCard
import app.hallyu.ui.PostCardWired

private val GENRES = listOf("K-Drama", "Romance", "Action", "Mystery", "Sageuk", "Variety", "Idol News", "OST")

@Composable
fun OnboardingFlow(store: HallyuStore) {
    var step by remember { mutableStateOf(0) }
    Box(Modifier.fillMaxSize().background(HallyuColors.Ink950)) {
        Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(24.dp))
            StepDots(step)
            Spacer(Modifier.height(24.dp))
            when (step) {
                0 -> InterestsStep(onNext = { step = 1 })
                1 -> DramasStep(onNext = { step = 2 })
                2 -> ActorsStep(onNext = { step = 3 })
                3 -> CommunitiesStep(onNext = { step = 4 })
                else -> DoneStep(store)
            }
        }
    }
}

@Composable
private fun StepDots(step: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(4) { i ->
            Box(
                Modifier
                    .width(if (i == step) 24.dp else 8.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (i <= step) HallyuColors.Coral else HallyuColors.Surface2),
            )
        }
    }
}

@Composable
private fun BottomNav(title: String, onClick: () -> Unit) {
    Spacer(Modifier.height(24.dp))
    HallyuButton(label = title, onClick = onClick)
    Spacer(Modifier.height(32.dp))
}

@Composable
private fun toggle(set: Set<String>, id: String): Set<String> = if (id in set) set - id else set + id

@Composable
private fun InterestsStep(onNext: () -> Unit) {
    var selected by remember { mutableStateOf(setOf("K-Drama", "Romance")) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("What do you watch?", style = HallyuType.H2)
        Spacer(Modifier.height(8.dp))
        Text("Pick everything that applies. Your feed is built from these.", style = HallyuType.Body, color = HallyuColors.Text2)
        Spacer(Modifier.height(20.dp))
        for (i in GENRES.indices step 2) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InterestChip(GENRES[i], i in selected) { selected = toggle(selected, GENRES[i]) }
                if (i + 1 < GENRES.size) {
                    InterestChip(GENRES[i + 1], (i + 1) in selected) { selected = toggle(selected, GENRES[i + 1]) }
                } else {
                    Box(Modifier.weight(1f))
                }
            }
        }
        BottomNav("Next", onNext)
    }
}

@Composable
private fun InterestChip(text: String, isOn: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .weight(1f)
            .height(52.dp)
            .clip(RoundedCornerShape(HallyuShape.Card))
            .background(if (isOn) HallyuColors.Surface2 else HallyuColors.Surface)
            .border(1.dp, if (isOn) HallyuColors.Coral else HallyuColors.Line)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (isOn) {
                Text("✓ ", style = HallyuType.BodyStrong, color = HallyuColors.Coral)
            }
            Text(text, style = HallyuType.Body, color = if (isOn) HallyuColors.Text1 else HallyuColors.Text2)
        }
    }
}

@Composable
private fun DramasStep(onNext: () -> Unit) {
    val ids = listOf("d_mbb", "d_bona", "d_para", "d_qot", "d_scandal", "d_itok")
    var selected by remember { mutableStateOf(setOf("d_mbb")) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Which dramas are you into?", style = HallyuType.H2)
        Spacer(Modifier.height(8.dp))
        Text("We'll follow these for you — you can change it anytime.", style = HallyuType.Body, color = HallyuColors.Text2)
        Spacer(Modifier.height(20.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ids.forEach { id ->
                val d = Seed.dramas.getValue(id)
                PosterCard(
                    seed = d.posterSeed,
                    emoji = d.posterEmoji,
                    title = d.title,
                    width = 128.dp,
                    onClick = { selected = toggle(selected, id) },
                    modifier = if (id in selected) {
                        Modifier.border(2.dp, HallyuColors.Coral, RoundedCornerShape(HallyuShape.Card))
                    } else {
                        Modifier
                    },
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("${selected.size}/5 selected", style = HallyuType.Small)
        BottomNav("Next", onNext)
    }
}

@Composable
private fun ActorsStep(onNext: () -> Unit) {
    val ids = listOf("a_kimsoohyun", "a_kimhyejun", "a_kanghoon", "a_chawoomin", "a_baesuzy")
    var selected by remember { mutableStateOf(setOf("a_kimsoohyun")) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Who's your bias?", style = HallyuType.H2)
        Spacer(Modifier.height(8.dp))
        Text("Follow actors to get their drama drops and mentions.", style = HallyuType.Body, color = HallyuColors.Text2)
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ids.forEach { id ->
                val a = Seed.actors.getValue(id)
                Column(
                    Modifier
                        .weight(1f)
                        .clickable(onClick = { selected = toggle(selected, id) }),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Avatar(seed = a.portraitSeed, emoji = a.portraitEmoji, size = 56.dp, ring = id in selected)
                    Spacer(Modifier.height(6.dp))
                    Text(a.name, style = HallyuType.Caption, color = if (id in selected) HallyuColors.Text1 else HallyuColors.Text2, maxLines = 1)
                }
            }
        }
        BottomNav("Next", onNext)
    }
}

@Composable
private fun CommunitiesStep(onNext: () -> Unit) {
    var joined by remember { mutableStateOf(setOf("c_watch", "c_romance", "c_theory")) }
    var tvnFollowed by remember { mutableStateOf(true) }
    val ids = listOf("c_watch", "c_romance", "c_theory")
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Find your people", style = HallyuType.H2)
        Spacer(Modifier.height(8.dp))
        Text("Communities are where watch parties and deep-dive threads live.", style = HallyuType.Body, color = HallyuColors.Text2)
        Spacer(Modifier.height(20.dp))
        ids.forEach { id ->
            val c = Seed.communities.getValue(id)
            CommunityRow(
                name = c.name,
                membersLabel = c.membersLabel,
                seed = c.avatarSeed,
                emoji = c.avatarEmoji,
                joined = id in joined,
                onToggle = { joined = toggle(joined, id) },
            )
            Spacer(Modifier.height(10.dp))
        }
        WaveDivider(Modifier.padding(vertical = 12.dp), color = HallyuColors.Line)
        Row(verticalAlignment = Alignment.CenterVertically) {
            CommunityAvatar(seed = 7, emoji = "📺", size = 44.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("tvN", style = HallyuType.BodyStrong)
                Text("Official · Premieres & previews", style = HallyuType.Caption)
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .border(1.dp, if (tvnFollowed) HallyuColors.Coral else HallyuColors.Line)
                    .background(if (tvnFollowed) HallyuColors.Surface2 else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable(onClick = { tvnFollowed = !tvnFollowed })
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(if (tvnFollowed) "Following" else "Follow", style = HallyuType.BodyStrong, color = if (tvnFollowed) HallyuColors.Coral else HallyuColors.Text1)
            }
        }
        BottomNav("Enter my feed", onNext)
    }
}

@Composable
private fun CommunityRow(name: String, membersLabel: String, seed: Int, emoji: String, joined: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HallyuShape.Card))
            .background(HallyuColors.Surface)
            .border(1.dp, HallyuColors.Line)
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CommunityAvatar(seed = seed, emoji = emoji, size = 44.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(name, style = HallyuType.BodyStrong, maxLines = 1)
            Text("$membersLabel members", style = HallyuType.Caption)
        }
        Text(if (joined) "✓ Joined" else "Join", style = HallyuType.BodyStrong, color = if (joined) HallyuColors.Success else HallyuColors.Coral)
    }
}

@Composable
private fun DoneStep(store: HallyuStore) {
    Column(
        Modifier.fillMaxSize().padding(vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WaveLogo(size = 64.dp)
        Spacer(Modifier.height(16.dp))
        Text("You're in, @hallyu_hana", style = HallyuType.H2)
        Spacer(Modifier.height(8.dp))
        Text("Here's a taste of your feed — Ep 12 is out now.", style = HallyuType.Body, color = HallyuColors.Text2)
        Spacer(Modifier.height(20.dp))
        val post = Seed.posts.getValue("p_coffee")
        PostCardWired(post = post, store = store, onClick = {})
        Spacer(Modifier.height(24.dp))
        HallyuButton(label = "Enter my feed", onClick = { store.completeOnboarding() })
    }
}
