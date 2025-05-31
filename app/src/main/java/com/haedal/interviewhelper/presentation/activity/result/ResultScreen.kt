package com.haedal.interviewhelper.presentation.activity.result

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haedal.interviewhelper.data.remote.response.Emotion
import com.haedal.interviewhelper.data.remote.response.ResultItem
import com.haedal.interviewhelper.presentation.theme.Color03
import com.haedal.interviewhelper.presentation.theme.Color04
import com.haedal.interviewhelper.presentation.theme.Color05
import com.haedal.interviewhelper.presentation.theme.Typography
import com.haedal.interviewhelper.presentation.theme.White

@Composable
fun ResultScreen(
    resultList: List<ResultItem>,
    serverMessage: String,
    feedback: String
) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        item {
            // 서버 메시지 카드
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, Color05),
                colors = CardDefaults.cardColors(containerColor = White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = null,
                        tint = Color04
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("📩 $serverMessage", style = Typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // 총평 카드
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, Color03),
                colors = CardDefaults.cardColors(containerColor = Color03.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("총평", style = Typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(feedback, style = Typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 각 문장에 대한 감정 결과
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
    }
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
        resultList = mockResults,
        serverMessage = "분석이 완료되었습니다!",
        feedback = "전반적으로 자신감 있는 어조였으며, 논리적인 흐름이 좋았습니다."
    )
}