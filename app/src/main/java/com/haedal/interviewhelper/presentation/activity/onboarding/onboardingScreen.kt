package com.haedal.interviewhelper.presentation.activity.onboarding

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haedal.interviewhelper.domain.helpfunction.moveActivity
import com.haedal.interviewhelper.presentation.activity.auth.AuthActivity
import com.haedal.interviewhelper.presentation.theme.Color03
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.theme.PrimaryButton
import com.haedal.interviewhelper.presentation.theme.White
import kotlinx.coroutines.launch

private const val PAGE_NUMBER = 3

@Composable
fun OnboardingScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { PAGE_NUMBER }
    )

    val isLastPage by remember {
        derivedStateOf { pagerState.currentPage == PAGE_NUMBER - 1 }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 로그인 버튼 (우상단 고정)
        TextButton(
            onClick = {
                moveActivity<AuthActivity>(context = context, finishFlag = true)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "로그인",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 페이지 인디케이터 (좌상단 30% 위치의 직사각형)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 180.dp), // 약 30%
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(PAGE_NUMBER) { index ->
                val color = if (pagerState.currentPage == index) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(24.dp)
                        .background(color = color, shape = RoundedCornerShape(percent = 50))
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = when (page) {
                                0 -> Color(0xFFE0F7FA)
                                1 -> Color(0xFFFFF9C4)
                                2 -> Color(0xFFFFE0B2)
                                else -> Color.LightGray
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "슬라이드 ${page + 1}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            // 텍스트 버튼 (시작하기 / 다음)
            TextButton(
                onClick = {
                    if (isLastPage) {
                        moveActivity<AuthActivity>(context = context, finishFlag = true)
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = if (isLastPage) "시작하기" else "다음",
                    color = Color03,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    InterviewHelperTheme {
        OnboardingScreen()
    }
}