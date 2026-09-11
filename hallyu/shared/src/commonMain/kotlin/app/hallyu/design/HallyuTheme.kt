package app.hallyu.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/** Maps design tokens onto Material3 (dark, cinema). */
@Composable
fun HallyuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = HallyuColors.BrandBlue,
            onPrimary = HallyuColors.Ink950,
            secondary = HallyuColors.BrandViolet,
            tertiary = HallyuColors.Coral,
            background = HallyuColors.Ink950,
            onBackground = HallyuColors.Text1,
            surface = HallyuColors.Surface,
            onSurface = HallyuColors.Text1,
            surfaceVariant = HallyuColors.Surface2,
            onSurfaceVariant = HallyuColors.Text2,
            outline = HallyuColors.Line,
        ),
        content = content,
    )
}
