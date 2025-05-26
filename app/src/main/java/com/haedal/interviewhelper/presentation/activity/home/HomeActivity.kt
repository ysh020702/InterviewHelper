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

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val viewModel: UserViewModel by viewModels()
    private val RECORD_AUDIO_PERMISSION = android.Manifest.permission.RECORD_AUDIO
    private val PERMISSION_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkAudioPermission()

        CoroutineScope(Dispatchers.IO).launch {
            val name = viewModel.loadUserName()
            val feedbackList = viewModel.loadUserFeedbacks()
            val contentList = loadGlobalContents()
            val dailyQuestion = loadDailyQuestion()

            withContext(Dispatchers.Main) {
                setContent {
                    InterviewHelperTheme {
                        HomeScreen(
                            userName = name,
                            feedbackList = feedbackList,
                            contentList = contentList,
                            onStartInterview = {
                                moveActivity<InterviewActivity>(context = this@HomeActivity, finishFlag = false)
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
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> {
                // 권한 이미 있음
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                Toast.makeText(this, "이 기능을 사용하려면 마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            else -> {
                requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "녹음을 위해 마이크 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }


}