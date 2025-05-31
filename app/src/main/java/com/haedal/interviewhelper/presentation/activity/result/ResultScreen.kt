package com.haedal.interviewhelper.presentation.activity.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haedal.interviewhelper.data.remote.response.Emotion
import com.haedal.interviewhelper.data.remote.response.ResultItem
import com.haedal.interviewhelper.presentation.theme.Color02
import com.haedal.interviewhelper.presentation.theme.Color03
import com.haedal.interviewhelper.presentation.theme.Color04
import com.haedal.interviewhelper.presentation.theme.Color05
import com.haedal.interviewhelper.presentation.theme.Typography
import com.haedal.interviewhelper.presentation.theme.White

@Composable
fun ResultScreen(
    question: String,
    resultList: List<ResultItem>,
    serverMessage: String,
    feedback: String
) {
    LazyColumn(
        modifier = Modifier
            .padding(top=(48+8).dp, bottom = 48.dp, start = 24.dp, end = 24.dp)
            .fillMaxSize()
    ) {
        // 1. 서버 메시지
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, Color05),
                colors = CardDefaults.cardColors(containerColor = White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.QuestionAnswer,
                        contentDescription = "면접 질문",
                        tint = Color04
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("$question", style = Typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. 분석 결과 타이틀
        item {
            SectionTitle("분석 결과")
            Spacer(modifier = Modifier.height(2.dp))
        }

        // 3. 감정 분석 결과 카드 목록
        items(resultList) { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Gray),
                colors = CardDefaults.cardColors(containerColor = White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🗣️ \"${item.sentence}\"", style = Typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    item.emotions
                        .sortedByDescending { it.score }
                        .take(3)
                        .forEach {
                            Text("→ ${it.label}: ${"%.2f".format(it.score)}")
                        }
                }
            }
        }

        // 4. ChatGPT 피드백 카드
        item {
            Spacer(modifier = Modifier.height(10.dp)) //위쪽 칼럼 6dp 아래 10dp 총 16dp
            SectionTitle("ChatGPT 피드백")
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, Color02),
                colors = CardDefaults.cardColors(containerColor = Color02.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(feedback, style = Typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }


}
@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
    )
}


@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    val mockResults = listOf(
        ResultItem(
            sentence = "저는 꼼꼼한 성격입니다.",
            emotions = listOf(
                Emotion("신뢰", 0.82f),
                Emotion("기쁨", 0.65f),
                Emotion("놀람", 0.30f)
            )
        ),
        ResultItem(
            sentence = "협업이 중요한 이유는 소통이라고 생각합니다.",
            emotions = listOf(
                Emotion("기쁨", 0.78f),
                Emotion("신뢰", 0.62f),
                Emotion("슬픔", 0.20f)
            )
        )
    )

    ResultScreen(
        question = "협업이 중요한 이유는 무엇인가요?",
        resultList = mockResults,
        serverMessage = "분석이 완료되었습니다!",
        feedback = "전반적으로 자신감 있는 어조였으며, 논리적인 흐름이 좋았습니다."
    )
}