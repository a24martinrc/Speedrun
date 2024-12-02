package com.example.speedrun_compose.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define una paleta de colores personalizada (o usa la predeterminada)
private val LightColorPalette = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFFBB86FC),
    surface = Color(0xFF121212),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.White
)

// Crea un tema personalizado
@Composable
fun SpeedRunAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorPalette, // Usamos nuestra paleta de colores personalizada
        typography = Typography, // Se puede personalizar si lo deseas
        content = content // El contenido de la aplicación
    )
}
