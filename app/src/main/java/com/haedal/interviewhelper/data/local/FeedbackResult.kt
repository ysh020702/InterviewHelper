package com.haedal.interviewhelper.data.local

import com.haedal.interviewhelper.data.remote.response.ResultItem
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackResult(
    val question: String = "",
    val resultList: List<ResultItem> = emptyList(),  // 기본값 추가
    val serverMessage: String = "",
    val feedback: String = "",
    val timestamp: Long = System.currentTimeMillis()
)