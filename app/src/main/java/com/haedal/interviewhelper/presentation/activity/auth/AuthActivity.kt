package com.haedal.interviewhelper.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.haedal.interviewhelper.presentation.activity.home.HomeActivity
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

