package com.haedal.interviewhelper.presentation.activity.home


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.haedal.interviewhelper.presentation.theme.PrimaryButton
import com.haedal.interviewhelper.presentation.theme.Color03

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.haedal.interviewhelper.presentation.theme.Color04
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.theme.White

@Composable
fun HomeScreen(
    userName: String,
    onStartInterview: () -> Unit,
    onLogout: () -> Unit,
    feedbackList: List<Pair<String, String>>,
    contentList: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        // 상단 인사 + 로그아웃
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$userName 님, 안녕하세요 👋",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "로그아웃", tint = Color.Gray)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SectionTitle("오늘의 추천 질문")
                CardSection(
                    content = "\"협업 중 갈등이 생겼을 때, 어떻게 해결했나요?\"",
                    icon = Icons.Default.QuestionAnswer
                )
            }
            item {
                SectionTitle("최근 피드백받은 질문")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    feedbackList.forEach { (question, feedback) ->
                        FeedbackCard(question = question, feedback = feedback)
                    }
                }
            }
            item {
                SectionTitle("추천 콘텐츠")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    contentList.forEach { (title, description) ->
                        ContentCard(title = title, description = description)
                    }
                }
            }
        }

        PrimaryButton(
            text = "인터뷰 시작하기",
            onClick = onStartInterview,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            containerColor = Color03,
            contentColor= White
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun CardSection(content: String, icon: ImageVector) {
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, Color.Gray),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = Color04)
            Spacer(Modifier.width(12.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FeedbackCard(question: String, feedback: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, Color.Gray),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = question,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = feedback,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
        }
    }
}

@Composable
fun ContentCard(title: String, description: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, Color.Gray),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    InterviewHelperTheme {
        HomeScreen(
            userName = "양승환",
            onStartInterview = {},
            onLogout = {},
            feedbackList = listOf(
                "자신의 단점을 말해보세요." to "말투가 소심하게 들릴 수 있어요. 조금 더 확신 있게 말하면 좋아요.",
                "성공적인 프로젝트 경험을 말해보세요." to "목표 중심으로 정리하면 더 설득력 있어요."
            ),
            contentList = listOf(
                "모범 답변 듣기" to "다른 사람들의 실제 인터뷰 답변을 들어보세요.",
                "AI 피드백 예시 보기" to "AI가 실제로 어떻게 피드백을 주는지 확인해보세요."
            )
        )
    }
}
