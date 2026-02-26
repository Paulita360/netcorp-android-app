package net.iesochoa.paulaboixvilella.tfgv1.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,

    secondary = BlueSecondary,
    onSecondary = Color.White,

    tertiary = PinkAccent,
    onTertiary = Color.White,

    background = DarkBackground,
    onBackground = LightText,

    surface = DarkSurface,
    onSurface = LightText,

    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = LightTextSecondary,

    outline = Color(0xFF3D3D3D)
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,

    secondary = BlueSecondary,
    onSecondary = Color.White,

    tertiary = PinkAccent,
    onTertiary = Color.White,

    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1C1C),

    surface = Color.White,
    onSurface = Color(0xFF1C1C1C),

    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF3D3D3D),

    outline = Color(0xFF9E9E9E)
)

@Composable
fun TFGv1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
