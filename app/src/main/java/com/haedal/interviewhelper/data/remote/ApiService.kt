package com.haedal.interviewhelper.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {

    @Multipart
    @POST("interviewHelper/upload") // Nginx 경로에 맞춰서 작성
    suspend fun uploadAudio(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>
}