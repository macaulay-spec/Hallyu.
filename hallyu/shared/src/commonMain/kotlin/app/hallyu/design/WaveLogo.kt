package app.hallyu.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/** The brand mark: two overlapping sine strokes, violet→blue. */
@Composable
fun WaveLogo(size: Dp = 28.dp, strokeWidth: Dp = 3.dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val brush = Brush.linearGradient(
            colors = listOf(HallyuColors.BrandViolet, HallyuColors.BrandBlue),
            start = Offset(0f, 0f),
            end = Offset(w, h),
        )
        for (i in 0 until 2) {
            val midY = h / 2f + i * h * 0.16f - h * 0.08f
            val amp = h * 0.15f
            val start = w * (0.06f + i * 0.05f)
            val end = w * (0.94f - i * 0.05f)
            val path = Path().apply {
                moveTo(start, midY)
                val steps = 40
                for (s in 0..steps) {
                    val t = s / steps.toFloat()
                    val x = start + (end - start) * t
                    val y = midY - amp * sin(t * 2f * PI).toFloat()
                    lineTo(x, y)
                }
            }
            drawPath(path, brush, style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round))
        }
    }
}

/** Thin decorative sine divider (the system's only ornament). */
@Composable
fun WaveDivider(modifier: Modifier = Modifier, color: Color = HallyuColors.Line) {
    Canvas(modifier = modifier) {
        val w = this.size.width
        val midY = size.height / 2f
        val amp = size.height * 0.3f
        val path = Path().apply {
            moveTo(0f, midY)
            for (s in 0..60) {
                val t = s / 60f
                lineTo(w * t, midY - amp * sin(t * 2f * PI).toFloat())
            }
        }
        drawPath(path, color, style = Stroke(width = 2f, cap = StrokeCap.Round))
    }
}

/** Six-point wave seal — verification badge (design system §3.5). */
@Composable
fun VerifiedSeal(size: Dp = 14.dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val c = center
        val rOuter = size / 2f
        val rInner = size * 0.36f
        val path = Path().apply {
            val points = 8
            for (i in 0 until points * 2) {
                val r = if (i % 2 == 0) rOuter else rInner
                val angle = (PI / points) * i - PI / 2f
                val x = c.x + r * kotlin.math.cos(angle).toFloat()
                val y = c.y + r * kotlin.math.sin(angle).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(path, brush = Brush.linearGradient(listOf(HallyuColors.BrandViolet, HallyuColors.BrandBlue)))
        drawCircle(color = Color.White, radius = size * 0.22f)
    }
}
