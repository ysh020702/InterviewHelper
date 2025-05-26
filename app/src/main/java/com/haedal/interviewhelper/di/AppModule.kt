package com.haedal.interviewhelper.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.haedal.interviewhelper.data.remote.ApiService
import com.haedal.interviewhelper.data.remote.RetrofitInstance
import com.haedal.interviewhelper.data.repository.AudioRepositoryImpl
import com.haedal.interviewhelper.domain.repository.AudioRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    ): AudioRepository = AudioRepositoryImpl(apiService)

}