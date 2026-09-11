package app.hallyu.ui.create

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import app.hallyu.data.HallyuStore
import app.hallyu.data.Seed
import app.hallyu.design.HallyuBrand
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuType
import app.hallyu.design.Avatar
import app.hallyu.design.components.Chip
import app.hallyu.design.components.HallyuButton
import app.hallyu.domain.PostCategory
import app.hallyu.navigation.Routes

@Composable
fun ComposerScreen(store: HallyuStore, nav: (String) -> Unit) {
    val s by store.state.collectAsState()
    var text by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(PostCategory.REACTION) }
    var dramaId by remember { mutableStateOf<String?>(null) }
    var ep by remember { mutableStateOf<Int?>(null) }
    var hashtag by remember { mutableStateOf<String?>(null) }
    var spoilerOn by remember { mutableStateOf(true) }
    var showPicker by remember { mutableStateOf(true) }
    val d = Seed.dramas[dramaId]
    val canPost = text.isNotBlank()
    val tagDramas = (s.followedDramas.toList() + "d_mbb").distinct()

    fun post() {
        if (!canPost) return
        store.addPost(
            content = text,
            category = category,
            dramaId = dramaId,
            episodeNumber = ep,
            spoilerLevel = if (d != null && ep != null && spoilerOn) ep else null,
            hashtag = hashtag ?: if (dramaId == "d_mbb") "#MyBiasMyBoss" else null,
        )
        nav(Routes.HOME)
    }

    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        // Header: Cancel · New post · Post (mockup 14)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable { nav(Routes.HOME) }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("👤", style = HallyuType.Body)
                Spacer(Modifier.width(6.dp))
                Text("Cancel", style = HallyuType.Body, color = HallyuColors.Text3)
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("New post", style = HallyuType.Title)
            }
            HallyuButton(label = "Post", onClick = { post() }, modifier = Modifier.width(92.dp), height = 40.dp, enabled = canPost)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(HallyuShape.Card))
                .background(HallyuColors.Surface)
                .border(1.dp, HallyuColors.Line)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Avatar(seed = 0, emoji = "👩", size = 40.dp)

            Box(Modifier.fillMaxWidth().height(150.dp)) {
                SelectionContainer {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = HallyuType.Body.copy(color = HallyuColors.Text1),
                        cursorBrush = SolidColor(HallyuColors.Coral),
                    )
                }
                if (text.isEmpty()) {
                    Text("What's happening with your bias?", style = HallyuType.Body, color = HallyuColors.Text3)
                }
            }

            // Drama tag row
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (d != null) {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(HallyuShape.Chip))
                            .background(HallyuColors.Surface2)
                            .border(1.dp, HallyuColors.BrandBlue)
                            .padding(start = 8.dp, end = 8.dp, top = 5.dp, bottom = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .width(16.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(HallyuBrand.Gradient),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (ep != null) "${d.title} · Ep $ep" else d.title,
                            style = HallyuType.Small,
                            color = HallyuColors.Text1,
                            maxLines = 1,
                        )
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).clickable { dramaId = null; ep = null }.padding(2.dp)) {
                            Text("×", style = HallyuType.BodyStrong, color = HallyuColors.Text3)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(HallyuShape.Chip))
                        .background(HallyuColors.Surface2)
                        .border(1.dp, HallyuColors.Line)
                        .clickable { showPicker = !showPicker }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                ) {
                    Text("👻 Tag a drama", style = HallyuType.Small, color = HallyuColors.Text3)
                }
            }

            if (showPicker) {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tagDramas.forEach { id ->
                        val drama = Seed.dramas[id] ?: return@forEach
                        Chip(label = drama.title, selected = dramaId == id, onClick = { dramaId = id; ep = null })
                    }
                }
            }

            if (d != null && showPicker) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Episode", style = HallyuType.Small)
                    Spacer(Modifier.width(10.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..d.totalEpisodes).forEach { n ->
                            Chip(label = "Ep $n", selected = ep == n, onClick = { ep = if (ep == n) null else n })
                        }
                    }
                }
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(HallyuColors.Line))

            Text("SUGGESTED TAGS", style = HallyuType.Caption)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("#MyBiasMyBoss", "#Ep12", "#Dnx").forEach { tag ->
                    val selected = hashtag == tag
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(HallyuColors.Surface2)
                            .border(1.dp, if (selected) HallyuColors.BrandBlue else HallyuColors.Line)
                            .clickable { hashtag = if (selected) null else tag }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                    ) {
                        Text(tag, style = HallyuType.Small, color = if (selected) HallyuColors.Text1 else HallyuColors.Text2)
                    }
                }
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(HallyuColors.Line))

            Row(verticalAlignment = Alignment.Center) {
                Column(Modifier.weight(1f)) {
                    Text("Spoiler warning", style = HallyuType.BodyStrong)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (d != null && ep != null) {
                            "Blurs for fans who watched through Episode ${s.watchedThrough[d.id] ?: 0}"
                        } else {
                            "Tag an episode to enable blur."
                        },
                        style = HallyuType.Small,
                        color = HallyuColors.Text3,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    Modifier
                        .width(46.dp)
                        .height(26.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (spoilerOn && d != null && ep != null) HallyuBrand.Gradient else HallyuColors.Surface2)
                        .border(1.dp, if (spoilerOn && d != null && ep != null) HallyuColors.BrandBlue else HallyuColors.Line)
                        .clickable(enabled = d != null && ep != null) { spoilerOn = !spoilerOn },
                    contentAlignment = if (spoilerOn && d != null && ep != null) Alignment.CenterEnd else Alignment.CenterStart,
                ) {
                    Box(Modifier.size(20.dp).padding(horizontal = 3.dp).clip(RoundedCornerShape(999.dp)).background(Color.White))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Category", style = HallyuType.BodyStrong)
                    Spacer(Modifier.height(6.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(HallyuColors.Surface2)
                            .border(1.dp, HallyuColors.Line)
                            .clickable {
                                val all = PostCategory.entries.toList()
                                category = all[(all.indexOf(category) + 1) % all.size]
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text("${category.emoji} ${category.label} ⌄", style = HallyuType.Small)
                    }
                }
            }
        }

        // Bottom meta: counter + spoiler note
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("${text.length} / 5,000", style = HallyuType.Caption, color = HallyuColors.Text3)
            Spacer(Modifier.weight(1f))
            if (d != null && ep != null && spoilerOn) {
                Text("≈ may spoil Ep $ep — marked ✓", style = HallyuType.Caption, color = HallyuColors.Success)
            } else {
                Text("≈ spoiler check applies when you tag an episode", style = HallyuType.Caption, color = HallyuColors.Text3)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun CreateCommunityScreen(store: HallyuStore, onBack: () -> Unit, nav: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    fun textInput(value: String, placeholder: String, onValueChange: (String) -> Unit, height: Int = 64) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(height.dp)
                .clip(RoundedCornerShape(HallyuShape.Card))
                .background(HallyuColors.Surface2)
                .border(1.dp, HallyuColors.Line)
                .padding(14.dp),
        ) {
            SelectionContainer {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = HallyuType.Body.copy(color = HallyuColors.Text1),
                    cursorBrush = SolidColor(HallyuColors.Coral),
                )
            }
            if (value.isEmpty()) {
                Text(placeholder, style = HallyuType.Body, color = HallyuColors.Text3)
            }
        }
    }

    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(HallyuColors.Surface2)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text("‹", style = HallyuType.BodyStrong)
            }
            Spacer(Modifier.width(8.dp))
            Text("Create community", style = HallyuType.Title)
        }
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(8.dp))
            Text("Name", style = HallyuType.Body, color = HallyuColors.Text2)
            Spacer(Modifier.height(8.dp))
            textInput(name, "e.g. MBB Spoiler-Safe Lounge", { name = it }, 56)
            Spacer(Modifier.height(14.dp))
            Text("Description", style = HallyuType.Body, color = HallyuColors.Text2)
            Spacer(Modifier.height(8.dp))
            textInput(desc, "What's this community for?", { desc = it }, 110)
            Spacer(Modifier.height(18.dp))
            HallyuButton(
                label = "Create community",
                enabled = name.isNotBlank(),
                onClick = {
                    val id = store.createCommunity(name.trim(), desc.trim())
                    val slug = store.state.value.communities[id]?.slug ?: "new"
                    nav(Routes.community(slug))
                },
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Public communities open to everyone; moderation tools arrive with the backend phase.",
                style = HallyuType.Caption,
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}
