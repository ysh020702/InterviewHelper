package com.haedal.interviewhelper.presentation.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haedal.interviewhelper.data.remote.response.ResultItem
import com.haedal.interviewhelper.domain.helpfunction.FFTUtils
import com.haedal.interviewhelper.domain.repository.AudioRecorderRepository
import com.haedal.interviewhelper.domain.repository.AudioUploadRepository
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
    private val audioUploadRepository: AudioUploadRepository
) : ViewModel() {

    // ====== 녹음 상태 ======
    var isRecording by mutableStateOf(false)
        private set

    var elapsedTime by mutableStateOf(0)
        private set

    private var timerJob: Job? = null
    private lateinit var currentFile: File

    // ====== 주파수 분석 상태 ======
    var frequencyBins by mutableStateOf<List<Float>>(emptyList())
        private set

    // ====== 최대 녹음 시간(초) ======
    val autoStopSecond = 180

    // ====== 업로드 상태 ======
    private val _uploadState = MutableStateFlow<ResultState>(ResultState.Idle)
    val uploadState: StateFlow<ResultState> = _uploadState

    // ====== 업로드 결과 ======
    var serverMessage: String by mutableStateOf("")
        private set
    var uploadResult: List<ResultItem> by mutableStateOf(emptyList())
        private set
    var analysisFeedback: String by mutableStateOf("")
        private set

    private var isAnalyzing = false
    private var analyzerJob: Job? = null
    private var audioRecord: AudioRecord? = null

    // ====== 녹음 시작/정지 ======
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun toggleRecording(context: Context, userName: String, question: String) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Toast.makeText(context, "녹음을 위해 마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (isRecording) {
            showToast(context, "녹음을 종료합니다. 분석을 시작합니다...")
            stopRecording()
        } else {
            showToast(context, "녹음을 시작합니다. 최대 ${autoStopSecond}까지만 녹음 가능합니다.}")
            startRecording(context, userName, question)
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording(context: Context, userName: String, question: String) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val sanitizedQuestion = question.replace(Regex("[^ㄱ-ㅎ가-힣a-zA-Z0-9]"), "_")
        val fileName = "${userName}_${sanitizedQuestion}_$timestamp"                                    //유저명_질문_타임스탬프.m4a

        currentFile = audioRecorder.start(fileName)
        isRecording = true
        elapsedTime = 0
        startFrequencyAnalysis()

        timerJob = viewModelScope.launch {
            while (isRecording) {
                delay(1000)
                elapsedTime += 1

                if (elapsedTime >= autoStopSecond) {
                    showToast(context, "${autoStopSecond/60}분이 지나 녹음을 종료합니다. 분석을 시작합니다...")
                    stopRecording()  // 자동 종료
                    break
                }
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

    fun uploadAudioFile(file: File) {
        viewModelScope.launch {
            _uploadState.value = ResultState.Loading
            try {
                val response = audioUploadRepository.uploadWav(file)  // Response<AnalysisResponse>
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        // 서버 응답 구조에 따라 필요한 값 저장
                        val message = responseBody.message
                        val results = responseBody.results
                        val feedback = responseBody.feedback


                        // 필요하다면 ViewModel에 저장
                        serverMessage = message      // 선언되어 있다고 가정
                        uploadResult = results
                        analysisFeedback = feedback  // 선언되어 있다고 가정

                        _uploadState.value = ResultState.Success

                        Log.d("UploadResponse", "message: $message")
                        Log.d("UploadResponse", "results: $results")
                        Log.d("UploadResponse", "feedback: $feedback")
                    } else {
                        _uploadState.value = ResultState.Error("서버 응답이 비어 있습니다.")
                    }
                } else {
                    _uploadState.value = ResultState.Error("오류: ${response.code()}")
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

    private fun showToast(context: Context, text: String){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}

sealed class ResultState {
    object Idle : ResultState()
    object Loading : ResultState()
    object Success : ResultState()
    data class Error(val message: String) : ResultState()
}