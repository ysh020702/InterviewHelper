package com.haedal.interviewhelper.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.haedal.interviewhelper.domain.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
open class UserViewModel @Inject constructor(
    private val fbAuth: FirebaseAuth,
    private val fbDb: FirebaseDatabase
) : ViewModel() {

    var loginResult by mutableStateOf<Result<Unit>?>(null)
    var signUpResult by mutableStateOf<Result<Unit>?>(null)

    private val dbRef = fbDb.getReference("users")

    fun login(email: String, password: String) {
        fbAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loginResult = if (task.isSuccessful) Result.success(Unit)
                else Result.failure(task.exception ?: Exception("로그인 실패"))
            }
    }

    fun signUp(email: String, password: String, name: String) {
        fbAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val user = UserData(email = email, name = name)
                dbRef.child(uid).setValue(user)
                signUpResult = Result.success(Unit)
            }
            .addOnFailureListener {
                signUpResult = Result.failure(it)
            }
    }

    fun signInWithGoogleCredential(credential: AuthCredential, onSuccess: () -> Unit) {
        fbAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                loginResult = Result.success(Unit)
            }
            .addOnFailureListener {
                loginResult = Result.failure(it)
            }
    }

    fun signUpWithGoogleCredential(credential: AuthCredential, name: String, email: String, onSuccess: () -> Unit) {
        fbAuth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val user = UserData(email = email, name = name)
                dbRef.child(uid).setValue(user)
                signUpResult = Result.success(Unit)
            }
            .addOnFailureListener {
                signUpResult = Result.failure(it)
            }
    }

    suspend fun loadUserName(): String {
        val uid = fbAuth.currentUser?.uid ?: return "사용자"
        val snapshot = dbRef.child(uid).child("name").get().await()
        return snapshot.getValue(String::class.java) ?: "사용자"
    }

    suspend fun loadUserFeedbacks(): List<Pair<String, String>> {
        val uid = fbAuth.currentUser?.uid ?: return emptyList()
        val snapshot = dbRef.child(uid).child("feedbacks").get().await()

        return snapshot.children.mapNotNull { snap ->
            val question = snap.child("question").getValue(String::class.java)
            val feedback = snap.child("feedback").getValue(String::class.java)
            if (question != null && feedback != null) question to feedback else null
        }
    }

    var isLoggedIn by mutableStateOf(false)
        private set
    fun checkLoginStatus() {
        isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    }

    fun clearResults() {
        loginResult = null
        signUpResult = null
    }
}