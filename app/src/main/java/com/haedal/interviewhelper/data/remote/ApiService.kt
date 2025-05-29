package com.haedal.interviewhelper.data.remote

import com.haedal.interviewhelper.data.remote.response.AnalysisResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {

    @Multipart
    @POST("interviewHelper/analyze") // 서버 경로에 맞춰서 작성
    suspend fun uploadAudio(
        @Part file: MultipartBody.Part
    ): Response<AnalysisResponse>

}