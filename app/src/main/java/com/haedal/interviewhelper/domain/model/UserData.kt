package com.haedal.interviewhelper.domain.model

data class UserData(
    val email: String = "",
    val name: String = "",
    val feedbacks: Map<String, FeedbackItem> = emptyMap()
)
