package com.haedal.interviewhelper.data.repository

import android.content.Context
import android.media.MediaRecorder
import com.haedal.interviewhelper.domain.repository.AudioRecorderRepository
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File

class AudioRecorderRepositoryImpl(
    private val context: Context
) : AudioRecorderRepository {

    private var recorder: MediaRecorder? = null
    private lateinit var outputFile: File

    @RequiresApi(Build.VERSION_CODES.S)
    override fun start(fileName: String): File {
        val dir = File(context.filesDir, "recordings")
        if (!dir.exists()) dir.mkdirs()

        outputFile = File(dir, "$fileName.m4a").apply {
            if (exists()) delete()
        }

        recorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)

            prepare()
            start()
        }

        return outputFile
    }

    override fun stop(): File {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        return outputFile
    }
}
