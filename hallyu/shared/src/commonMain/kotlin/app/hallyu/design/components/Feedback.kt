package app.hallyu.design.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.hallyu.design.HallyuBrand
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuSpacing
import app.hallyu.design.HallyuType
import app.hallyu.design.WaveDivider

private val SkeletonRadius = RoundedCornerShape(10.dp)

@Composable
fun ShimmerSurface(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "sweep",
    )
    val offset = (sweep - 0.5f) * 1.6f
    Box(
        modifier = modifier
            .clip(SkeletonRadius)
            .background(HallyuColors.Surface2)
            .background(
                Brush.linearGradient(
                    listOf(Color.Transparent, Color.White.copy(alpha = 0.07f), Color.Transparent),
                    startOffset = offset * 400f,
                    endOffset = (offset + 0.4f) * 400f,
                )
            ),
    )
}

@Composable
fun SkeletonPostCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HallyuShape.Card))
            .background(HallyuColors.Surface)
            .border(1.dp, HallyuColors.Line, RoundedCornerShape(HallyuShape.Card))
            .padding(HallyuSpacing.L),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerSurface(Modifier.size(40.dp).clip(CircleShape))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ShimmerSurface(Modifier.fillMaxWidth(0.35f).height(12.dp))
                ShimmerSurface(Modifier.fillMaxWidth(0.7f).height(10.dp))
            }
        }
        ShimmerSurface(Modifier.aspectRatio(4f / 5f))
        Row {
            repeat(4) {
                ShimmerSurface(Modifier.size(26.dp).clip(CircleShape).padding(end = 8.dp))
            }
        }
    }
}

/** §38: wave illustration + product voice + one path forward. */
@Composable
fun EmptyState(
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(HallyuSpacing.XXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(HallyuColors.Surface),
            contentAlignment = Alignment.Center,
        ) {
            WaveDivider(Modifier.width(84.dp).height(28.dp), color = HallyuColors.BrandBlue.copy(alpha = 0.7f))
        }
        Text(title, style = HallyuType.Title, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text(body, style = HallyuType.Small, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(8.dp))
            HallyuButton(actionLabel, onAction)
        }
    }
}

@Composable
fun ErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(HallyuSpacing.XXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(HallyuColors.Surface),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WaveDivider(Modifier.width(40.dp).height(22.dp), color = HallyuColors.Text3)
                Text("×", style = HallyuType.H2, color = HallyuColors.Danger)
                WaveDivider(Modifier.width(40.dp).height(22.dp), color = HallyuColors.Text3)
            }
        }
        Text("Couldn't load this right now.", style = HallyuType.Title, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text("Check your connection and try again.", style = HallyuType.Small, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        HallyuButton("Retry", onRetry)
        HallyuButton("Report a problem", {}, ButtonVariant.GHOST, fillWidth = false)
    }
}
