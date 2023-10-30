package net.araymond.application.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import net.araymond.application.Utility
import net.araymond.application.Values
import kotlin.math.atan

private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val NebulaColorScheme = darkColorScheme(
    // Main background
    background = grey00,
    surface = grey00,
    // Transaction category
    tertiary = lightBlue,
    primary = lightBlue,
    // Transaction date and time
    surfaceTint = darkTan,
    // Tile background
    surfaceVariant = grey01,
    // Floating button background
    primaryContainer = darkerBlue,
    onPrimaryContainer = Color.White,
    // Account name
    inverseSurface = darkerTan,
    // Text
    onSurface = lightTan,
    onSurfaceVariant = lightTan,
    outline = darkTan
    )

@Composable
fun ApplicationTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), isDynamicColor: Boolean = true, content: @Composable () -> Unit) {
    val themePreference = Values.themes[Utility.getPreference("themePreference")]
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when (themePreference) {
        "System default" ->
            when {
                dynamicColor && isDarkTheme -> {
                    dynamicDarkColorScheme(LocalContext.current)
                }

                dynamicColor && !(isDarkTheme) -> {
                    dynamicLightColorScheme(LocalContext.current)
                }
                isDarkTheme -> DarkColorScheme
                else -> LightColorScheme
            }
        "Material You (Dark)" ->
            dynamicDarkColorScheme(LocalContext.current)
        "Material You (Light)" ->
            dynamicLightColorScheme(LocalContext.current)
        "Nebula" ->
            NebulaColorScheme

        else ->
            NebulaColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}