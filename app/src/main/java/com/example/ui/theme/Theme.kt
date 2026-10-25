package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CartCraftDarkPrimary,
    onPrimary = CartCraftDarkOnPrimary,
    primaryContainer = CartCraftDarkPrimaryContainer,
    onPrimaryContainer = CartCraftDarkOnPrimaryContainer,
    secondary = CartCraftDarkSecondary,
    onSecondary = CartCraftDarkOnSecondary,
    secondaryContainer = CartCraftDarkSecondaryContainer,
    onSecondaryContainer = CartCraftDarkOnSecondaryContainer,
    tertiary = CartCraftDarkTertiary,
    onTertiary = CartCraftDarkOnTertiary,
    tertiaryContainer = CartCraftDarkTertiaryContainer,
    onTertiaryContainer = CartCraftDarkOnTertiaryContainer,
    background = CartCraftDarkBackground,
    onBackground = CartCraftDarkOnBackground,
    surface = CartCraftDarkSurface,
    onSurface = CartCraftDarkOnSurface,
    surfaceVariant = CartCraftDarkSurfaceVariant,
    onSurfaceVariant = CartCraftDarkOnSurfaceVariant,
    outline = CartCraftDarkOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CartCraftPrimary,
    onPrimary = CartCraftOnPrimary,
    primaryContainer = CartCraftPrimaryContainer,
    onPrimaryContainer = CartCraftOnPrimaryContainer,
    secondary = CartCraftSecondary,
    onSecondary = CartCraftOnSecondary,
    secondaryContainer = CartCraftSecondaryContainer,
    onSecondaryContainer = CartCraftOnSecondaryContainer,
    tertiary = CartCraftTertiary,
    onTertiary = CartCraftOnTertiary,
    tertiaryContainer = CartCraftTertiaryContainer,
    onTertiaryContainer = CartCraftOnTertiaryContainer,
    background = CartCraftBackground,
    onBackground = CartCraftOnBackground,
    surface = CartCraftSurface,
    onSurface = CartCraftOnSurface,
    surfaceVariant = CartCraftSurfaceVariant,
    onSurfaceVariant = CartCraftOnSurfaceVariant,
    outline = CartCraftOutline
  )

@Composable
fun CartCraftTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep CartCraft signature branding consistent
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
