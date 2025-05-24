package com.haedal.interviewhelper.presentation.activity.onboarding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme

class OnBoardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterviewHelperTheme {
                OnboardingScreen()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InterviewHelperTheme {
        OnboardingScreen()
    }
}