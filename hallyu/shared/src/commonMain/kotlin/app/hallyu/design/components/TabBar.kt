package app.hallyu.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.hallyu.design.HallyuBrand
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuType
import app.hallyu.design.WaveLogo

/** Five destinations (§4) with the raised gradient Create button (the app's only shadow). */
@Composable
fun BottomTabBar(
    current: String,
    onNavigate: (String) -> Unit,
    unreadNotifications: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(HallyuColors.Ink900.copy(alpha = 0.97f))
            .border(1.dp, HallyuColors.Line)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TabItem(label = "Home", route = "home", current = current, onNavigate = onNavigate, isWave = true)
        TabItem(label = "Explore", route = "explore", current = current, onNavigate = onNavigate, icon = "✦")
        // Raised Create
        Box(
            modifier = Modifier
                .weight(1f)
                .height(64.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .offset(y = (-10).dp)
                    .clip(CircleShape)
                    .background(HallyuBrand.Gradient)
                    .border(4.dp, HallyuColors.Ink900, CircleShape)
                    .clickable(onClick = { onNavigate("create") }),
                contentAlignment = Alignment.Center,
            ) {
                Text("+", style = HallyuType.H1, color = Color.White)
            }
        }
        TabItem(
            label = "Notifications",
            route = "notifications",
            current = current,
            onNavigate = onNavigate,
            icon = "🔔",
            badge = unreadNotifications,
        )
        TabItem(label = "Profile", route = "profile", current = current, onNavigate = onNavigate, icon = "👤")
    }
}

@Composable
private fun TabItem(
    label: String,
    route: String,
    current: String,
    onNavigate: (String) -> Unit,
    icon: String? = null,
    isWave: Boolean = false,
    badge: Boolean = false,
) {
    val active = current == route
    val labelColor = if (active) HallyuColors.BrandBlue else HallyuColors.Text3
    Column(
        modifier = Modifier
            .weight(1f)
            .height(64.dp)
            .clickable(onClick = { onNavigate(route) }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.Center) {
            when {
                isWave -> WaveLogo(size = 22.dp, strokeWidth = (if (active) 2.5f else 2f).dp)
                else -> Text(icon ?: "•", fontSize = 18.sp)
            }
            if (badge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-4).dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HallyuColors.Coral),
                )
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 10.sp, color = labelColor, maxLines = 1)
    }
}
