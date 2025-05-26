package com.haedal.interviewhelper.domain.helpfunction

import kotlin.math.cos
import kotlin.math.sin

object FFTUtils {
    fun fft(real: FloatArray, imag: FloatArray) {
        val n = real.size
        if (n == 0 || (n and (n - 1)) != 0)
            throw IllegalArgumentException("FFT input size must be power of 2")

        val logN = Integer.numberOfTrailingZeros(n)

        // Bit-reversal permutation
        for (i in 0 until n) {
            val j = Integer.reverse(i).ushr(32 - logN)
            if (j > i) {
                real[i] = real[j].also { real[j] = real[i] }
                imag[i] = imag[j].also { imag[j] = imag[i] }
            }
        }

        // Cooley-Tukey FFT
        var size = 2
        while (size <= n) {
            val halfSize = size / 2
            val tableStep = n / size
            for (i in 0 until n step size) {
                for (j in 0 until halfSize) {
                    val k = j * tableStep
                    val angle = -2.0 * Math.PI * k / n
                    val cos = cos(angle).toFloat()
                    val sin = sin(angle).toFloat()

                    val tReal = cos * real[i + j + halfSize] - sin * imag[i + j + halfSize]
                    val tImag = sin * real[i + j + halfSize] + cos * imag[i + j + halfSize]

                    val uReal = real[i + j]
                    val uImag = imag[i + j]

                    real[i + j] = uReal + tReal
                    imag[i + j] = uImag + tImag
                    real[i + j + halfSize] = uReal - tReal
                    imag[i + j + halfSize] = uImag - tImag
                }
            }
            size *= 2
        }
    }
}
