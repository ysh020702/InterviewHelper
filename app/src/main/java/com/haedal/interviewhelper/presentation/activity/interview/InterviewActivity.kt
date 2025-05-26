package com.haedal.interviewhelper.presentation.activity.interview

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.haedal.interviewhelper.presentation.activity.result.ResultActivity
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.viewmodel.InterviewViewModel
import com.haedal.interviewhelper.presentation.viewmodel.ResultState
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class InterviewActivity : ComponentActivity() {

    private val viewModel: InterviewViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val question = intent.getStringExtra("question") ?: "질문이 전달되지 않았습니다."

        setContent {
            InterviewHelperTheme {
                var userName by remember { mutableStateOf("사용자") }

                LaunchedEffect(Unit) {
                    userName = userViewModel.loadUserName()
                }

                InterviewScreen(
                    question = question,
                    userName = userName
                )
            }
        }

        observeUploadState()
    }

    // 업로드 상태 변화 감지
    private fun observeUploadState() {
        lifecycleScope.launch {
            viewModel.uploadState.collectLatest { state ->
                when (state) {
                    is ResultState.Success -> {
                        val result = viewModel.uploadResult ?: "결과 없음"
                        val intent = Intent(this@InterviewActivity, ResultActivity::class.java)
                        intent.putExtra("result", result)
                        startActivity(intent)
                        finish()
                    }
                    is ResultState.Error -> showToast("업로드 실패: ${state.message}")
                    is ResultState.Loading -> showToast("업로드 중입니다...")
                    ResultState.Idle -> {}
                }
            }
        }
    }

    // (선택) 녹음 완료 후 직접 업로드 호출할 경우 사용
    private fun uploadRecordedFile() {
        val file = File(getExternalFilesDir(null), "recorded_audio.wav")
        viewModel.uploadAudioFile(file) // 내부에서 internal로 설정되어 있어도 같은 모듈이면 호출 가능
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}