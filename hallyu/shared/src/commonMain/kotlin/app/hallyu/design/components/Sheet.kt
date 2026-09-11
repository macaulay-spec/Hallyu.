package app.hallyu.design.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuType

/** One row in a [HallyuSheet]. Destructive actions render in danger color and travel to the bottom. */
data class SheetAction(val label: String, val destructive: Boolean = false, val onClick: () -> Unit)

/**
 * docs/03 §3.6 sheet system: 40% scrim, spring(0.72 / 200) entry, 24dp top radius,
 * 40×4 drag handle, 15pt rows, destructive last.
 */
@Composable
fun HallyuSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    actions: List<SheetAction>,
) {
    val spec = spring(dampingRatio = 0.72f, stiffness = 200f)
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(tween(120)) + slideInVertically(animationSpec = spec, initialOffsetY = { it }),
        exit = fadeOut(tween(120)) + slideOutVertically(animationSpec = spec, targetOffsetY = { it }),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(HallyuColors.Ink950.copy(alpha = 0.4f))
                .clickable(onClick = onDismiss),
        ) {
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(HallyuColors.Ink900)
                    .navigationBarsPadding(),
            ) {
                // 40×4 drag handle (also dismisses)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 6.dp)
                        .clickable(onClick = onDismiss),
                ) {
                    Box(Modifier.weight(1f).padding(start = 8.dp))
                    Box(Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(HallyuColors.Surface2))
                    Box(Modifier.weight(1f).padding(end = 8.dp))
                }
                actions.forEachIndexed { index, action ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                action.onClick()
                                if (!action.destructive) onDismiss()
                            }
                            .padding(horizontal = HallyuSpacing.L, vertical = 15.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            action.label,
                            style = HallyuType.Body,
                            color = if (action.destructive) HallyuColors.Danger else HallyuColors.Text1,
                        )
                    }
                    if (index < actions.lastIndex) {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(HallyuColors.Line))
                    }
                }
            }
        }
    }
}
