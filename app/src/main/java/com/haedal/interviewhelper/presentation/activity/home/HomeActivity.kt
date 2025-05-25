package com.haedal.interviewhelper.presentation.activity.home

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Context
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.haedal.interviewhelper.domain.helpfunction.moveActivity
import com.haedal.interviewhelper.presentation.activity.auth.AuthActivity
import com.haedal.interviewhelper.presentation.activity.interview.InterviewActivity
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userName = FirebaseAuth.getInstance().currentUser?.email ?: "사용자"

        setContent {
            HomeScreen(
                userName = userName,
                onStartInterview = {
                    moveActivity<InterviewActivity>(context = this, finishFlag = false)
                },
                feedbackList = listOf(
                    "자신의 단점을 말해보세요." to "말투가 소심하게 들릴 수 있어요. 조금 더 확신 있게 말하면 좋아요.",
                    "성공적인 프로젝트 경험을 말해보세요." to "목표 중심으로 정리하면 더 설득력 있어요.",
                    "갈등 상황에서 어떻게 대처했나요?" to "상황 설명이 구체적이면 더 신뢰를 줄 수 있어요.",
                    "팀워크를 발휘한 사례가 있나요?" to "본인의 역할에 대해 더 명확히 강조하면 좋겠어요."
                ),

                contentList = listOf(
                    "모범 답변 듣기" to "다른 사람들의 실제 인터뷰 답변을 들어보세요.",
                    "AI 피드백 예시 보기" to "AI가 실제로 어떻게 피드백을 주는지 확인해보세요.",
                    "면접관이 좋아하는 키워드 정리" to "답변에 활용하면 좋은 키워드를 모아봤어요.",
                    "면접 준비 체크리스트" to "인터뷰 전 필수 준비사항을 빠짐없이 확인하세요."
                )
                ,
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    moveActivity<AuthActivity>(context = this, finishFlag = true)
                }
            )
        }
    }
}