package com.flxholle.quicktiles.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.flxholle.quicktiles.R

/**
 * A voice is one deliberate setting of Google Sans Flex axes (wght is taken from each style).
 * Keep to two or three per app and reuse them. Ranges: wdth 25..151, ROND 0..100.
 */
data class Voice(val width: Float = 100f, val roundness: Float = 0f, val weightBoost: Int = 0)

val DisplayVoice = Voice(width = 108f, roundness = 100f, weightBoost = 100)
val TextVoice = Voice()

private fun TextStyle.voiced(voice: Voice): TextStyle {
    val weight = ((fontWeight ?: FontWeight.Normal).weight + voice.weightBoost).coerceIn(1, 1000)
    val font = Font(
        resId = R.font.google_sans_flex,
        weight = FontWeight(weight),
        variationSettings = FontVariation.Settings(
            FontVariation.weight(weight),
            FontVariation.width(voice.width),
            FontVariation.opticalSizing(fontSize),
            FontVariation.Setting("ROND", voice.roundness),
        ),
    )
    return copy(fontFamily = FontFamily(font), fontWeight = FontWeight(weight))
}

private val Base = Typography()

val AppTypography = Typography(
    displayLarge = Base.displayLarge.voiced(DisplayVoice),
    displayMedium = Base.displayMedium.voiced(DisplayVoice),
    displaySmall = Base.displaySmall.voiced(DisplayVoice),
    headlineLarge = Base.headlineLarge.voiced(DisplayVoice),
    headlineMedium = Base.headlineMedium.voiced(DisplayVoice),
    headlineSmall = Base.headlineSmall.voiced(DisplayVoice),
    titleLarge = Base.titleLarge.voiced(TextVoice),
    titleMedium = Base.titleMedium.voiced(TextVoice),
    titleSmall = Base.titleSmall.voiced(TextVoice),
    bodyLarge = Base.bodyLarge.voiced(TextVoice),
    bodyMedium = Base.bodyMedium.voiced(TextVoice),
    bodySmall = Base.bodySmall.voiced(TextVoice),
    labelLarge = Base.labelLarge.voiced(TextVoice),
    labelMedium = Base.labelMedium.voiced(TextVoice),
    labelSmall = Base.labelSmall.voiced(TextVoice),
    displayLargeEmphasized = Base.displayLargeEmphasized.voiced(DisplayVoice),
    displayMediumEmphasized = Base.displayMediumEmphasized.voiced(DisplayVoice),
    displaySmallEmphasized = Base.displaySmallEmphasized.voiced(DisplayVoice),
    headlineLargeEmphasized = Base.headlineLargeEmphasized.voiced(DisplayVoice),
    headlineMediumEmphasized = Base.headlineMediumEmphasized.voiced(DisplayVoice),
    headlineSmallEmphasized = Base.headlineSmallEmphasized.voiced(DisplayVoice),
    titleLargeEmphasized = Base.titleLargeEmphasized.voiced(TextVoice),
    titleMediumEmphasized = Base.titleMediumEmphasized.voiced(TextVoice),
    titleSmallEmphasized = Base.titleSmallEmphasized.voiced(TextVoice),
    bodyLargeEmphasized = Base.bodyLargeEmphasized.voiced(TextVoice),
    bodyMediumEmphasized = Base.bodyMediumEmphasized.voiced(TextVoice),
    bodySmallEmphasized = Base.bodySmallEmphasized.voiced(TextVoice),
    labelLargeEmphasized = Base.labelLargeEmphasized.voiced(TextVoice),
    labelMediumEmphasized = Base.labelMediumEmphasized.voiced(TextVoice),
    labelSmallEmphasized = Base.labelSmallEmphasized.voiced(TextVoice),
)

/** Numbers that change (totals, timers, stats): tabular figures so digits don't jitter. */
val TextStyle.tabular: TextStyle get() = copy(fontFeatureSettings = "tnum")
