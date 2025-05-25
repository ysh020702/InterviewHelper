package com.haedal.interviewhelper.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.haedal.interviewhelper.domain.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {

    private val usersRef = database.getReference("users")

    var loginResult by mutableStateOf<Result<Unit>?>(null)
    var signUpResult by mutableStateOf<Result<Unit>?>(null)

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loginResult = if (task.isSuccessful) Result.success(Unit)
                else Result.failure(task.exception ?: Exception("로그인 실패"))
            }
    }

    fun signUp(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val user = UserData(email = email, name = name)
                usersRef.child(uid).setValue(user)
                signUpResult = Result.success(Unit)
            }
            .addOnFailureListener {
                signUpResult = Result.failure(it)
            }
    }

    suspend fun loadUserFeedbacks(): List<Pair<String, String>> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val snapshot = usersRef.child(uid).child("feedbacks").get().await()

        return snapshot.children.mapNotNull { snap ->
            val question = snap.child("question").getValue(String::class.java)
            val feedback = snap.child("feedback").getValue(String::class.java)
            if (question != null && feedback != null) question to feedback else null
        }
    }

    fun clearResults() {
        loginResult = null
        signUpResult = null
    }
}
