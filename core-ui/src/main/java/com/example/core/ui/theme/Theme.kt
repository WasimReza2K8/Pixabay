package com.example.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White

private val color = Color()

private val DarkColorPalette = darkColors(
    primary = color.grey500,
    primaryVariant = color.blue700,
    secondary = color.teal200
)

private val LightColorPalette = lightColors(
    primary = color.blue500,
    primaryVariant = color.blue700,
    secondary = color.teal200,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = Black,
    onBackground = Black,
    onSurface = Black,
)

private val LocalSpacings = compositionLocalOf<Spacing> {
    error("No spacings provided! Make sure to wrap all components in an JetTheme.")
}
private val LocalColors = compositionLocalOf<Color> {
    error("No colors provided! Make sure to wrap all components in an JetTheme.")
}

private val LocalTypography = compositionLocalOf<Typography> {
    error("No typography provided! Make sure to wrap all components in an JetTheme.")
}

private val LocalElevations = compositionLocalOf<Elevation> {
    error("No elevation provided! Make sure to wrap all components in an JetTheme.")
}

private val LocalShapes = compositionLocalOf<Shape> {
    error("No shape provided! Make sure to wrap all components in an JetTheme.")
}

@Composable
fun WasimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    CompositionLocalProvider(
        LocalElevations provides Elevation(),
        LocalSpacings provides Spacing(),
        LocalColors provides Color(),
        LocalTypography provides Typography(),
        LocalShapes provides Shape(),
    ) {
        MaterialTheme(
            colors = colors,
            content = content
        )
    }
}

object WasimTheme {
    val color: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val elevation: Elevation
        @Composable
        @ReadOnlyComposable
        get() = LocalElevations.current

    val shape: Shape
        @Composable
        @ReadOnlyComposable
        get() = LocalShapes.current

    val spacing: Spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacings.current
}
