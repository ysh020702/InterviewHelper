package com.haedal.interviewhelper.presentation.activity.interview

import android.annotation.SuppressLint
import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haedal.interviewhelper.presentation.theme.*
import com.haedal.interviewhelper.presentation.viewmodel.InterviewViewModel
import com.haedal.interviewhelper.presentation.viewmodel.ResultState
import kotlin.math.log10
import kotlin.math.pow

@SuppressLint("MissingPermission")
@Composable
fun InterviewScreen(
    question: String,
    userName: String,
    isRecording: Boolean,                   //현재 녹음 중인지 표시 \
    elapsed: Int,
    frequencies: List<Float>,               //주파수 받아서 오디오 바 표시
    uploadState: ResultState,               //로딩 중에 다른 화면 표시
    onToggleRecording: () -> Unit           //녹음 버튼이 눌렸을 때 실행할 함수
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B2A4E))
    ) {
        QuestionDisplay(
            question = question,
            modifier = Modifier.weight(0.35f)
        )
        ControlPanel(
            isRecording = isRecording,
            elapsed = elapsed,
            frequencies = frequencies,
            uploadState = uploadState,   // ✅ 전달
            onToggleRecording = onToggleRecording
        )
    }
}


@Composable
fun QuestionDisplay(
    question: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "\u201c$question\u201d",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        )
    }
}


@Composable
fun ControlPanel(
    isRecording: Boolean,
    elapsed: Int,
    frequencies: List<Float>,
    uploadState: ResultState,
    onToggleRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUploading = uploadState is ResultState.Loading

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color.White)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isUploading) 0.5f else 1f),              //업로드 및 처리 시 투명해지게
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MicButton(isRecording && !isUploading, onToggleRecording)
            TimerDisplay(elapsed)
            FrequencyVisualizer(frequencies)
        }

        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("분석 중입니다...", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun MicButton(isRecording: Boolean, onToggleRecording: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color(0xFFE2F2EF))
            .clickable { onToggleRecording() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = "녹음 버튼",
            tint = Color.Black,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun TimerDisplay(elapsed: Int) {
    Text(
        text = String.format("%02d:%02d", elapsed / 60, elapsed % 60),
        style = MaterialTheme.typography.bodyLarge.copy(
            color = Color04,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun FrequencyVisualizer(frequencies: List<Float>) {
    val maxHeight = 80.dp
    val maxBars = 32

    // 1. dB 변환 및 클램핑 (0~60dB 기준)
    val dbList = frequencies.map {
        val db = 20 * log10(it + 1e-6f)
        db.coerceIn(-30f, 60f) // max = 60dB
    }

    // 2. 정규화 (0.0 ~ 1.0) 후 감도 조절
    val normalized = dbList.map { (it / 60f).pow(0.8f) }

    // 3. 시각화: 최소 1칸 보장
    val heights = normalized.map { (it * maxHeight.value).coerceAtLeast(4f) }

    // 4. UI 출력
    Row(
        modifier = Modifier
            .height(maxHeight)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(h.dp)
                    .background(Color(0xFF3D5AFE), RoundedCornerShape(2.dp))
            )
        }
    }
}
