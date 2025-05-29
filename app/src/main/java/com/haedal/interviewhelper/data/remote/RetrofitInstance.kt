package com.haedal.interviewhelper.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // 서버 연결까지 대기 시간
        .readTimeout(120, TimeUnit.SECONDS)    // 서버 응답까지 대기 시간 (← 이게 중요!)
        .writeTimeout(60, TimeUnit.SECONDS)   // 데이터 전송 시간
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://155.230.25.18:8000")  // 포트 없이, nginx에서 프록시 설정됨
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
