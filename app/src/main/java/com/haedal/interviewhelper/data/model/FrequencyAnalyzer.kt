package com.haedal.interviewhelper.data.model

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import com.haedal.interviewhelper.domain.helpfunction.FFTUtils
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

class FrequencyAnalyzer {

    private var isRecording = false
    private var audioRecord: AudioRecord? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun start(onResult: (List<Float>) -> Unit) {
        val sampleRate = 44100
        val bufferSize = 1024
        val minBuffer = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            maxOf(minBuffer, bufferSize * 2)
        )
        val buffer = ShortArray(bufferSize)
        val window = HannWindow(bufferSize)

        isRecording = true
        audioRecord?.startRecording()

        Thread {
            while (isRecording) {
                val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                if (read > 0) {
                    val floatBuffer = FloatArray(read) { buffer[it] / 32768f * window[it] }
                    val result = fftMagnitude(floatBuffer)
                    onResult(result)
                    Thread.sleep(33) // ≒ 30fps
                }
            }
        }.start()
    }

    fun stop() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    private fun fftMagnitude(input: FloatArray): List<Float> {
        val fftSize = input.size
        val real = input.copyOf()
        val imag = FloatArray(fftSize) { 0f }

        // Cooley-Tukey FFT (간단 구현 또는 라이브러리 사용 가능)
        FFTUtils.fft(real, imag)

        return real.indices.take(32).map { i ->
            val mag = sqrt(real[i].pow(2) + imag[i].pow(2))
            20 * log10(mag + 1e-6f) // 로그 스케일 dB
        }
    }

    private fun HannWindow(size: Int): FloatArray {
        return FloatArray(size) { i -> (0.5 - 0.5 * cos(2 * PI * i / size)).toFloat() }
    }
}
