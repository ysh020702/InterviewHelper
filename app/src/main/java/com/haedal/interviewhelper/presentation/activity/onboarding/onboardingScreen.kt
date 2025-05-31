package com.haedal.interviewhelper.presentation.activity.onboarding


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haedal.interviewhelper.domain.helpfunction.moveActivity
import com.haedal.interviewhelper.presentation.activity.auth.AuthActivity
import com.haedal.interviewhelper.presentation.theme.Color02
import com.haedal.interviewhelper.presentation.theme.Color03
import com.haedal.interviewhelper.presentation.theme.Color04
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.theme.NotoSansKR
import com.haedal.interviewhelper.presentation.theme.PrimaryButton
import com.haedal.interviewhelper.presentation.theme.White
import kotlinx.coroutines.launch

private const val PAGE_NUMBER = 3


@OptIn(ExperimentalFoundationApi::class) // rememberPagerState 사용을 위해 추가
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

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(White)
    ) {
        // 화면 높이의 30%를 계산합니다.
        val indicatorTopPadding = this.maxHeight * 0.2f

        // 로그인 버튼 (우상단 고정)
        TextButton(
            onClick = {
                moveActivity<AuthActivity>(context = context, finishFlag = true)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top=32.dp, end=16.dp)
        ) {
            Text(
                text = "Skip",
                color = Color04,
                style = TextStyle(
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            )
        }

        // 페이지 인디케이터 (화면 높이의 20% 위치에 좌측 정렬)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = indicatorTopPadding) // 동적 top padding 적용, 좌측 여백도 조정
                .align(Alignment.TopStart), // BoxWithConstraints 내에서 TopStart에 정렬
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            repeat(PAGE_NUMBER) { index ->
                val color = if (pagerState.currentPage == index) Color04 else Color.LightGray // 테마 색상 사용 고려
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(24.dp)
                        .height(8.dp)
                        .background(color = color, shape = RoundedCornerShape(percent = 50))
                )
            }
        }

        // HorizontalPager와 하단 버튼을 포함하는 Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = indicatorTopPadding + 16.dp, // 인디케이터 높이(8.dp)와 추가 여백 고려
                    bottom = 48.dp, // 하단 버튼을 위한 충분한 공간
                    start = 24.dp,
                    end = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally // 내부 컨텐츠를 중앙 정렬 (선택 사항)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            if (pagerState.currentPage < PAGE_NUMBER - 1) {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                    )
            ) { page ->
                val (title, description) = when (page) {
                    0 -> "답변을 녹음해보세요" to
                            "예상 질문에 답하듯 말하면,\n앱이 음성을 분석해 준비를 도와줍니다."
                    1 -> "AI가 말투와 감정을 분석해요" to
                            "전달력이 부족하진 않았는지,\n말투에 긴장감이 있진 않았는지\nAI가 판단해 알려줘요."
                    2 -> "추천 피드백으로 답변을 개선하세요" to
                            "AI가 제시하는 수정 예시와 함께,\n더 나은 말하기를 연습할 수 있어요."
                    else -> "" to ""
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = White
                        ),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp,vertical = 48.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Left
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Left
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            // 텍스트 버튼 (시작하기 / 다음)
            // 조건에 따라 색상만 달라짐
            val isStart = isLastPage

            val buttonModifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    color = if (isStart) Color03 else White,
                    shape = RoundedCornerShape(8.dp)
                )

            val containerColor = if (isStart) Color03 else White
            val contentColor = if (isStart) White else Color03

            PrimaryButton(
                text = if (isStart) "시작하기" else "다음",
                onClick = {
                    if (isStart) {
                        moveActivity<AuthActivity>(context = context, finishFlag = true)
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = buttonModifier,
                containerColor = containerColor,
                contentColor = contentColor
            )

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