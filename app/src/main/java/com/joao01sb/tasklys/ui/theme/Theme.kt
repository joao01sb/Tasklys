package com.joao01sb.tasklys.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun TasklysTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val colorScheme = lightColorScheme(
        primary = com.joao01sb.tasklys.ui.theme.Primary,
        onPrimary = com.joao01sb.tasklys.ui.theme.OnPrimary,
        secondary = com.joao01sb.tasklys.ui.theme.Secondary,
        onSecondary = com.joao01sb.tasklys.ui.theme.OnSecondary,
        background = com.joao01sb.tasklys.ui.theme.Background,
        onBackground = com.joao01sb.tasklys.ui.theme.OnSurface,
        surface = com.joao01sb.tasklys.ui.theme.Surface,
        onSurface = com.joao01sb.tasklys.ui.theme.OnSurface,
        surfaceVariant = com.joao01sb.tasklys.ui.theme.SurfaceVariant,
        onSurfaceVariant = com.joao01sb.tasklys.ui.theme.OnSurfaceVariant,
        outline = com.joao01sb.tasklys.ui.theme.Outline,
        outlineVariant = com.joao01sb.tasklys.ui.theme.OutlineVariant,
        error = com.joao01sb.tasklys.ui.theme.Error
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}