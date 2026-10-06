package com.aquila.pocxpertalerts.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aquila.pocxpertalerts.ui.theme.AppBackground
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.TextGray
import com.aquila.pocxpertalerts.ui.theme.White
import com.aquila.pocxpertalerts.ui.theme.XpertOrange
import com.aquila.pocxpertalerts.ui.theme.XpertPurple

private val XpertColorScheme = lightColorScheme(
    primary = XpertPurple,
    secondary = XpertOrange,
    background = AppBackground,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextGray
)

@Composable
fun XpertAlertsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = XpertColorScheme,
        content = content
    )
}