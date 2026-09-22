package pt.tiagoduarte.challenge.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE8450A),
    onPrimary = Color.White,
    background = Color(0xFFF2F2F2),
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFF4A5565),
    outline = Color(0xFF9E9E9E),
    outlineVariant = Color(0xFFE0E0E0),
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE8450A),
    onPrimary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFF2F2F2),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF2F2F2),
    onSurfaceVariant = Color(0xFFA9B1BC),
    outline = Color(0xFF8D8D8D),
    outlineVariant = Color(0xFF3A3A3A),
)
