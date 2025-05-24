package com.haedal.interviewhelper.presentation.activity.home


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.haedal.interviewhelper.presentation.theme.PrimaryButton
import com.haedal.interviewhelper.presentation.theme.Color03

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.ui.tooling.preview.Preview
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme

@Composable
fun HomeScreen(
    userName: String,
    onStartInterview: () -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 상단 바 (안내 + 로그아웃)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$userName 님, 안녕하세요 👋",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            // ✅ 로그아웃 버튼 제안 ① : 텍스트 버튼
            TextButton(onClick = onLogout) {
                Text("로그아웃", color = Color.Gray)
            }

            // ✅ 로그아웃 버튼 제안 ② : 아이콘 버튼 (더 미니멀하게)
            IconButton(onClick = onLogout) {
                 Icon(Icons.Default.ExitToApp, contentDescription = "로그아웃", tint = Color.Gray)
             }
        }

        // 하단 인터뷰 시작 버튼
        PrimaryButton(
            text = "인터뷰 시작하기",
            onClick = onStartInterview,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 48.dp)
                .align(Alignment.BottomCenter),
            containerColor = Color03
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview(){
    InterviewHelperTheme {
        HomeScreen(
            userName = "양승환",
            onStartInterview = {},
            onLogout = {}
        )
    }
}
