// HomeActivity.kt
package com.haedal.interviewhelper.presentation.activity.home

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.haedal.interviewhelper.domain.helpfunction.*
import com.haedal.interviewhelper.presentation.activity.auth.AuthActivity
import com.haedal.interviewhelper.presentation.activity.interview.InterviewActivity
import com.haedal.interviewhelper.presentation.activity.result.ResultActivity
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.viewmodel.HomeViewModel
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkAudioPermission()
        insertDummyDataForTestUser()

        setContent {
            InterviewHelperTheme {
                HomeScreenContainer(
                    homeViewModel = homeViewModel,
                    userViewModel = userViewModel,
                    onStartInterview = { selectedQuestion ->
                        val intent = Intent(this, InterviewActivity::class.java).apply {
                            putExtra("question", selectedQuestion)
                        }
                        startActivity(intent)
                        finish()
                    },
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        moveActivity<AuthActivity>(context = this, finishFlag = true)
                    },
                    onDeleteQuestion = { question ->
                        lifecycleScope.launch {
                            val success = userViewModel.deleteResultByQuestion(question)
                            if (success) {
                                Toast.makeText(this@HomeActivity, "삭제 완료", Toast.LENGTH_SHORT).show()
                                userViewModel.loadRecentQuestions()
                            } else {
                                Toast.makeText(this@HomeActivity, "삭제 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onShowQuestion = { question ->
                        openResultActivityByQuestion(question)
                    }
                )
            }
        }
    }

    private fun checkAudioPermission() {
        val permissionsNeeded = listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.VIBRATE
        ).filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isNotEmpty()) {
            requestAudioPermissionLauncher.launch(permissionsNeeded.toTypedArray())
        }
    }

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { !it }) {
            Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openResultActivityByQuestion(question: String) {
        lifecycleScope.launch {
            try {
                val result = userViewModel.loadResultByQuestion(question)
                if (result != null) {
                    val intent = Intent(this@HomeActivity, ResultActivity::class.java).apply {
                        putExtra("question", result.question)
                        putExtra("result_json", Gson().toJson(result.resultList))
                        putExtra("server_message", result.serverMessage)
                        putExtra("analysis_feedback", result.feedback)
                        putExtra("save", false)
                    }
                    val options = ActivityOptions.makeCustomAnimation(
                        this@HomeActivity,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                    startActivity(intent, options.toBundle())
                } else {
                    Toast.makeText(this@HomeActivity, "결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun HomeScreenContainer(
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel,
    onStartInterview: (String) -> Unit,
    onLogout: () -> Unit,
    onDeleteQuestion: (String) -> Unit,
    onShowQuestion: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var userName by remember { mutableStateOf("사용자") }
    var dailyQuestion by remember { mutableStateOf("") }
    var questionList by remember { mutableStateOf(emptyList<Pair<String, String>>()) }
    var contentList by remember { mutableStateOf(emptyList<Pair<String, String>>()) }
    val recentQuestions by userViewModel.recentQuestions

    LaunchedEffect(Unit) {
        userName = userViewModel.loadUserName()
        dailyQuestion = loadDailyQuestion()
        questionList = loadPopularQuestions()
        contentList = loadGlobalContents()
        userViewModel.loadRecentQuestions()
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "홈 화면 로딩 중이에요.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        HomeScreen(
            userName = userName,
            questionList = questionList,
            dailyQuestion = dailyQuestion,
            contentList = contentList,
            recentQuestions = recentQuestions,
            onStartInterview = onStartInterview,
            onLogout = onLogout,
            onDeleteQuestion = onDeleteQuestion,
            onShowQuestion = onShowQuestion,
            homeViewModel = homeViewModel
        )
    }
}

