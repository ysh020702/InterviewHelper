package com.haedal.interviewhelper.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class Emotion(
    val label: String = "",
    val score: Float = 0f
)