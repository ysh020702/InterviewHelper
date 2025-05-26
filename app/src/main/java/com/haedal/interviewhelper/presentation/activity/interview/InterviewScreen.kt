package com.haedal.interviewhelper.presentation.activity.interview

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haedal.interviewhelper.presentation.theme.Black
import com.haedal.interviewhelper.presentation.theme.Color02
import com.haedal.interviewhelper.presentation.theme.Color04
import com.haedal.interviewhelper.presentation.theme.Color05
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.theme.White
import com.haedal.interviewhelper.presentation.viewmodel.InterviewViewModel
import com.haedal.interviewhelper.presentation.viewmodel.ResultState

@SuppressLint("MissingPermission")
@Composable
fun InterviewScreen(
    question: String,
    userName: String,
    viewModel: InterviewViewModel = hiltViewModel()
) {
    val isRecording by remember { derivedStateOf { viewModel.isRecording } }
    val elapsed by remember { derivedStateOf { viewModel.elapsedTime } }
    val frequencies = viewModel.frequencyBins
    val uploadState by viewModel.uploadState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit)  @androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO) {
        viewModel.startFrequencyAnalysis() // 자동 분석 시작
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopFrequencyAnalysis()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B2A4E))
    ) {
        // 질문 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.35f)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "“$question”",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            )
        }

        // 하단 컨트롤 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.65f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 마이크 버튼
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2F2EF))
                        .clickable @androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO) {
                            viewModel.toggleRecording(userName, question)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "녹음 버튼",
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // 타이머
                Text(
                    text = String.format("%02d:%02d", elapsed / 60, elapsed % 60),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color04,
                        fontWeight = FontWeight.Bold
                    )
                )

                // 파형 시각화
                FrequencyVisualizer(frequencies)

                // 업로드 상태 피드백
                if (uploadState is ResultState.Loading) {
                    CircularProgressIndicator()
                } else if (uploadState is ResultState.Success) {
                    Text("업로드 완료!", color = Color.Green)
                } else if (uploadState is ResultState.Error) {
                    Text(
                        (uploadState as ResultState.Error).message,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


@Composable
fun FrequencyVisualizer(frequencies: List<Float>) {
    val maxHeight = 80.dp
    val normalized = frequencies.map { ((it + 60f).coerceIn(0f, 60f)) }

    Row(
        modifier = Modifier
            .height(maxHeight)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        normalized.forEach { height ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(height.dp)
                    .background(Color(0xFF3D5AFE), RoundedCornerShape(2.dp))
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InterviewPreview() {
    InterviewHelperTheme {
        InterviewScreen("username","Example Question")
    }
}