package com.haedal.interviewhelper.presentation.activity.result

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haedal.interviewhelper.data.local.FeedbackResult
import com.haedal.interviewhelper.data.remote.response.ResultItem
import com.haedal.interviewhelper.presentation.activity.result.ResultScreen
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme
import com.haedal.interviewhelper.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResultActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val question = intent.getStringExtra("question") ?: ""
        val json = intent.getStringExtra("result_json") ?: "[]"
        val message = intent.getStringExtra("server_message") ?: ""
        val feedback = intent.getStringExtra("analysis_feedback") ?: ""
        val save = intent.getBooleanExtra("save", false)

        val resultList: List<ResultItem> = Gson().fromJson(
            json,
            object : TypeToken<List<ResultItem>>() {}.type
        )

        //Toast.makeText(this, "Save 값: $save", Toast.LENGTH_SHORT).show()
        if(save) {
            // 결과를 FeedbackResult 객체로 변환하여 저장
            val result = FeedbackResult(
                question = question,
                resultList = resultList,
                serverMessage = message,
                feedback = feedback
            )

            lifecycleScope.launch {
                val isSuccess = userViewModel.saveResultToFirebase(result)
                if (isSuccess) {
                    Toast.makeText(this@ResultActivity, "결과 저장 완료!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ResultActivity, "결과 저장 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

        setContent {
            InterviewHelperTheme {
                ResultScreen(
                    question = question,
                    resultList = resultList,
                    serverMessage = message,
                    feedback = feedback
                )
            }
        }
    }
}
