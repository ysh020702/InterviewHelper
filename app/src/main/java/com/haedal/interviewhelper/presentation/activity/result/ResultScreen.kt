package com.haedal.interviewhelper.presentation.activity.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haedal.interviewhelper.data.remote.response.Emotion
import com.haedal.interviewhelper.data.remote.response.ResultItem
import com.haedal.interviewhelper.domain.helpfunction.moveActivity
import com.haedal.interviewhelper.presentation.activity.auth.AuthActivity
import com.haedal.interviewhelper.presentation.activity.home.HomeActivity
import com.haedal.interviewhelper.presentation.theme.Color02
import com.haedal.interviewhelper.presentation.theme.Color03
import com.haedal.interviewhelper.presentation.theme.Color04
import com.haedal.interviewhelper.presentation.theme.Color05
import com.haedal.interviewhelper.presentation.theme.PrimaryButton
import com.haedal.interviewhelper.presentation.theme.Typography
import com.haedal.interviewhelper.presentation.theme.White

@Composable
fun ResultScreen(
    question: String,
    resultList: List<ResultItem>,
    serverMessage: String,
    feedback: String
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp, bottom = 48.dp, start=24.dp, end=24.dp) // 상단 여백 (예: 앱바 아래), 하단은 버튼과 겹치지 않도록
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            // 1. 면접 질문 카드
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Color05),
                    colors = CardDefaults.cardColors(containerColor = Color05.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QuestionAnswer,
                                contentDescription = "면접 질문",
                                tint = Color04
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "면접 질문",
                                style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color04
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = question,
                            style = Typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.Black
                        )
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
                Spacer(modifier = Modifier.height(10.dp))
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

        // 5. 하단 버튼
        PrimaryButton(
            text = "홈 화면으로 돌아가기",
            onClick = { moveActivity<HomeActivity>(context = context, finishFlag = true) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            containerColor = Color03,
            contentColor = White
        )
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