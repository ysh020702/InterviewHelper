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
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    moveActivity<AuthActivity>(context = this, finishFlag = true)
                }
            )
        }
    }
}