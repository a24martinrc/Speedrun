package com.example.speedrun_compose.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define una paleta de colores personalizada (o usa la predeterminada)
private val DarkColorPalette = lightColorScheme(
    primary = Color(0xFF757575),
    secondary = Color(0xFF9B9B9B),
    background = Color(0xFF1D1D1D),
    surface = Color(0xFF1D1D1D),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

// Crea un tema personalizado
@Composable
fun SpeedRunAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorPalette, // Usamos nuestra paleta de colores personalizada
        typography = Typography, // Se puede personalizar si lo deseas
        content = content // El contenido de la aplicación
    )
}
