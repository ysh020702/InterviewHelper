@file:Suppress("UNUSED_EXPRESSION")

package com.haedal.interviewhelper.presentation.activity.home


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.haedal.interviewhelper.domain.helpfunction.vibrate
import com.haedal.interviewhelper.presentation.theme.Color04
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.theme.White
import com.haedal.interviewhelper.presentation.viewmodel.HomeViewModel
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    userName: String,
    onStartInterview: (String) -> Unit,
    onLogout: () -> Unit,
    dailyQuestion: String = "",
    feedbackList: List<Pair<String, String>>,
    contentList: List<Pair<String, String>>,
    recentQuestions: List<String>,
    onDeleteQuestion: (String) -> Unit,
    onShowQuestion: (String) -> Unit,
    homeViewModel: HomeViewModel
) {
    val selected = homeViewModel.selectedQuestion
    val context = LocalContext.current


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
                .padding(top = 8.dp, bottom = 16.dp),
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
                SelectableCardSection(
                    content = dailyQuestion,
                    isSelected = selected == dailyQuestion,
                    onSelect = {
                        vibrate(context)
                        homeViewModel.toggleSelection(dailyQuestion)
                    }
                )
            }
            item {
                SectionTitle("인기가 많은 질문")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    feedbackList.forEach { (question, _) ->
                        SelectableCardSection(
                            content = question,
                            isSelected = selected == question,
                            onSelect = {
                                vibrate(context)
                                homeViewModel.toggleSelection(question)
                            }
                        )
                    }
                }
            }

            item {
                SectionTitle("최근에 답변한 질문")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    recentQuestions.forEach { question ->
                        DeleteableCardSection(
                            content = question,
                            isSelected = selected == question,
                            onSelect = {
                                //TODO: resultActivity 띄우기
                                vibrate(context)
                                onShowQuestion(question)
                            },
                            onDelete = { onDeleteQuestion(question) }
                        )
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
            onClick = {
                selected?.let { onStartInterview(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            containerColor = Color03,
            contentColor= White,
            enabled = selected != null
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SelectableCardSection(
    content: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, if (isSelected) Color03 else Color.Gray),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color03.copy(alpha = 0.2f) else Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {}, // 일반 클릭 무시
                onLongClick = onSelect // 길게 눌러서만 선택
            )
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = Color04)
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
fun DeleteableCardSection(
    content: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("삭제 확인") },
            text = { Text("이 질문을 삭제하시겠습니까?\n\"$content\"") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDelete?.invoke()
                }) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, if (isSelected) Color03 else Color.Gray),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color03.copy(alpha = 0.2f) else Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {onSelect}, // 일반 클릭 무시
                onLongClick = {
                    showDialog = true // 길게 눌렀을 때 다이얼로그 출력
                }
            )
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = Color04)
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
    // 더미 뷰모델 객체 (기능 없음)
    val dummyViewModel = object : HomeViewModel() {}

    InterviewHelperTheme {
        HomeScreen(
            userName = "양승환",
            onStartInterview = {},
            onLogout = {},
            dailyQuestion = "자신의 강점을 말해보세요.",
            feedbackList = listOf(
                "자신의 단점을 말해보세요." to "말투가 소심하게 들릴 수 있어요. 조금 더 확신 있게 말하면 좋아요.",
                "성공적인 프로젝트 경험을 말해보세요." to "목표 중심으로 정리하면 더 설득력 있어요."
            ),
            contentList = listOf(
                "모범 답변 듣기" to "다른 사람들의 실제 인터뷰 답변을 들어보세요.",
                "AI 피드백 예시 보기" to "AI가 실제로 어떻게 피드백을 주는지 확인해보세요."
            ),
            recentQuestions = listOf(
                "최근에 도전한 경험은?",
                "리더십 경험이 있나요?",
                "협업 중 갈등을 어떻게 해결했나요?"
            ),
            homeViewModel = dummyViewModel,
            onDeleteQuestion = {}, // Preview에서는 빈 람다
            onShowQuestion = {}  // Preview에서는 빈 람다
        )
    }
}
