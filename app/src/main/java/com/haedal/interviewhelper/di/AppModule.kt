package com.haedal.interviewhelper.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.haedal.interviewhelper.data.remote.ApiService
import com.haedal.interviewhelper.data.remote.RetrofitInstance
import com.haedal.interviewhelper.data.repository.AudioRecorderRepositoryImpl
import com.haedal.interviewhelper.data.repository.AudioUploadRepositoryImpl
import com.haedal.interviewhelper.domain.repository.AudioRecorderRepository
import com.haedal.interviewhelper.domain.repository.AudioUploadRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    fun provideApiService(): ApiService = RetrofitInstance.api

    @Provides
    fun provideAudioRepository(
        apiService: ApiService
    ): AudioUploadRepository = AudioUploadRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideAudioRecorderRepository(
        @ApplicationContext context: Context
    ): AudioRecorderRepository {
        return AudioRecorderRepositoryImpl(context)
    }

}