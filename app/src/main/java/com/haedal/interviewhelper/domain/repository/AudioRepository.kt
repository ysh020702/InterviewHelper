package com.haedal.interviewhelper.domain.repository

import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

interface AudioRepository {
    suspend fun uploadWav(file: File): Response<ResponseBody>
}