package com.haedal.interviewhelper.presentation.activity.home

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.haedal.interviewhelper.domain.helpfunction.*
import com.haedal.interviewhelper.domain.helpfunction.moveActivity
import com.haedal.interviewhelper.presentation.activity.auth.AuthActivity
import com.haedal.interviewhelper.presentation.activity.interview.InterviewActivity
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import android.content.Intent

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val viewModel: UserViewModel by viewModels()
    private val RECORD_AUDIO_PERMISSION = android.Manifest.permission.RECORD_AUDIO
    private val PERMISSION_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkAudioPermission()
        insertDummyDataForTestUser()

        CoroutineScope(Dispatchers.IO).launch {
            val name = viewModel.loadUserName()
            val dailyQuestion = loadDailyQuestion()             //오늘의 추천
            val feedbackList = viewModel.loadUserFeedbacks()    //유저 최근 질문
            val contentList = loadGlobalContents()              //글로벌 콘텐츠


            withContext(Dispatchers.Main) {
                setContent {
                    InterviewHelperTheme {
                        HomeScreen(
                            userName = name,
                            feedbackList = feedbackList,
                            dailyQuestion = dailyQuestion,
                            contentList = contentList,
                            onStartInterview = { selectedQuestion ->
                                val intent = Intent(this@HomeActivity, InterviewActivity::class.java)
                                intent.putExtra("question", selectedQuestion)
                                startActivity(intent)
                            },
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                moveActivity<AuthActivity>(context = this@HomeActivity, finishFlag = true)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun checkAudioPermission() {
        val audioGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val vibrateGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED

        when {
            audioGranted && vibrateGranted -> {
                // 둘 다 권한 있음
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                Toast.makeText(this, "이 기능을 사용하려면 마이크 및 진동 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                requestAudioPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.VIBRATE
                    )
                )
            }
            else -> {
                requestAudioPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.VIBRATE
                    )
                )
            }
        }
    }
    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        val vibrateGranted = permissions[Manifest.permission.VIBRATE] ?: false

        if (audioGranted && vibrateGranted) {
            // 권한 허용됨
        } else {
            Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }


}