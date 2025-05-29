package com.haedal.interviewhelper.presentation.activity.result

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haedal.interviewhelper.data.remote.response.ResultItem
import com.haedal.interviewhelper.presentation.activity.result.ResultScreen
import com.haedal.interviewhelper.presentation.theme.InterviewHelperTheme

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra("result_json") ?: "[]"
        val message = intent.getStringExtra("server_message") ?: ""
        val feedback = intent.getStringExtra("analysis_feedback") ?: ""

        val resultList: List<ResultItem> = Gson().fromJson(
            json,
            object : TypeToken<List<ResultItem>>() {}.type
        )

        setContent {
            InterviewHelperTheme {
                ResultScreen(
                    resultList = resultList,
                    serverMessage = message,
                    feedback = feedback
                )
            }
        }
    }
}
