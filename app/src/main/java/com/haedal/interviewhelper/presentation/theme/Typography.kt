package com.haedal.interviewhelper.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = NotoSansKR,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = NotoSansKR,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )
)