package com.haedal.interviewhelper.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthViewModel @Inject constructor() : ViewModel() {
    var loginResult by mutableStateOf<Result<Unit>?>(null)

    var signUpResult by mutableStateOf<Result<Unit>?>(null)

    fun login(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loginResult = if (task.isSuccessful) Result.success(Unit)
                else Result.failure(task.exception ?: Exception("로그인 실패"))
            }
    }

    fun signUp(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                signUpResult = if (task.isSuccessful) Result.success(Unit)
                else Result.failure(task.exception ?: Exception("회원가입 실패"))
            }
    }

    fun clearResults() {
        loginResult = null
        signUpResult = null
    }
}