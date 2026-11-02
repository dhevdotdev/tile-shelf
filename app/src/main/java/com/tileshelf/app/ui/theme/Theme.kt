package com.tileshelf.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> BrandDark
        else -> BrandLight
    }
    val spacing = Spacing.forWidth(LocalConfiguration.current.screenWidthDp)
    CompositionLocalProvider(LocalSpacing provides spacing) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = rememberAppMotionScheme(),
            shapes = AppShapes,
            typography = AppTypography,
            content = content,
        )
    }
}
