package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Red80,
    onPrimary = Color(0xFF690005),
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = RedGrey80,
    onSecondary = Color(0xFF442927),
    background = DarkSurface,
    surface = DarkSurface,
    surfaceVariant = DarkCardBg
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RedPrimary,
    onPrimary = RedOnPrimary,
    primaryContainer = RedPrimaryContainer,
    onPrimaryContainer = RedOnPrimaryContainer,
    secondary = RedSecondary,
    secondaryContainer = RedSecondaryContainer,
    background = Color(0xFFFDF8F8),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF5EBEA)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep medical red branding by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
