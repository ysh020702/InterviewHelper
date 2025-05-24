package com.haedal.interviewhelper.presentation.activity.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haedal.interviewhelper.presentation.theme.Black
import com.haedal.interviewhelper.presentation.theme.Color02
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.theme.PrimaryButton
import com.haedal.interviewhelper.presentation.theme.White

@Composable
fun AuthScreen(
    onSignUp: () -> Unit,
    onLogin: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    val offsetY = with(density) {
        (windowInfo.containerSize.height * 0.3f).toDp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // 텍스트 위치: 30% 아래
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = offsetY),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AI Interview\nSelf-Coaching",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "AI 피드백을 통한\n효과적인 면접 준비 방법!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            )
        }

        // 버튼은 그대로 BottomCenter
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PrimaryButton(
                text = "시작하기",
                onClick = onSignUp,
                modifier = Modifier.fillMaxWidth()
            )
            PrimaryButton(
                text = "계정이 이미 있습니다",
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color02,
                contentColor = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    InterviewHelperTheme {
        AuthScreen(onSignUp = {}, onLogin = {})
    }
}