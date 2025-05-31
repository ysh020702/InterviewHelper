package com.haedal.interviewhelper.presentation.activity.interview

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.haedal.interviewhelper.presentation.activity.result.ResultActivity
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.viewmodel.InterviewViewModel
import com.haedal.interviewhelper.presentation.viewmodel.ResultState
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.google.gson.Gson


/*
액티비티: UI 설정 및 뷰모델, 상태 관찰
스크린콘테이너: 뷰모델의 상태를 수집해서 인터뷰스크린에 넘김
인터뷰스크린: 딱 받아서 UI 만 표현
 */

@Suppress("UNCHECKED_CAST")
@AndroidEntryPoint
class InterviewActivity : ComponentActivity() {

    private val viewModel: InterviewViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val RECORD_AUDIO_REQUEST_CODE = 1001
    var question: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        question = intent.getStringExtra("question") ?: ""
        if (question == "") {
            showToast("질문이 전달되지 않았습니다. 홈 화면으로 돌아갑니다.")
            finish()
            return
        }

        // 권한 체크 및 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_REQUEST_CODE
            )
        }

        setContent {
            InterviewHelperTheme {
                var userName by remember { mutableStateOf("사용자") }
                LaunchedEffect(Unit) {
                    userName = userViewModel.loadUserName()
                }

                val uploadState by viewModel.uploadState.collectAsState()
                BackHandler(enabled = uploadState is ResultState.Loading) {
                    // 아무것도 안 함 = 뒤로가기 무시됨
                    Toast.makeText(this, "분석 중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show()
                }

                InterviewScreenContainer(
                    context = this@InterviewActivity,
                    question = question,
                    userName = userName,
                    viewModel = viewModel,
                    uploadState = uploadState,
                    onError = { msg -> showToast(msg) }
                )
            }
        }

        observeUploadState()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    //업로드 상태를 관찰하고, 성공하면 결과 화면으로 이동
    private fun observeUploadState() {
        lifecycleScope.launch {
            viewModel.uploadState.collectLatest { state ->
                when (state) {
                    is ResultState.Success -> {
                        showToast("수신 완료. 결과 화면으로 이동합니다.")
                        val resultList = viewModel.uploadResult
                        val message = viewModel.serverMessage
                        val feedback = viewModel.analysisFeedback

                        //1초 기다리기
                        delay(1000)

                        val intent = Intent(this@InterviewActivity, ResultActivity::class.java)
                        intent.putExtra("question", question)
                        intent.putExtra("result_json", Gson().toJson(resultList))
                        intent.putExtra("server_message", message)
                        intent.putExtra("analysis_feedback", feedback)
                        val options = ActivityOptions.makeCustomAnimation(
                            this@InterviewActivity,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                        startActivity(intent, options.toBundle())
                        finish()
                    }
                    is ResultState.Error -> showToast("업로드 실패: ${state.message}")
                    is ResultState.Loading -> showToast("업로드 중입니다...")
                    ResultState.Idle -> {}
                }
            }
        }
    }

}



@Composable
fun InterviewScreenContainer(
    context: Context,
    question: String,
    userName: String,
    viewModel: InterviewViewModel,
    uploadState: ResultState,
    onError: (String) -> Unit
) {
    val isRecording by remember { derivedStateOf { viewModel.isRecording } }
    val elapsed by remember { derivedStateOf { viewModel.elapsedTime } }
    val frequencies by remember { derivedStateOf { viewModel.frequencyBins } }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        )
        viewModel.startFrequencyAnalysis()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopFrequencyAnalysis() }
    }

    InterviewScreen(
        question = question,
        userName = userName,
        isRecording = isRecording,
        elapsed = elapsed,
        frequencies = frequencies,
        uploadState = uploadState,         // ✅ 전달
        onToggleRecording = {
        if (uploadState !is ResultState.Loading) {
            viewModel.toggleRecording(context = context, userName = userName, question = question)
        }
    }
    )
}
