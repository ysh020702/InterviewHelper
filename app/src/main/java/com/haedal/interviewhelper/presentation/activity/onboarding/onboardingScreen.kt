package com.haedal.interviewhelper.presentation.activity.onboarding

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        derivedStateOf { pagerState.currentPage == 2 }
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
            //TODO: 사용 안내 화면 만들기
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = when (page) {
                            0 -> Color(0xFFE0F7FA)
                            1 -> Color(0xFFFFF9C4)
                            2 -> Color(0xFFFFE0B2)
                            else -> Color.LightGray
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "슬라이드 ${page + 1}", style = MaterialTheme.typography.headlineMedium)
            }
        }

        // 페이지 인디케이터 (작은 점 3개)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(PAGE_NUMBER) { index ->
                val color = if (pagerState.currentPage == index) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(12.dp)
                        .background(color = color, shape = CircleShape)
                )
            }
        }

        PrimaryButton(
            text = if (isLastPage) "시작하기" else "다음",
            onClick = {
                if (isLastPage) {
                    context.startActivity(Intent(context, AuthActivity::class.java))
                    (context as? Activity)?.finish()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            containerColor = Color03,
            contentColor = White

        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    InterviewHelperTheme {
        OnboardingScreen()
    }
}