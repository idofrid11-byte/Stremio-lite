package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StremioColorScheme = darkColorScheme(
    primary = CinemaPrimary,
    onPrimary = Color.White,
    primaryContainer = CinemaPrimaryContainer,
    onPrimaryContainer = CinemaOnPrimaryContainer,
    secondary = CinemaSecondary,
    onSecondary = Color.Black,
    secondaryContainer = CinemaSecondaryContainer,
    onSecondaryContainer = Color(0xFFA5F3FC),
    tertiary = CinemaAccent,
    onTertiary = Color.White,
    background = CinemaBlack,
    onBackground = TextPrimary,
    surface = CinemaSurface,
    onSurface = TextPrimary,
    surfaceVariant = CinemaSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CinemaCardBorder,
    error = Color(0xFFEF4444)
)

@Composable
fun StremioTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StremioColorScheme,
        typography = Typography,
        content = content
    )
}
