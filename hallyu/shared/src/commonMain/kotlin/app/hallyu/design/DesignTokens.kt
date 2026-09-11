package app.hallyu.design

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Design system tokens — docs/03-design-system.md §3.2. Single source of truth. */
object HallyuColors {
    val Ink950 = Color(0xFF0C0B10)   // app background
    val Ink900 = Color(0xFF121116)   // alt background (tab bar base)
    val Surface = Color(0xFF15141A)  // cards
    val Surface2 = Color(0xFF1D1B24) // raised: inputs, skeletons
    val Line = Color(0xFF2A2833)     // 1px borders
    val Text1 = Color(0xFFF4F2F7)
    val Text2 = Color(0xFFA8A5B3)
    val Text3 = Color(0xFF6F6C7C)
    val BrandViolet = Color(0xFF4A1C6E)
    val BrandBlue = Color(0xFF2D6CDF)
    val Coral = Color(0xFFFF6B6B)    // likes, now-airing, unread
    val Success = Color(0xFF3DD68C)
    val Warning = Color(0xFFFFB454)
    val Danger = Color(0xFFFF5470)
    val Info = Color(0xFF4DA3FF)

    // Reaction semantics (A-4)
    val ReactLike = Color(0xFFFF6B6B)
    val ReactCrush = Color(0xFFFF8FB1)
    val ReactCrying = Color(0xFF6FA8FF)
    val ReactFire = Color(0xFFFF9F45)
    val ReactClap = Color(0xFF9D7BFF)
}

object HallyuBrand {
    val Gradient = Brush.linearGradient(listOf(HallyuColors.BrandViolet, HallyuColors.BrandBlue))
    val VeilGradient = Brush.verticalGradient(
        listOf(HallyuColors.BrandViolet.copy(alpha = 0.35f), HallyuColors.BrandBlue.copy(alpha = 0.35f))
    )
}

object HallyuType {
    val Caption = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = HallyuColors.Text3, letterSpacing = 0.2.sp)
    val Small = TextStyle(fontSize = 13.sp, color = HallyuColors.Text2)
    val Body = TextStyle(fontSize = 15.sp, lineHeight = 22.sp, color = HallyuColors.Text1)
    val BodyStrong = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = HallyuColors.Text1)
    val Title = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = HallyuColors.Text1, letterSpacing = -0.2.sp)
    val H2 = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = HallyuColors.Text1, letterSpacing = -0.4.sp)
    val H1 = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold, color = HallyuColors.Text1, letterSpacing = -0.6.sp)
    val Hero = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, color = HallyuColors.Text1, letterSpacing = -1.sp)
}

object HallyuSpacing {
    val Xs = 4.dp
    val S = 8.dp
    val M = 12.dp
    val L = 16.dp
    val XL = 24.dp
    val XXL = 32.dp
    val Gutter = 16.dp
    val CardGap = 8.dp
}

object HallyuShape {
    val Card = 12.dp
    val Chip = 8.dp
    val Input = 12.dp
    val Sheet = 24.dp
}

/** Deterministic gradient pairs for placeholder art (preview dataset). */
val ArtPalettes: List<Pair<Color, Color>> = listOf(
    Color(0xFF4A1C6E) to Color(0xFF2D6CDF),
    Color(0xFF6E1C3E) to Color(0xFFB83280),
    Color(0xFF10316E) to Color(0xFF2D6CDF),
    Color(0xFF3E1C6E) to Color(0xFF7B4DFF),
    Color(0xFF1C4E5E) to Color(0xFF2DA8DF),
    Color(0xFF5E1C6E) to Color(0xFF2D6CDF),
)

fun paletteFor(seed: Int): Pair<Color, Color> = ArtPalettes[(seed % ArtPalettes.size + ArtPalettes.size) % ArtPalettes.size]
