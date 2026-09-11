package app.hallyu.design.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.hallyu.design.HallyuBrand
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuSpacing
import app.hallyu.design.HallyuType

enum class ButtonVariant { PRIMARY, SECONDARY, GHOST, DANGER_TEXT }

@Composable
fun HallyuButton(
    label: String,
    onClick: () -> Unit,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    enabled: Boolean = true,
    fillWidth: Boolean = true,
) {
    val shape = if (variant == ButtonVariant.DANGER_TEXT) RoundedCornerShape(0.dp) else RoundedCornerShape(999.dp)
    val bg: Modifier = when (variant) {
        ButtonVariant.PRIMARY -> Modifier.background(HallyuBrand.Gradient, shape)
        ButtonVariant.SECONDARY -> Modifier.background(HallyuColors.Surface2, shape).border(1.dp, HallyuColors.Line, shape)
        ButtonVariant.GHOST, ButtonVariant.DANGER_TEXT -> Modifier
    }
    val fg: Color = when (variant) {
        ButtonVariant.PRIMARY -> Color.White
        ButtonVariant.SECONDARY -> HallyuColors.Text1
        ButtonVariant.GHOST -> HallyuColors.Text2
        ButtonVariant.DANGER_TEXT -> HallyuColors.Danger
    }
    Row(
        modifier = modifier
            .let { if (fillWidth) it.fillMaxWidth() else it }
            .height(height)
            .clip(shape)
            .then(bg)
            .clickable(enabled = enabled, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            label,
            style = HallyuType.BodyStrong.copy(color = fg),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun FollowButton(following: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // Mockup 19: both states are gradient pills — the checkmark is the state reward.
    // §3.7: following triggers one wave ripple (400ms, brand-grad at 30%).
    val ripple = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    Box(modifier) {
        if (ripple.value > 0.01f) {
            Box(
                Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        val s = 1f + ripple.value * 0.5f
                        scaleX = s
                        scaleY = s
                        alpha = 1f - ripple.value
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .border(2.dp, HallyuColors.BrandBlue.copy(alpha = 0.3f * (1f - ripple.value)), RoundedCornerShape(999.dp)),
            )
        }
        HallyuButton(
            if (following) "✓ Following" else "＋ Follow",
            onClick = {
                if (!following) {
                    scope.launch {
                        ripple.snapTo(0f)
                        ripple.animateTo(1f, tween(400)) {
                            if (isFinished) ripple.snapTo(0f)
                        }
                    }
                }
                onClick()
            },
            ButtonVariant.PRIMARY,
            Modifier.matchParentSize(),
            height = 40.dp,
        )
    }
}

@Composable
fun Chip(
    label: String,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(HallyuShape.Chip)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (selected) HallyuColors.Surface2 else HallyuColors.Surface)
            .border(1.dp, if (selected) HallyuColors.BrandBlue else HallyuColors.Line, shape)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = HallyuType.Small.copy(color = if (selected) HallyuColors.Text1 else HallyuColors.Text2))
    }
}

@Composable
fun SectionHeader(label: String, modifier: Modifier = Modifier, trailing: String? = null, onTrailing: (() -> Unit)? = null) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HallyuSpacing.Gutter, vertical = HallyuSpacing.M),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label.uppercase(), style = HallyuType.Caption)
        if (trailing != null) {
            Spacer(Modifier.weight(1f))
            Text(
                trailing,
                style = HallyuType.Small.copy(color = HallyuColors.Info),
                modifier = if (onTrailing != null) Modifier.clickable(onClick = onTrailing) else Modifier,
            )
        }
    }
}

@Composable
fun TopBar(title: String, onBack: (() -> Unit)? = null, modifier: Modifier = Modifier, actions: @Composable () -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HallyuSpacing.S, vertical = HallyuSpacing.S),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            BackCircle(onBack)
            Spacer(Modifier.width(HallyuSpacing.S))
        }
        Text(
            title,
            style = HallyuType.Title,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            // Detail screens (mockups 17/20) center the title; tab roots keep it left.
            textAlign = if (onBack != null) TextAlign.Center else TextAlign.Start,
        )
        actions()
    }
}

@Composable
fun BackCircle(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(HallyuColors.Surface2)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text("‹", style = HallyuType.H1, color = HallyuColors.Text1)
    }
}

@Composable
fun IconCircle(icon: String, onClick: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(HallyuColors.Surface2)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        contentAlignment = Alignment.Center,
    ) {
        Text(icon, fontSize = 16.sp)
    }
}

@Composable
fun GradientProgress(fraction: Float, modifier: Modifier = Modifier, height: Dp = 3.dp) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(999.dp))
            .background(HallyuColors.Surface2),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(999.dp))
                .background(HallyuBrand.Gradient),
        )
    }
}
