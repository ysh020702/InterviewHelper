package com.haedal.interviewhelper.data.repository

import com.haedal.interviewhelper.data.remote.ApiService
import com.haedal.interviewhelper.data.remote.response.AnalysisResponse
import com.haedal.interviewhelper.domain.repository.AudioUploadRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class AudioUploadRepositoryImpl(
    private val api: ApiService
) : AudioUploadRepository {

    override suspend fun uploadWav(file: File): Response<AnalysisResponse> {
        val requestFile = file.asRequestBody("audio/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        return api.uploadAudio(body)
    }
}