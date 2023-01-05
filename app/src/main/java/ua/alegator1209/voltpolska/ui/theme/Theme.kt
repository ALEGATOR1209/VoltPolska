package ua.alegator1209.voltpolska.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = White,
    secondary = ElectricViolet,
    secondaryVariant = Yellow,
    background = CherryPie,
    surface = CherryPie,
    onBackground = White,
    onSurface = White,
)

@Composable
fun VoltPolskaTheme(content: @Composable () -> Unit) {
    val colors = DarkColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}