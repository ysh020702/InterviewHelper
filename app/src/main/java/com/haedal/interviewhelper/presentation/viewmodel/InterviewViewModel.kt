package com.haedal.interviewhelper.presentation.viewmodel

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haedal.interviewhelper.data.model.FrequencyAnalyzer
import com.haedal.interviewhelper.domain.helpfunction.FFTUtils
import com.haedal.interviewhelper.domain.repository.AudioRecorderRepository
import com.haedal.interviewhelper.domain.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val audioRecorder: AudioRecorderRepository,
    private val audioRepository: AudioRepository
) : ViewModel() {

    // ====== 녹음 상태 ======
    var isRecording by mutableStateOf(false)
        private set

    var elapsedTime by mutableStateOf(0)
        private set

    private var timerJob: Job? = null
    private lateinit var currentFile: File

    // ====== 업로드 상태 ======
    private val _uploadState = MutableStateFlow<ResultState>(ResultState.Idle)
    val uploadState: StateFlow<ResultState> = _uploadState

    // ====== 주파수 분석 상태 ======
    var frequencyBins by mutableStateOf<List<Float>>(emptyList())
        private set

    private var isAnalyzing = false
    private var analyzerJob: Job? = null
    private var audioRecord: AudioRecord? = null

    // ====== 녹음 시작/정지 ======
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun toggleRecording(userName: String, question: String) {
        if (isRecording) stopRecording() else startRecording(userName, question)
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording(userName: String, question: String) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val sanitizedQuestion = question.replace(Regex("[^ㄱ-ㅎ가-힣a-zA-Z0-9]"), "_")
        val fileName = "${userName}_${sanitizedQuestion}_$timestamp.wav"

        currentFile = audioRecorder.start(fileName)
        isRecording = true
        elapsedTime = 0
        startFrequencyAnalysis()

        timerJob = viewModelScope.launch {
            while (isRecording) {
                delay(1000)
                elapsedTime += 1
            }
        }
    }

    private fun stopRecording() {
        isRecording = false
        timerJob?.cancel()
        audioRecorder.stop()
        stopFrequencyAnalysis()
        uploadAudioFile(currentFile)
    }

    internal fun uploadAudioFile(file: File) {
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

    // ====== 주파수 분석 ======
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    internal fun startFrequencyAnalysis() {
        if (isAnalyzing) return
        isAnalyzing = true

        val sampleRate = 44100
        val fftSize = 1024
        val buffer = ShortArray(fftSize)
        val window = hannWindow(fftSize)

        val minBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            maxOf(minBufferSize, fftSize * 2)
        ).apply { startRecording() }

        analyzerJob = viewModelScope.launch(Dispatchers.Default) {
            while (isAnalyzing) {
                val read = audioRecord?.read(buffer, 0, fftSize) ?: 0
                if (read > 0) {
                    val real = FloatArray(fftSize) { i ->
                        if (i < read) buffer[i] / 32768f * window[i] else 0f
                    }
                    val imag = FloatArray(fftSize)

                    FFTUtils.fft(real, imag)

                    val magnitudes = real.indices.take(32).map { i ->
                        val mag = sqrt(real[i].pow(2) + imag[i].pow(2))
                        20 * log10(mag + 1e-6f) // 로그 스케일
                    }

                    withContext(Dispatchers.Main) {
                        frequencyBins = magnitudes
                    }
                }
                delay(33) // 30fps
            }
        }
    }

    internal fun stopFrequencyAnalysis() {
        isAnalyzing = false
        analyzerJob?.cancel()
        analyzerJob = null
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    private fun hannWindow(size: Int): FloatArray =
        FloatArray(size) { i -> (0.5 - 0.5 * cos(2 * PI * i / size)).toFloat() }
}




sealed class ResultState {
    object Idle : ResultState()
    object Loading : ResultState()
    object Success : ResultState()
    data class Error(val message: String) : ResultState()
}