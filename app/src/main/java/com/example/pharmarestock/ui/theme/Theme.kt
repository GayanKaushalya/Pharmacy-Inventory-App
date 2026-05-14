package com.example.pharmarestock.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTeal,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black, // Text on top of Teal buttons will be black
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun PharmaRestockTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}