package com.haedal.interviewhelper.data.repository

import com.haedal.interviewhelper.data.remote.ApiService
import com.haedal.interviewhelper.domain.repository.AudioRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): AudioRepository{
    override suspend fun uploadWav(file: File): Response<ResponseBody> {
        val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return apiService.uploadAudio(body)
    }
}