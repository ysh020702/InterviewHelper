package com.haedal.interviewhelper.presentation.activity.onboarding

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.haedal.interviewhelper.domain.helpfunction.moveActivity
import com.haedal.interviewhelper.presentation.activity.home.HomeActivity
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "OnBoardingActivity"

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        Log.d(TAG, "entry-onboardingScreen")

        userViewModel.checkLoginStatus()
        if (userViewModel.isLoggedIn) {
            moveActivity<HomeActivity>(context = this, finishFlag = true)
        } else {
            setContent {
                InterviewHelperTheme {
                    OnboardingScreen()
                }
            }
        }
    }
}


