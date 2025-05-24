package com.haedal.interviewhelper.presentation.activity.onboarding

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
private const val TAG = "OnBoardingActivity"
class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InterviewHelperTheme {
                Log.d(TAG, "onCreate: ")
                OnboardingScreen()
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