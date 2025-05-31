package com.haedal.interviewhelper.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ResultItem(
    val sentence: String = "",
    val emotions: List<Emotion> = emptyList()
)
