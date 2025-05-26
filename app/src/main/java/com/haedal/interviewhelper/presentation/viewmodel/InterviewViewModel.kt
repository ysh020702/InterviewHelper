package com.haedal.interviewhelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haedal.interviewhelper.domain.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val audioRepository: AudioRepository
) : ViewModel() {

    private val _uploadState = MutableStateFlow<ResultState>(ResultState.Idle)
    val uploadState: StateFlow<ResultState> = _uploadState

    fun uploadAudioFile(file: File) {
        viewModelScope.launch {
            _uploadState.value = ResultState.Loading
            try {
                val response = audioRepository.uploadWav(file)
                _uploadState.value = if (response.isSuccessful) {
                    ResultState.Success
                } else {
                    ResultState.Error("오류: ${response.code()}")
                }
            } catch (e: Exception) {
                _uploadState.value = ResultState.Error("예외 발생: ${e.localizedMessage}")
            }
        }
    }
}


sealed class ResultState {
    object Idle : ResultState()
    object Loading : ResultState()
    object Success : ResultState()
    data class Error(val message: String) : ResultState()
}