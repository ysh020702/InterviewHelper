package com.haedal.interviewhelper.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisResponse (
    val message: String,
    val results: List<ResultItem>,
    val feedback: String
)