package com.flxholle.quicktiles.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material spacing system: space100 = 8dp, names are multiples of the base unit.
 * space150 and space250 extend the official set using the same multiplier rule.
 */
@Immutable
data class Spacing(
    val space025: Dp = 2.dp,
    val space050: Dp = 4.dp,
    val space075: Dp = 6.dp,
    val space100: Dp = 8.dp,
    val space125: Dp = 10.dp,
    val space150: Dp = 12.dp,
    val space200: Dp = 16.dp,
    val space250: Dp = 20.dp,
    val space300: Dp = 24.dp,
    val space400: Dp = 32.dp,
    val space500: Dp = 40.dp,
    val space600: Dp = 48.dp,
    val space700: Dp = 56.dp,
    val space800: Dp = 64.dp,
    val space900: Dp = 72.dp,
    val margin: Dp = 16.dp,
) {
    companion object {
        /** Window margins: 16dp below 600dp width, 24dp from medium up. */
        fun forWidth(widthDp: Int): Spacing = if (widthDp >= 600) Spacing(margin = 24.dp) else Spacing()
    }
}

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable @ReadOnlyComposable get() = LocalSpacing.current

/** The M3 Expressive corner scale is the default; change a step here, never per screen. */
val AppShapes = Shapes()

/** Inner radius for content nested in a rounded container: outer minus padding, floor 4dp. */
fun nestedCorner(outer: Dp, padding: Dp): RoundedCornerShape =
    RoundedCornerShape((outer - padding).coerceAtLeast(4.dp))
