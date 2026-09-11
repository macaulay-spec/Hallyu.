package app.hallyu.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private fun gradientFor(seed: Int): Brush =
    Brush.linearGradient(listOf(paletteFor(seed).first, paletteFor(seed).second))

/** Deterministic placeholder art for the preview dataset. Real poster/avatar URLs flow through
 *  the same slots when the backend (TMDB-sourced) lands. */
@Composable
fun PlaceholderArt(seed: Int, emoji: String, modifier: Modifier = Modifier, emojiSize: Float = 36f) {
    Box(modifier = modifier.background(gradientFor(seed)), contentAlignment = Alignment.Center) {
        WaveDivider(
            Modifier
                .fillMaxWidth()
                .height(22.dp)
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
            color = Color.White.copy(alpha = 0.16f),
        )
        Text(emoji, fontSize = emojiSize.sp, color = Color.White.copy(alpha = 0.92f))
    }
}

/** Vertical 2:3 K-drama poster — the system's signature ratio. */
@Composable
fun PosterCard(
    seed: Int,
    emoji: String,
    title: String,
    subtitle: String = "",
    badge: String? = null,
    badgeLive: Boolean = false,
    width: Dp = 116.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.width(width)) {
        val inner = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        Box(
            modifier = inner
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(HallyuShape.Card))
                .border(1.dp, HallyuColors.Line),
        ) {
            PlaceholderArt(seed = seed, emoji = emoji, modifier = Modifier.fillMaxSize())
            if (badge != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(HallyuColors.Ink950.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (badgeLive) {
                        // §3.7: coral dot pulses once per 4s — calm, not alarming.
                        val transition = rememberInfiniteTransition(label = "airingPulse")
                        val pulse by transition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(tween(1000, delayMillis = 3000), RepeatMode.Restart),
                            label = "airingPulseValue",
                        )
                        Box(
                            Modifier
                                .size(6.dp)
                                .graphicsLayer {
                                    val s = 1f + 0.6f * pulse
                                    scaleX = s
                                    scaleY = s
                                }
                                .clip(CircleShape)
                                .background(HallyuColors.Coral),
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(badge, style = HallyuType.Caption, color = HallyuColors.Text1)
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, HallyuColors.Ink950.copy(alpha = 0.92f)))
                    )
                    .padding(8.dp),
            ) {
                Text(
                    title,
                    style = HallyuType.Small.copy(color = HallyuColors.Text1, fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, style = HallyuType.Caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun Avatar(
    seed: Int,
    emoji: String,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier,
    ring: Boolean = false,
) {
    val base = Modifier.size(size).clip(CircleShape).background(gradientFor(seed))
    val final = if (ring) base.border(2.dp, HallyuColors.BrandBlue, CircleShape) else base
    Box(modifier = modifier.then(final), contentAlignment = Alignment.Center) {
        Text(emoji, fontSize = (size.value * 0.48f).sp)
    }
}

@Composable
fun CommunityAvatar(seed: Int, emoji: String, size: Dp = 48.dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(size).clip(RoundedCornerShape(12.dp)).background(gradientFor(seed)),
        contentAlignment = Alignment.Center,
    ) {
        Text(emoji, fontSize = (size.value * 0.48f).sp)
    }
}
