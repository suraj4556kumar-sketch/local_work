package com.example.localconnect.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary          = OrangePrimary,
    onPrimary        = Color.White,
    primaryContainer = OrangeContainer,
    onPrimaryContainer = OrangeDark,
    secondary        = BlueAccent,
    onSecondary      = Color.White,
    secondaryContainer = BlueSurface,
    onSecondaryContainer = BlueAccent,
    background       = BackgroundCream,
    onBackground     = TextPrimary,
    surface          = SurfaceWhite,
    onSurface        = TextPrimary,
    surfaceVariant   = CardSelectedBg,
    onSurfaceVariant = TextSecondary,
    outline          = CardBorderUnselected,
    outlineVariant   = Divider,
    error            = Color(0xFFB00020),
    onError          = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary          = OrangePrimaryDark,
    onPrimary        = Color.Black,
    primaryContainer = OrangeDarkContainer,
    onPrimaryContainer = OrangeLight,
    secondary        = OrangeLight,
    onSecondary      = Color.Black,
    background       = BackgroundDark,
    onBackground     = Color(0xFFE8E8E8),
    surface          = SurfaceDark,
    onSurface        = Color(0xFFE8E8E8),
    surfaceVariant   = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFF9E9E9E),
    outline          = Color(0xFF3A3A3A),
)

@Composable
fun LocalConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}