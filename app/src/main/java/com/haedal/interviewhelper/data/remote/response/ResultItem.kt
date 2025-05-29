package com.haedal.interviewhelper.data.remote.response

data class ResultItem (
    val sentence: String,
    val emotions: List<Emotion>
)