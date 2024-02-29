package com.konyaco.keeptally.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.Typography
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = KeepTallyColors.md_theme_light_primary,
    onPrimary = KeepTallyColors.md_theme_light_onPrimary,
    primaryContainer = KeepTallyColors.md_theme_light_primaryContainer,
    onPrimaryContainer = KeepTallyColors.md_theme_light_onPrimaryContainer,
    secondary = KeepTallyColors.md_theme_light_secondary,
    onSecondary = KeepTallyColors.md_theme_light_onSecondary,
    secondaryContainer = KeepTallyColors.md_theme_light_secondaryContainer,
    onSecondaryContainer = KeepTallyColors.md_theme_light_onSecondaryContainer,
    tertiary = KeepTallyColors.md_theme_light_tertiary,
    onTertiary = KeepTallyColors.md_theme_light_onTertiary,
    tertiaryContainer = KeepTallyColors.md_theme_light_tertiaryContainer,
    onTertiaryContainer = KeepTallyColors.md_theme_light_onTertiaryContainer,
    error = KeepTallyColors.md_theme_light_error,
    errorContainer = KeepTallyColors.md_theme_light_errorContainer,
    onError = KeepTallyColors.md_theme_light_onError,
    onErrorContainer = KeepTallyColors.md_theme_light_onErrorContainer,
    background = KeepTallyColors.md_theme_light_background,
    onBackground = KeepTallyColors.md_theme_light_onBackground,
    surface = KeepTallyColors.md_theme_light_surface,
    onSurface = KeepTallyColors.md_theme_light_onSurface,
    surfaceVariant = KeepTallyColors.md_theme_light_surfaceVariant,
    onSurfaceVariant = KeepTallyColors.md_theme_light_onSurfaceVariant,
    outline = KeepTallyColors.md_theme_light_outline,
    inverseOnSurface = KeepTallyColors.md_theme_light_inverseOnSurface,
    inverseSurface = KeepTallyColors.md_theme_light_inverseSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = KeepTallyColors.md_theme_dark_primary,
    onPrimary = KeepTallyColors.md_theme_dark_onPrimary,
    primaryContainer = KeepTallyColors.md_theme_dark_primaryContainer,
    onPrimaryContainer = KeepTallyColors.md_theme_dark_onPrimaryContainer,
    secondary = KeepTallyColors.md_theme_dark_secondary,
    onSecondary = KeepTallyColors.md_theme_dark_onSecondary,
    secondaryContainer = KeepTallyColors.md_theme_dark_secondaryContainer,
    onSecondaryContainer = KeepTallyColors.md_theme_dark_onSecondaryContainer,
    tertiary = KeepTallyColors.md_theme_dark_tertiary,
    onTertiary = KeepTallyColors.md_theme_dark_onTertiary,
    tertiaryContainer = KeepTallyColors.md_theme_dark_tertiaryContainer,
    onTertiaryContainer = KeepTallyColors.md_theme_dark_onTertiaryContainer,
    error = KeepTallyColors.md_theme_dark_error,
    errorContainer = KeepTallyColors.md_theme_dark_errorContainer,
    onError = KeepTallyColors.md_theme_dark_onError,
    onErrorContainer = KeepTallyColors.md_theme_dark_onErrorContainer,
    background = KeepTallyColors.md_theme_dark_background,
    onBackground = KeepTallyColors.md_theme_dark_onBackground,
    surface = KeepTallyColors.md_theme_dark_surface,
    onSurface = KeepTallyColors.md_theme_dark_onSurface,
    surfaceVariant = KeepTallyColors.md_theme_dark_surfaceVariant,
    onSurfaceVariant = KeepTallyColors.md_theme_dark_onSurfaceVariant,
    outline = KeepTallyColors.md_theme_dark_outline,
    inverseOnSurface = KeepTallyColors.md_theme_dark_inverseOnSurface,
    inverseSurface = KeepTallyColors.md_theme_dark_inverseSurface,
)

@Composable
fun KeepTallyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val md2Color = Colors(
        primary = colorScheme.primary,
        primaryVariant = colorScheme.primary,
        onPrimary = colorScheme.onPrimary,
        secondary = colorScheme.secondary,
        secondaryVariant = colorScheme.secondary,
        onSecondary = colorScheme.onSecondary,
        surface = colorScheme.surface,
        onSurface = colorScheme.onSurface,
        background = colorScheme.background,
        onBackground = colorScheme.onBackground,
        error = colorScheme.error,
        onError = colorScheme.onError,
        isLight = !darkTheme
    )

    val typography = KeepTallyTypography

    val md2Typo = Typography(
        h1 = typography.displayLarge,
        h2 = typography.displayMedium,
        h3 = typography.displaySmall,
        h4 = typography.headlineLarge,
        h5 = typography.headlineMedium,
        h6 = typography.headlineSmall,
        subtitle1 = typography.titleLarge,
        subtitle2 = typography.titleMedium,
        body1 = typography.bodyMedium,
        body2 = typography.bodySmall,
        caption = typography.labelLarge
    )

    androidx.compose.material.MaterialTheme(md2Color) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}

@Composable
fun AndroidKeepTallyTheme(content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    KeepTallyTheme {
        val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
        LaunchedEffect(systemUiController) {
            systemUiController.setSystemBarsColor(
                Color.Transparent,
                surfaceColor.luminance() > 0.5f
            )
        }
        content()
    }
}