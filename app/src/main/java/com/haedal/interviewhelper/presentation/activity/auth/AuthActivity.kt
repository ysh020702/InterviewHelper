package com.haedal.interviewhelper.presentation.activity.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InterviewHelperTheme {
                AuthScreen(onSignUp = ::onSignup, onLogin = ::onLogin)
            }
        }
    }

    private fun onSignup(){
        showToast("회원가입 기능 구현 전입니다.")
    }

    private fun onLogin(){
        showToast("로그인 기능 구현 전입니다.")
    }

    private fun showToast(text :String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}



@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    InterviewHelperTheme {
        AuthScreen(onSignUp = {}, onLogin = {})
    }
}