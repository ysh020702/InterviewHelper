package com.haedal.interviewhelper.presentation.activity.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            InterviewHelperTheme {
                AuthNavHost()
            }
        }
    }

    private fun showToast(text :String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}

