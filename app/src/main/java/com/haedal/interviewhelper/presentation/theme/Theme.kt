package com.haedal.interviewhelper.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color03,            // 주요 색상 (예: 시작하기 버튼, 주요 강조)
    onPrimary = White,            // primary 위에 얹힐 텍스트 (반전용: 흰색)

    secondary = Color01,          // 보조 색상 (예: 작은 버튼, 강조선 등)
    onSecondary = Black,          // secondary 위 텍스트

    background = White,           // 앱 전체 배경
    onBackground = Color05,       // 일반 텍스트 색상 (진회색 계열 추천)

    surface = White,              // 카드, 다이얼로그 등 기본 배경
    onSurface = Black,            // surface 위에 올라가는 텍스트

    error = Color(0xFFB00020),    // 오류 색상 (기본 Material 색상)
    onError = White,              // 오류 배경 위 텍스트
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
        content = {
            Surface( // 여기로 감싸서 완전한 흰색 배경 강제
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                content()
            }
        }
    )
}