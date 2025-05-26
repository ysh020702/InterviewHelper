package com.haedal.interviewhelper.presentation.activity.interview

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.haedal.interviewhelper.presentation.viewmodel.InterviewViewModel
import com.haedal.interviewhelper.presentation.viewmodel.ResultState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class InterviewActivity : ComponentActivity() {

    private val viewModel: InterviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 파일 준비 (예시)
        val recordedFile = File(getExternalFilesDir(null), "recorded_audio.wav")

        // 2. 업로드 요청
        viewModel.uploadAudioFile(recordedFile)

        // 3. 상태 감지하여 UI 반영
        lifecycleScope.launch {
            viewModel.uploadState.collectLatest { state ->
                when (state) {
                    is ResultState.Loading -> showToast("업로드 중입니다...")
                    is ResultState.Success -> showToast("업로드 완료!")
                    is ResultState.Error -> showToast("업로드 실패: ${state.message}")
                    ResultState.Idle -> { /* 대기 상태 */ }
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}