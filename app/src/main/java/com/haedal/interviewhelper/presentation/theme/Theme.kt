package com.haedal.interviewhelper.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.haedal.interviewhelper.InterviewHelperApp

private val LightColors = lightColorScheme(
    primary = Color03,      // 주 색상
    onPrimary = White,
    secondary = Color01,
    background = White,
    surface = Color02,
    onBackground = Color05,
    onSurface = Black
)

@Composable
fun InterviewHelperTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}