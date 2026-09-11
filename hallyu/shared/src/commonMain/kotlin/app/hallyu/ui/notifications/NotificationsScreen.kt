package app.hallyu.ui.notifications

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.hallyu.data.HallyuStore
import app.hallyu.data.Seed
import app.hallyu.design.HallyuBrand
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuType
import app.hallyu.design.components.Chip
import app.hallyu.design.components.EmptyState
import app.hallyu.design.PlaceholderArt
import app.hallyu.design.components.TopBar
import app.hallyu.domain.NotificationCategory

@Composable
fun NotificationsScreen(store: HallyuStore) {
    val s by store.state.collectAsState()
    var filter by remember { mutableStateOf("all") }

    val list = Seed.notifications.filter { n ->
        when (filter) {
            "mentions" -> n.actorId != null && n.category == NotificationCategory.CRITICAL
            "episodes" -> n.title.contains("Episode") || n.body.contains("aired", ignoreCase = true)
            "communities" -> n.icon == "🎬"
            else -> true
        }
    }

    Column(Modifier.fillMaxSize().background(HallyuColors.Ink950).verticalScroll(rememberScrollState())) {
        TopBar(
            title = "Notifications",
            actions = {
                Text(
                    "Mark all read",
                    style = HallyuType.Small,
                    color = HallyuColors.Info,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable { store.markAllNotificationsRead() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            },
        )
        Column(Modifier.padding(horizontal = 20.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(label = "All", selected = filter == "all", onClick = { filter = "all" })
                Chip(label = "Mentions", selected = filter == "mentions", onClick = { filter = "mentions" })
                Chip(label = "Episodes", selected = filter == "episodes", onClick = { filter = "episodes" })
                Chip(label = "Communities", selected = filter == "communities", onClick = { filter = "communities" })
            }
            Spacer(Modifier.height(12.dp))
            if (list.isEmpty()) {
                EmptyState(title = "You're caught up", body = "No notifications in this category yet.")
            }
            list.forEach { n ->
                val read = n.id in s.notificationsRead
                Row(
                    Modifier
                        .fillMaxWidth()
                        .alpha(if (read) 0.5f else 1f)
                        .clip(RoundedCornerShape(HallyuShape.Card))
                        .background(HallyuColors.Surface)
                        .border(1.dp, HallyuColors.Line)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .width(44.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (n.category == NotificationCategory.CRITICAL) HallyuBrand.Gradient else HallyuColors.Surface2),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(n.icon, style = HallyuType.BodyStrong)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(n.title, style = HallyuType.BodyStrong, maxLines = 2)
                        Spacer(Modifier.height(2.dp))
                        Text(n.body, style = HallyuType.Small, maxLines = 2)
                        Spacer(Modifier.height(4.dp))
                        Text(n.timeLabel, style = HallyuType.Caption)
                    }
                    if (n.hasThumbnail) {
                        Spacer(Modifier.width(10.dp))
                        PlaceholderArt(seed = n.thumbnailSeed, emoji = "🎬", modifier = Modifier.width(40.dp).height(60.dp).clip(RoundedCornerShape(8.dp)), emojiSize = 14f)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
