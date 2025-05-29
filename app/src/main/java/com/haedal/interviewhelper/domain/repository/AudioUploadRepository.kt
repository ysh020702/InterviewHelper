package com.haedal.interviewhelper.domain.repository

import com.haedal.interviewhelper.data.remote.response.AnalysisResponse
import retrofit2.Response
import java.io.File

interface AudioUploadRepository {
    suspend fun uploadWav(file: File): Response<AnalysisResponse>
}